package io.github.kldtz.lambda

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.language.postfixOps
import scala.sys.process._

def generateTree(source: String, name: String) =
  val ast = Parser(Lexer(source)).parse()
  val dotGraph = toDot(ast)
  Files.write(Paths.get(s"$name.dot"), dotGraph.getBytes(StandardCharsets.UTF_8))
  s"dot $name.dot -Tpng -o $name.png" !


@main def main() =
  generateTree(raw"(\x.\y.(\z.z x) a)", "1")
  generateTree(raw"\z.(\y.(y \x.x) \x.(z x))", "dbi-example")