package io.github.kldtz.lambda

/**
 * Lexer that provides peek() and next() methods to iterate over tokens.
 */
class Lexer(source: String):
  private val tokens = Lexer.tokenize(source)
  private var i = 0

  def peek(): Token = tokens(i)

  def next(): Token =
    if i >= tokens.length then
      Token(Token.Type.EOF, "EOF", i)
    else
      val token = tokens(i)
      i += 1
      token

object Lexer:
  val Whitespace = raw"\s".r

  private def readChar(c: Char, i: Int): Token = c match
    case 'λ' | '\\' => Token(Token.Type.Lambda, c.toString, i)
    case '.' => Token(Token.Type.Dot, c.toString, i)
    case '(' => Token(Token.Type.LPar, c.toString, i)
    case ')' => Token(Token.Type.RPar, c.toString, i)
    case c => Token(Token.Type.Ident, c.toString, i)

  private def isNotWhitespace(c: Char, @annotation.unused i: Int): Boolean =
    !Whitespace.matches(c.toString)

  /**
   * Splits source string into tokens.
   */
  def tokenize(source: String): Seq[Token] =
    source.zipWithIndex.filter(isNotWhitespace).map(readChar)

