# Lambda Calculus Interpreter

Work-in-progress toy implementation of a lambda calculus interpreter to explore Scala.

## Installation

* [Install JDK and sbt](https://www.scala-sbt.org/1.x/docs/Setup.html)

## Usage
* Execute `sbt run` in project root to start the REPL

### Grammar

The parser only understands the following simple grammar (parentheses around applications, nowhere else), make sure to use it.

```
<exp> ::= <var>
       |  λ<var>.<exp>
       |  (<exp> <exp>)
```
For convenience of typing, you can use a backslash instead of λ.

### REPL

Type an expression and it will be evaluated in normal order. The result is displayed using De Bruijn indices.
```
> (\x.x \x.x)
λ.1
```
Evaluation results can be stored in global variables. Whenever there is a name for an expression, it is printed in the REPL instead of the lambda expression. To get the lambda expression, just type the name.

```
> id = \x.x
id
> id
λ.1
```

A few useful global variables are loaded by default on startup, see [stdlib.lambda](src/main/resources/stdlib.lambda).
```
> TRUE
λ.λ.2
> FALSE
λ.λ.1
> (SUCC ZERO)
ONE
> ONE
λ.λ.(2 1)
```

The same expression can have several names and all of them are printed.
```
> ((AND TRUE) FALSE)
NIL, ZERO, FALSE

# Please don't ...
> FALSE = \x.\y.x
TRUE, FALSE
> ((AND TRUE) FALSE)
TRUE, FALSE
```

By default, the interpreter evaluates under abstractions (to normalize as much as possible and get the best variable mapping). For some expressions this results in infinite recursion, for example with the fixed-point combinator Y. In such cases you can switch to a 'lazy' reduction strategy by adding a question mark after the expression. 

```
> Y
java.lang.StackOverflowError
> Y ?
λ.(λ.(2 (1 1)) λ.(2 (1 1)))
```