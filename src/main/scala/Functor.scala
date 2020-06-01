package meow
package functor

trait Functor[F[_]] {
  def [A,B] (fb: F[A]).map(f: A => B): F[B]
}
