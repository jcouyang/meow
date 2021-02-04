package meow
package data

import scala.annotation.targetName
import Function._

trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]
  extension [A, B](fa: F[A])
    infix def map(f: A => B): F[B] = fmap(f)(fa)

    // $>
    infix def as(a: B): F[B] = fmap(const[B, A](a))(fa)
end Functor