package lambda

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.io.StdIn.readLine

object Repl:
  val Assignment = raw"([a-zA-Z_\-]+)\s*=\s*(.+)".r

  def run(): Unit =
    var ctx: mutable.AbstractMap[String, Expression] = HashMap()
    while true do
      print("> ")
      val line = readLine()
      val res = line match {
        case "exit" => return
        case line => safeEval(line, ctx)
      }
      println(s"$res")

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
