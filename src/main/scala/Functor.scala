package meow
package data

import scala.annotation.targetName
import Function._

trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]
  extension [A, B](fa: F[A])
    infix def map(f: A => B): F[B] = fmap(f)(fa)

    @targetName("mapFlipped")
    def <#>(f: A => B): F[B] = fa map f

    @targetName("voidLeft")
    infix def `$>`(a: B): F[B] = fmap(const[B, A](a))(fa)
    def void: F[Unit] = fmap(const[Unit, A](()))(fa)
  extension [A, B](a: A)
    @targetName("voidRight")
    infix def `<$`(fb: F[B]): F[A] = fmap(const[A, B](a))(fb)

  extension [A, B](f: A => B)
    def `<$>`(fa: F[A]): F[B] = fa map f
end Functor

object Functor:
  def map[F[_], A, B] = (F: Functor[F]) ?=> (f: A => B) => (fa: F[A]) => F.fmap(f)(fa)

  given Functor[Option] with
      def fmap[A, B](f: A => B): Option[A] => Option[B] = (oa: Option[A]) => oa.map(f)
