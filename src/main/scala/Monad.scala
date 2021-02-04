package meow
package monad

import functor._

trait Monad[F[_]] extends Functor[F]{
  def pure[A](a: A): F[A]
  extension [A, B](fb: F[A])
    def >>=(f: A => F[B]): F[B]
}
