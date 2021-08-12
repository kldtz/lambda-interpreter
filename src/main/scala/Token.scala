package main.scala

case class Token(
                  typ: Token.Type,
                  text: String,
                  start: Int
                )

object Token:
  enum Type:
    case Lambda
    case LPar
    case RPar
    case Dot
    case Ident
