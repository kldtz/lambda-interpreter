package main.scala

import scala.util.matching.Regex.Match

/**
 * Lexer that splits source into tokens.
 */
object Lexer:
  val TokenPattern = raw"λ|\\|\.|\(|\)|[a-zA-Z_\-]+".r

  private def readToken(tokenMatch: Match): Token =
    tokenMatch.group(0) match
    case "λ" | "\\" => Token(Token.Type.Lambda, "λ", tokenMatch.start)
    case "." => Token(Token.Type.Dot, ".", tokenMatch.start)
    case "(" => Token(Token.Type.LPar, "(", tokenMatch.start)
    case ")" => Token(Token.Type.RPar, ")", tokenMatch.start)
    case c => Token(Token.Type.Ident, c, tokenMatch.start)

  /**
   * Splits source string into tokens.
   */
  def tokenize(source: String): Iterator[Token] =
    TokenPattern.findAllMatchIn(source).map(readToken)

