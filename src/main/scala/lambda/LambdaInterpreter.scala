package lambda

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.language.postfixOps
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.sys.process.*
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.{Event, HTMLInputElement}

import java.awt.event.KeyEvent


private def generateGraph(dotGraph: String, name: String) =
  Files.write(Paths.get(s"$name.dot"), dotGraph.getBytes(StandardCharsets.UTF_8))
  s"dot $name.dot -Tpng -o $name.png" !

private def generateTree(source: String, name: String): Expression =
  val ast = Parser(Lexer.tokenize(source)).parse()
  val dotGraph = toDot(ast)
  generateGraph(dotGraph, name)
  ast

private def prependDiv(targetNode: dom.Node, text: String): Unit = {
  val div = document.createElement("div")
  div.textContent = text
  targetNode.insertBefore(div, targetNode.firstChild)
}

private def setupUI(repl: Repl): Unit = {
  val input_box = document.getElementById("input").asInstanceOf[dom.html.Div]
  input_box.focus()
  val output = document.getElementById("output").asInstanceOf[dom.html.Div]
  input_box.onkeyup = (e: dom.KeyboardEvent) => {
    // evaluate and print input if Enter key is pressed
    if (e.keyCode == 13) {
      val input = input_box.textContent.trim()
      val res = repl.safeEval(input)
      prependDiv(output, s"> ${input}")
      prependDiv(output, s"$res")
      input_box.textContent = ""
    }
  }
  // Put focus in input box on click anywhere in console
  val console = document.getElementById("console").asInstanceOf[dom.html.Div]
  console.onclick = (e: dom.MouseEvent) => {
    input_box.focus()
  }
}

@main def main() =
  //generateTree(raw"(\x.\y.(\z.z x) a)", "1")
  //val ast = generateTree(raw"(\y.(y \x.x) \x.(zeit x))", "dbi-example")
  //val res = Interpreter.eval(ast)
  //generateGraph(toDot(res), "reduced")

  val repl = Repl()
  setupUI(repl)
