package lambda

import org.scalatest.funsuite.AnyFunSuite

class InterpreterTest extends AnyFunSuite {

  test("((AND TRUE) TRUE) == TRUE") {
    val result = Interpreter.eval(raw"((\p.\q.((p q) p) \a.\b.a) \a.\b.a)")
    assert(result == Interpreter.eval(raw"\a.\b.a"))
  }

  test("((AND TRUE) FALSE) == FALSE") {
    val result = Interpreter.eval(raw"((\p.\q.((p q) p) \a.\b.a) \a.\b.b)")
    assert(result == Interpreter.eval(raw"\a.\b.b"))
  }

  test("(SUCC ZERO) == ONE") {
    val result = Interpreter.eval(raw"(\n.\f.\x.(f ((n f) x)) \f.\x.x)")
    assert(result == Interpreter.eval(raw"\f.\x.(f x)"))
  }

  test("(SUCC ONE) == TWO") {
    val result = Interpreter.eval(raw"(\n.\f.\x.(f ((n f) x)) \f.\x.(f x))")
    assert(result == Interpreter.eval(raw"\f.\x.(f (f x))"))
  }

  test("(PRED TWO) == ONE") {
    val result = Interpreter.eval(raw"(\n.\f.\x.(((n \g.\h.(h (g f))) \u.x) \u.u) " +
      raw"(\n.\f.\x.(f ((n f) x)) (\n.\f.\x.(f ((n f) x)) \f.\x.x)))")
    assert(result == Interpreter.eval(raw"(\n.\f.\x.(f ((n f) x)) \f.\x.x)"))
  }
}
