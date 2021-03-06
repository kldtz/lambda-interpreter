package lambda

import scala.::
import scala.sys.error

trait Expression

case class Variable(name: Option[String], dbi: Int) extends Expression :
  override def toString: String = dbi.toString

case class Abstraction(body: Expression) extends Expression :
  override def toString: String = s"λ.$body"

case class Application(left: Expression, right: Expression) extends Expression :
  override def toString: String = s"($left $right)"

/**
 * Parser that takes a lexer object and produces an AST.
 */
class Parser(tokens: Iterator[Token]):
  private var lex = tokens.buffered
  private var binders: List[String] = Nil
  private val test = " "

  /**
   * Returns AST or throws syntax error.
   */
  def parse(): Expression =
    val ast = expression()
    if lex.hasNext then
      val n = lex.next()
      error(s"Syntax error: unexpected token ${n.text} at position ${n.start}!")
    else
      ast

  private def expression(): Expression =
    if !lex.hasNext then error("Syntax error: premature EOF!")
    lex.head.typ match
      case Token.Type.Lambda => abstraction()
      case Token.Type.LPar => application()
      case Token.Type.Ident => variable()
      case t => error(s"Unexpected token of type $t at position ${lex.next().start}")

  private def variable(): Variable =
    val text = lex.next().text
    binders.indexOf(text) + 1 match
      case 0 => Variable(Some(text), 0)
      case i => Variable(None, i)

  private def abstraction(): Abstraction =
    val start = lex.next().start
    val v = lex.next()
    binders = v.text :: binders
    val dot = lex.next()
    if dot.typ != Token.Type.Dot then
      error(s"Syntax error in abstraction at position ${dot.start}!")
    val e = expression()
    binders = binders.drop(1)
    Abstraction(e)

  private def application(): Application =
    val start = lex.next().start
    val e1 = expression()
    val e2 = expression()
    val rpar = lex.next()
    if rpar.typ != Token.Type.RPar then
      error(s"Syntax error in application at position ${rpar.start}!")
    Application(e1, e2)

/**
 * Returns dot language representation of an expression.
 */
def toDot(expression: Expression): String =
  "digraph graphname {\nnode [style=filled];\n0 [label=Root];\n"
    + dot(expression, 0, 1).mkString("\n") + "\n}"

private def dot(expression: Expression, parent: Int, id: Int): List[String] = expression match
  case variable: Variable =>
    val label = if variable.dbi == 0 then variable.name else variable.dbi
    s"$id [label=${label}];" :: s"$parent -> $id;" :: Nil
  case abstraction: Abstraction =>
    //val param = dot(abstraction.param, id, id+1)
    val body = dot(abstraction.body, id, id + 1)
    s"$id [label=Abstraction, fillcolor=darkseagreen];" :: s"$parent -> $id" :: body
  case application: Application =>
    val left = dot(application.left, id, id + 1)
    val right = dot(application.right, id, id + left.length / 2 + 1)
    s"$id [label=Application, fillcolor=burlywood];" :: s"$parent -> $id" :: (left ++ right)