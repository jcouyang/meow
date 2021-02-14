package meow
package data

import scala.annotation.targetName
import Function._

trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]
  extension [A, B](fa: F[A])
    infix def map(f: A => B): F[B] = fmap(f)(fa)

    @targetName("mapFlipped")
    def <#>(f: A => B): F[B] = fmap(f)(fa)

    @targetName("voidLeft")
    infix def `$>`(a: B): F[B] = fmap(const[B, A](a))(fa)
    def void: F[Unit] = fmap(const[Unit, A](()))(fa)

end Functor

object Functor:
  def map[F[_]](using Functor[F]) = [A, B] => (f: A => B) => (fa: F[A]) => fa.map(f)

  extension [F[_], A, B](f: A => B)
    @targetName("fmap")
    def `<$>`(fa: F[A])(using Functor[F]): F[B] = fa map f

  extension [F[_], A, B](a: A)
    @targetName("voidRight")
    def `<$`(fb: F[B])(using Functor[F]): F[A] = fb.map(const(a))

  given Functor[Option] with
    def fmap[A, B](f: A => B): Option[A] => Option[B] = (oa: Option[A]) => oa.map(f)

  given [R]: Functor[R => *] with
    def fmap[A, B](f: A => B): (R => A) => (R => B) = (fa: R => A) => f.compose(fa)

  given Functor[List] with
    def fmap[A, B](f: A => B): List[A] => List[B] = (la: List[A]) => la.map(f)

  given [R]: Functor[(R, *)] with
    def fmap[A, B](f: A => B): Tuple2[R, A] => (R, B) = (ta: (R, A)) => (ta._1, f(ta._2))
