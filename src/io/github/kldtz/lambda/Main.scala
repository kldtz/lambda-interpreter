package io.github.kldtz.lambda

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.language.postfixOps
import scala.sys.process.*


def generateGraph(dotGraph: String, name: String) =
  Files.write(Paths.get(s"$name.dot"), dotGraph.getBytes(StandardCharsets.UTF_8))
  s"dot $name.dot -Tpng -o $name.png" !

def generateTree(source: String, name: String): Expression =
  val ast = Parser(Lexer(source)).parse()
  val dotGraph = toDot(ast)
  generateGraph(dotGraph, name)
  ast


@main def main() =
  //generateTree(raw"(\x.\y.(\z.z x) a)", "1")
  //val ast = generateTree(raw"(\y.(y \x.x) \x.(z x))", "dbi-example")
  //val res = Interpreter.eval(ast)
  //generateGraph(toDot(res), "reduced")
  Repl.startRepl()