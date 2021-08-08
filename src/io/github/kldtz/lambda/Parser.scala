package io.github.kldtz.lambda

import scala.::
import scala.sys.error

trait Expression

case class Variable(name: String, start: Int) extends Expression

case class Abstraction(param: Variable, body: Expression, start: Int) extends Expression

case class Application(left: Expression, right: Expression, start: Int) extends Expression

/**
 * Parser that takes a lexer object and produces an AST.
 */
class Parser(lexer: Lexer):
  private var lex = lexer

  /**
   * Returns AST or throws syntax error.
   */
  def parse(): Expression =
    val ast = expression()
    val eof = lex.next()
    if eof.typ != Token.Type.EOF then
      error(s"Syntax error: unexpected token ${eof.text} at ${eof.start}!")
    else
      ast

  private def expression(): Expression = lex.peek().typ match
    case Token.Type.Lambda => abstraction()
    case Token.Type.LPar => application()
    case Token.Type.Ident => variable()
    case Token.Type.EOF => error("Syntax error: premature EOF!")

  private def variable(): Variable =
    val t = lex.next()
    Variable(t.text, t.start)

  private def abstraction(): Abstraction =
    val start = lex.next().start
    val v = variable()
    val dot = lex.next()
    if dot.typ != Token.Type.Dot then
      error(s"Syntax error in abstraction at position ${dot.start}!")
    val e = expression()
    Abstraction(v, e, start)

  private def application(): Application =
    val start = lex.next().start
    val e1 = expression()
    val e2 = expression()
    val rpar = lex.next()
    if rpar.typ != Token.Type.RPar then
      error(s"Syntax error in application at position ${rpar.start}!")
    Application(e1, e2, start)

/**
 * Returns dot language representation of an expression.
 */
def toDot(expression: Expression): String =
  "digraph graphname {\nnode [style=filled];\n0 [label=Root];\n"
    + dot(expression, 0, 1).mkString("\n") + "\n}"

private def dot(expression: Expression, parent:Int, id: Int): List[String] = expression match
  case variable: Variable => s"$id [label=${variable.name}];" :: s"$parent -> $id;" :: Nil
  case abstraction: Abstraction =>  {
    val param = dot(abstraction.param, id, id+1)
    val body = dot(abstraction.body, id, id+param.length/2 + 1)
    s"$id [label=Abstraction, fillcolor=darkseagreen];" :: s"$parent -> $id" :: (param ++ body)
  }
  case application: Application => {
    val left = dot(application.left, id, id+1)
    val right = dot(application.right, id, id+left.length/2 + 1)
    s"$id [label=Application, fillcolor=burlywood];" :: s"$parent -> $id" :: (left ++ right)
  }