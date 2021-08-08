package io.github.kldtz.lambda

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.language.postfixOps
import scala.sys.process._

@main def main() =
  val ast = Parser(Lexer(raw"(\x.\y.(\z.z x) a)")).parse()
  val dotGraph = toDot(ast)
  Files.write(Paths.get("test.dot"), dotGraph.getBytes(StandardCharsets.UTF_8))
  "dot test.dot -Tpng -o test.png" !