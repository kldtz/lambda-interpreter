package lambda

import scala.collection.mutable.HashMap
import scala.collection.{AbstractMap, mutable}
import scala.io.Source
import scala.io.StdIn.readLine
import scala.sys.error


class Repl():

  import Repl.*

  private var ctx: mutable.AbstractMap[String, Expression] = HashMap()
  private var symbols: mutable.AbstractMap[Expression, mutable.Set[String]] = HashMap()

  def run(): Unit =
    readSource("stdlib.lambda").foreach(eval)
    while true do
      print("> ")
      val res = readLine() match
        case "exit" => return
        case line => safeEval(line)
      println(s"$res")

  private def safeEval(line: String): String =
    try
      val res = eval(line)
      line match
        case VariablePattern(v) => res.toString()
        case l =>
          symbols.getOrElse(res, List(res.toString)).mkString(", ")
    catch
      case e => e.toString()

  private def eval(line: String): Expression =
    val eval_func: Interpreter.StringEval = if line.endsWith("?") then Interpreter.lazy_eval else Interpreter.eval
    line match
      case AssignmentPattern(v, e) =>
        val value = eval_func(e, ctx)
        ctx.put(v, value)
        symbols.getOrElseUpdate(value, new mutable.HashSet[String]()) += v
        value
      case l => eval_func(l, ctx)


object Repl:
  val AssignmentPattern = raw"([a-zA-Z_\-]+)\s*=\s*(.+)".r
  val VariablePattern = raw"^([a-zA-Z_\-]+)\s*\??$$".r

  def apply(): Repl = new Repl()

  private def readSource(filename: String): Iterator[String] =
    Source.fromResource(filename)
      .getLines()
      .map(l => l.strip())
      .filter(l => !l.isEmpty() && !l.startsWith("#"))
