package lambda

import org.scalatest.funsuite.AnyFunSuite

class InterpreterTest extends AnyFunSuite {

  test("Eval ((and true) true) == true") {
    val result = Interpreter.eval(raw"((\p.\q.((p q) p) \a.\b.a) \a.\b.a)")
    result == Interpreter.eval(raw"\a.\b.a")
  }

  test("Eval ((and true) false) == false") {
    val result = Interpreter.eval(raw"((\p.\q.((p q) p) \a.\b.a) \a.\b.b)")
    result == Interpreter.eval(raw"\a.\b.b")
  }

  test( "Eval (SUCC ZERO) == ONE") {
    val result = Interpreter.eval(raw"(\n.\f.\x.(f ((n f) x)) \f.\x.x)")
    result == Interpreter.eval(raw"\f.\x.(f x)")
  }
}
