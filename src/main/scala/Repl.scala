package main.scala

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.io.StdIn.readLine

object Repl:
  val Assignment = raw"([a-zA-Z_\-]+)\s*=\s*(.+)".r

  def startRepl(): Unit =
    var ctx: mutable.AbstractMap[String, Expression] = HashMap()
    while true do
      print("> ")
      val line = readLine()
      if line.equals("exit") then return
      val res = line match {
        case Assignment(v, e) =>
          val value = Interpreter.eval(Parser(Lexer.tokenize(e)).parse(), ctx)
          ctx.put(v, value)
          value
        case l => Interpreter.eval(Parser(Lexer.tokenize(l)).parse(), ctx)
      }
      println(s"$res")
