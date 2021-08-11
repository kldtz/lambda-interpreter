package io.github.kldtz.lambda

import scala.io.StdIn.readLine

object Repl:
  def startRepl(): Unit =
    while true do
      print("> ")
      val line = readLine()
      if line.equals("exit") then return
      val ast = Parser(Lexer(line)).parse()
      val res = Interpreter.eval(ast)
      println(s"$res")
