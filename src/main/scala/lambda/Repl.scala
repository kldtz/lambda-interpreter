package lambda

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.io.Source
import scala.io.StdIn.readLine
import scala.sys.error

object Repl:
  val Assignment = raw"([a-zA-Z_\-]+)\s*=\s*(.+)".r

  def run(): Unit =
    var ctx: mutable.AbstractMap[String, Expression] = HashMap()
    ctx ++= loadSource("stdlib.txt")
    while true do
      print("> ")
      val res = readLine() match {
        case "exit" => return
        case line => safeEval(line, ctx)
      }
      println(s"$res")

  private def loadSource(filename: String): Iterator[(String, Expression)] =
    Source.fromResource(filename)
      .getLines()
      .filter(l => !l.startsWith("#"))
      .map(readAssignment)

  private def readAssignment(line: String): (String, Expression) = line match {
    case Assignment(v, e) => v -> Interpreter.eval(e)
    case l => error(s"Could not read source line '$l'")
  }

  private def safeEval(line: String, ctx: mutable.AbstractMap[String, Expression]): String =
    try
      eval(line, ctx).toString()
    catch
      case e => e.toString()

  private def eval(line: String, ctx: mutable.AbstractMap[String, Expression]): Expression = line match {
    case Assignment(v, e) =>
      val value = Interpreter.eval(e, ctx)
      ctx.put(v, value)
      value
    case l => Interpreter.eval(l, ctx)
  }
