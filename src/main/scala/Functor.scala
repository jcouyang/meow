package meow
package functor

trait Functor[F[?]] {
  def [A,B] (fb: F[A]).map(f: A => B): F[B]
}