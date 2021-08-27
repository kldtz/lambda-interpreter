package lambda

import scala.collection.AbstractMap
import scala.collection.mutable.HashMap
import scala.sys.error

object Interpreter:

  def eval(source: String, ctx: AbstractMap[String, Expression] = HashMap()): Expression =
    Interpreter.eval(Parser(Lexer.tokenize(source)).parse(), ctx)

  def eval(e: Expression, ctx: AbstractMap[String, Expression]): Expression =
    eta_reduce(nord_reduce(replUnboundVars(e, ctx)))

  private def substitute(a: Expression, e: Expression, numBinders: Int = 1): Expression = e match {
    case Variable(name, dbi) if dbi == numBinders => a
    case Variable(name, dbi) if dbi > numBinders => Variable(name, dbi-1)
    case Abstraction(body) => Abstraction(substitute(a, body, numBinders + 1))
    case Application(left, right) => Application(substitute(a, left, numBinders),
      substitute(a, right, numBinders))
    case v => v
  }

  private def replUnboundVars(e: Expression, ctx: AbstractMap[String, Expression]): Expression = e match {
    case Variable(Some(name), dbi) => ctx.get(name) match {
      case Some(e) => e
      case None => error(s"Cannot resolve unbound variable '$name'")
    }
    case Abstraction(b) => Abstraction(replUnboundVars(b, ctx))
    case Application(l, r) => Application(replUnboundVars(l, ctx), replUnboundVars(r, ctx))
    case v => v
  }

  // call by name
  private def cbn_reduce(e: Expression): Expression = e match {
    case Application(left, right) => cbn_reduce(left) match {
      case Abstraction(body) => cbn_reduce(substitute(right, body))
      case e => Application(e, right)
    }
    case e => e
  }

  // normal order
  private def nord_reduce(e: Expression): Expression = e match {
    case Abstraction(body) => Abstraction(nord_reduce(body))
    case Application(left, right) => cbn_reduce(left) match {
      case Abstraction(body) => nord_reduce(substitute(right, body))
      case e => Application(nord_reduce(e), nord_reduce(right))
    }
    case v => v
  }

  private def contains_var(e: Expression, numBinders: Int): Boolean = e match {
    case Variable(name, dbi) if dbi == numBinders => true
    case Abstraction(body) => contains_var(body, numBinders + 1)
    case Application(left, right) =>
      contains_var(left, numBinders) || contains_var(right, numBinders)
    case v => false
  }

  private def eta_reduce(e: Expression): Expression = e match {
    case Application(left, right) => Application(eta_reduce(left), eta_reduce(right))
    case Abstraction(Application(Abstraction(b), v: Variable))
      if v.dbi == 1 && !contains_var(b, 2) => Abstraction(eta_reduce(b))
    case Abstraction(body) => Abstraction(eta_reduce(body))
    case v => v
  }
