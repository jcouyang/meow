package meow
package control

import data._
import scala.annotation.targetName
import scala.concurrent.{Future, ExecutionContext}
import Functor._

trait Monad[F[_]](using applicative: Applicative[F]):
  def bind[A, B](f: A => F[B]): F[A] => F[B]

  export applicative._

  extension [A, B](fa: F[A])
    def flatMap(f: A => F[B]): F[B] = bind(f)(fa)

    @targetName("bind")
    def >>=(f: A => F[B]): F[B] = bind(f)(fa)
    
    @targetName("dropLeft")
    def >>(fb: F[B]): F[B] = fa >>= {(_: A) => fb}

  extension [A, B, C](f: A => F[B])
    @targetName("bindFlipped")
    def =<<(ma: F[A]): F[B] = ma >>= f

    @targetName("composeKleisli")
    def >=>(ff: B => F[C]): A => F[C] = (a: A) => f(a) >>= ff

    @targetName("composeKleisliFlipped")
    def <=<(ff: C => F[A]): C => F[B] = (c: C) => ff(c) >>= f

  extension [A](ffa: F[F[A]])
     def flatten = ffa.flatMap(identity)

end Monad


object Monad:
  def flatMap[M[_]] = [A, B] => (f: A => M[B]) => (ma: M[A]) => (M: Monad[M]) ?=> ma >>= f
  def flatten[M[_], A] = (mma: M[M[A]]) => (M: Monad[M]) ?=> mma.flatten
  def liftM[M[_]](using Monad[M]) = [A, B] => (f: A => B) => (ma: M[A]) =>
    ma.map(f)

  given Monad[Option] with
    def bind[A, B](f: A => Option[B]): Option[A] => Option[B] = (oa: Option[A]) => oa.flatMap(f)

  given Monad[List] with
    def bind[A, B](f: A => List[B]): List[A] => List[B] = (oa: List[A]) => oa.flatMap(f)

  given [E]: Monad[Either[E, *]] with
    def bind[A, B](f: A => Either[E, B]): Either[E, A] => Either[E, B] = (oa: Either[E, A]) => oa.flatMap(f)

  given (using ExecutionContext): Monad[Future] with
    def bind[A, B](f: A => Future[B]): Future[A] => Future[B] = (oa: Future[A]) => oa.flatMap(f)
