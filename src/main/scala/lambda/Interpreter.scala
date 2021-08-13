package lambda

import scala.collection.mutable.HashMap
import scala.collection.AbstractMap
import scala.sys.error

object Interpreter:

  def eval(source: String, ctx: AbstractMap[String, Expression] = HashMap()): Expression =
    eval(Interpreter.eval(Parser(Lexer.tokenize(source)).parse(), ctx))

  def eval(e: Expression, ctx: AbstractMap[String, Expression]): Expression =
    eval(replUnboundVars(e, ctx))

  private def substitute(a: Expression, e: Expression, depth: Int = 1): Expression = e match {
    case Variable(name, dbi) if dbi == depth => a
    case Abstraction(body) => Abstraction(substitute(a, body, depth + 1))
    case Application(left, right) => Application(substitute(a, left, depth),
      substitute(a, right, depth))
    case v => v
  }

  private def replUnboundVars(e: Expression, ctx: AbstractMap[String, Expression]): Expression = e match {
    case Variable(name, dbi) if dbi == 0 => ctx.get(name) match {
      case Some(e) => e
      case None => error(s"Cannot resolve unbound variable '$name'")
    }
    case Abstraction(b) => Abstraction(replUnboundVars(b, ctx))
    case Application(l, r) => Application(replUnboundVars(l, ctx), replUnboundVars(r, ctx))
    case v => v
  }

  // call by name
  private def eval(e: Expression): Expression = e match {
    case Application(left, right) => (left, right) match {
      case (Abstraction(b), right: Abstraction) => eval(substitute(right, b))
      case (Abstraction(b), right) => eval(Application(left, eval(right)))
      case (left, right) => eval(Application(eval(left), right))
    }
    case e => e
  }
