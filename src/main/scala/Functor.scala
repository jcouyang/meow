package meow
package data

import scala.annotation.targetName
import scala.concurrent.{ExecutionContext,Future}
import Function._

case class Identity[A](run: A)
/**
  * A type `F` is a Functor if it provides a function `fmap` which, given any types `A` and `B`
  * lets you apply any function from ~A => B~ to turn an `F[A]` into an `F[B]`, preserving the
  * structure of `F`. Furthermore `F` needs to adhere to the following:
  *
  * ### Identity
  * ```scala
  * fa <#> identity == identity(fa)
  * ```
  * ### Composition
  * ```scala
  * (f compose g) `<$>` fa  == (map[F](f) compose map[F](g))(fa)
  * ```
  */
trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]
  extension [A, B](fa: F[A])
    infix def map(f: A => B): F[B] = fmap(f)(fa)

    /** Usually [[this.fmap]] takes function first, `mapFlipped` takes data first
      * ```scala
      * fa <#> identity == identity(fa)
      * ```
      */
    @targetName("mapFlipped")
    def <#>(f: A => B): F[B] = fmap(f)(fa)

    /** Drop whatever value `A` on left and return `F[B]` on the right
      * ```scala
      * Option(1) `$>` 3 == Option(3)
      * ```
      */
    @targetName("voidLeft")
    infix def `$>`(a: B): F[B] = fmap(const[B, A](a))(fa)

    /** Void `F[A]` and return `F[Unit]`
      * ```scala
      * Option(2).void == Option(())
      * ```
      */
    def void: F[Unit] = fmap(const[Unit, A](()))(fa)

end Functor

/** Instances and static functions for [[meow.data.Functor]] */
object Functor:
  def map[F[_]](using Functor[F]) = [A, B] => (f: A => B) => (fa: F[A]) => fa.map(f)

  extension [F[_], A, B](f: A => B)
    /** Infix syntax for [[meow.data.Functor#fmap]]
      * ```scala
      * (f compose g) `<$>` fa  == (map[Option](f) compose map[Option](g))(fa)
      * ```
      */
    @targetName("fmap")
    def `<$>`(fa: F[A])(using Functor[F]): F[B] = fa map f

  extension [F[_], A, B](a: A)
    /** Void right
      * ```scala
      * 2 `<$` Option(3) == Option(2)
      * ```
      */
    @targetName("voidRight")
    def `<$`(fb: F[B])(using Functor[F]): F[A] = fb.map(const(a))

  given Functor[Option] with
    def fmap[A, B](f: A => B): Option[A] => Option[B] = (oa: Option[A]) => oa.map(f)

  given [R]: Functor[R => *] with
    def fmap[A, B](f: A => B): (R => A) => (R => B) = (fa: R => A) => f.compose(fa)

  given Functor[List] with
    def fmap[A, B](f: A => B): List[A] => List[B] = (la: List[A]) => la.map(f)

  given Functor[Vector] with
    def fmap[A, B](f: A => B): Vector[A] => Vector[B] = (la: Vector[A]) => la.map(f)

  given [R]: Functor[(R, *)] with
    def fmap[A, B](f: A => B): Tuple2[R, A] => (R, B) = (ta: (R, A)) => (ta._1, f(ta._2))

  given [E]: Functor[Either[E, *]] with
    def fmap[A, B](f: A => B): Either[E, A] => Either[E, B] = (ea: Either[E, A]) => ea.map(f)

  given (using ExecutionContext): Functor[Future] with
    def fmap[A, B](f: A => B): Future[A] => Future[B] = (ea: Future[A]) => ea.map(f)
