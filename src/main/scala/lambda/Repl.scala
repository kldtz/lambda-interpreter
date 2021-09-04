package lambda

import scala.collection.mutable.HashMap
import scala.collection.{AbstractMap, mutable}
import scala.sys.error


class Repl():

  import Repl.*

  private var ctx: mutable.AbstractMap[String, Expression] = HashMap()
  private var symbols: mutable.AbstractMap[Expression, mutable.Set[String]] = HashMap()

  def safeEval(line: String): String =
    try
      line match
        case DelAssignmentPattern(v) => deleteVariableBinding(v)
        case VariablePattern(v) => eval(line).toString()
        case l =>
          val res = eval(line)
          symbols.getOrElse(res, List(res.toString)).mkString(", ")
    catch
      case e => e.toString()

  private def deleteVariableBinding(varName: String): String =
    ctx.remove(varName) match
      case Some(e) =>
        val names = symbols.getOrElse(e, mutable.HashSet()) -= varName
        if names.isEmpty then symbols.remove(e)
        s"Removed variable binding '$varName = $e'"
      case None => s"Variable binding for '$varName' does not exist!"

  private def eval(line: String): Expression =
    val eval_func: Interpreter.StringEval = if line.endsWith("?") then Interpreter.lazy_eval else Interpreter.eval
    line match
      case AssignmentPattern(v, e) =>
        deleteVariableBinding(v)
        val value = eval_func(e, ctx)
        ctx.put(v, value)
        symbols.getOrElseUpdate(value, new mutable.HashSet[String]()) += v
        value
      case l => eval_func(l, ctx)


object Repl:
  val AssignmentPattern = raw"([a-zA-Z_\-]+)\s*=\s*(.+)".r
  val DelAssignmentPattern = raw"([a-zA-Z_\-]+)\s*=\s*$$".r
  val VariablePattern = raw"^([a-zA-Z_\-]+)\s*\??$$".r

  val Source =
    """
      |# Boolean arithmetic
      |# Church Booleans
      |TRUE = \a.\b.a
      |FALSE = \a.\b.b
      |# Logical operators and control structures
      |AND = \p.\q.((p q) p)
      |OR = \p.\q.((p p) q)
      |NOT = \p.\a.\b.((p b) a)
      |IF = \p.\a.\b.((p a) b)
      |
      |# Church numerals
      |ZERO = \f.\x.x
      |SUCC = \n.\f.\x.(f ((n f) x))
      |ISZERO = \n.((n \x.FALSE) TRUE)
      |ONE = (SUCC ZERO)
      |TWO = (SUCC ONE)
      |THREE = (SUCC TWO)
      |PRED = \n.\f.\x.(((n \g.\h.(h (g f))) \u.x) \u.u)
      |
      |# Church pairs
      |CONS = \x.\y.\f.((f x) y)
      |CAR = \l.(l TRUE)
      |CDR = \l.(l FALSE)
      |NIL = FALSE
      |ISNIL = \l.((l \h.\t.\b.FALSE) TRUE)
      |
      |# Combinators
      |Y = \f.(\x.(f (x x)) \x.(f (x x))) ?
    """.stripMargin

  def apply(): Repl =
    var repl = new Repl()
    Source.split("\n")
      .map(l => l.trim())
      .filter(l => !l.isEmpty() && !l.startsWith("#"))
      .foreach(repl.eval)
    repl
