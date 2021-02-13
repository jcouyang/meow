package meow
package data

import scala.annotation.targetName
import Function._

trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]
  extension [A, B](fa: F[A])
    infix def map(f: A => B): F[B] = fmap(f)(fa)

    infix def voidLeft(a: B): F[B] = fmap(const[B, A](a))(fa)
    def void: F[Unit] = fmap(const[Unit, A](()))(fa)
  extension [A, B](a: A)
    infix def voidRight(fb: F[B]): F[A] = fmap(const[A, B](a))(fb)
end Functor

object Functor:
  def map[F[_], A, B] = (F: Functor[F]) ?=> (f: A => B) => (fa: F[A]) => F.fmap(f)(fa)
