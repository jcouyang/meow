package meow
package data

import scala.annotation.targetName

case class NonEmpty[A](head: A, tail: List[A])
  derives Show, Functor

object NonEmpty:
  extension [A] (a: A)
    @targetName("cons")
    infix def :|(tail: List[A]): NonEmpty[A] =
      NonEmpty(a, tail)
