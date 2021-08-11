package io.github.kldtz.lambda

object Interpreter {

  private def substitute(a: Abstraction, e: Expression, depth: Int = 1): Expression = e match {
    case Variable(name, dbi) if dbi == depth => a
    case Abstraction(body) => Abstraction(substitute(a, body, depth + 1))
    case Application(left, right) => Application(substitute(a, left, depth),
      substitute(a, right, depth))
    case v => v
  }

  def eval(e: Expression): Expression = e match {
    case Application(left, right) => (left, right) match {
      case (Abstraction(b), right: Abstraction) => eval(substitute(right, b))
      case (Abstraction(b), right) => Application(left, eval(right))
      case (left, right) => Application(eval(left), right)
    }
    case e => e
  }
}
