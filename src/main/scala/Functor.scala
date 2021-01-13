package meow
package functor

trait Functor[F[_]]:
  extension [A, B](fb: F[A])
    def map(f: A => B): F[B]
