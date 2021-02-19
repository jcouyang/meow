package meow
package control

import data.Functor
import control._
import scala.concurrent.{ExecutionContext,Future}
import scala.annotation.targetName
import Function._


trait Applicative[F[_]](using functor: Functor[F]):
  def pure[A](a: A): F[A]
  def liftA2[A, B, C](f: A => B => C): F[A] => F[B] => F[C]

  extension [A, B](fab: F[A => B])
    @targetName("ap")
    infix def <*>(fa: F[A]): F[B] = liftA2(identity[A => B])(fab)(fa)

  extension [A, B](fa: F[A])
    @targetName("productRight")
    infix def *>(fb: F[B]): F[B] =  fa.`$>`(identity[B]) <*> fb

    @targetName("productLeft")
    infix def <*(fb: F[B]): F[A] = liftA2(const[A, B])(fa)(fb)

    @targetName("apFlipped")
    infix def <**>(fab: F[A => B]): F[B] = liftA2((a: A) => (f: A => B) => f(a))(fa)(fab)

    inline def when(cond: Boolean): F[Unit] = inline if cond then fa.void else pure(())
    inline def unless(cond: Boolean): F[Unit] = fa.when(!cond)

end Applicative

object Applicative:
  def pure[F[_]] =
    [A] => (a: A) => (A: Applicative[F]) ?=> A.pure(a)

  def liftA2[F[_]] = [A, B, C] => (f: A => B => C) => (A: Applicative[F]) ?=> A.liftA2(f)
  
  def liftA[F[_]] =
    [A, B] => (f: A => B) => (fa: F[A]) => (A: Applicative[F]) ?=> A.pure[A => B](f) <*> fa
    
  def liftA3[F[_]] = [A, B, C, D] => (f: A => B => C => D) =>
    (fa: F[A]) => (fb: F[B]) => (fc: F[C]) => (A: Applicative[F]) ?=> A.liftA2(f)(fa)(fb) <*> fc

  inline def when[F[_]] = (cond: Boolean) => [A] => (doThing: F[A]) =>
    (A: Applicative[F]) ?=> doThing.when(cond)

  inline def unless[F[_]] = (cond: Boolean) => [A] => (doThing: F[A]) =>
    (A: Applicative[F]) ?=> doThing.unless(cond)

  given Applicative[List] with
    def pure[A](a: A): List[A] = List(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: List[A]) => (fb: List[B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)

  given Applicative[Vector] with
    def pure[A](a: A): Vector[A] = Vector(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: Vector[A]) => (fb: Vector[B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)

  given Applicative[Option] with
    def pure[A](a: A): Option[A] = Option(a)
    def liftA2[A, B, C](f: A => B => C) = (oa: Option[A]) => (ob: Option[B]) =>
      oa match
        case Some(a) => ob match
          case Some(b) => Option(f(a)(b))
          case None => None
        case None => None

  given (using ExecutionContext): Applicative[Future] with
    def pure[A](a: A): Future[A] = Future(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: Future[A]) => (fb: Future[B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)

  given [E]: Applicative[Either[E, *]] with
    def pure[A](a: A): Either[E, A] = Right(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: Either[E, A]) => (fb: Either[E, B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)
end Applicative
