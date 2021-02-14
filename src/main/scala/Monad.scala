package meow
package control

import data._
import scala.annotation.targetName
import Functor._

trait Monad[F[_]](using applicative: Applicative[F]):
  def bind[A, B](f: A => F[B]): F[A] => F[B]

  export applicative.{pure, liftA2}

  extension [A, B](fa: F[A])
    def flatMap(f: A => F[B]): F[B] = bind(f)(fa)

    @targetName("bind")
    infix def >>=(f: A => F[B]): F[B] = bind(f)(fa)
    
    @targetName("dropLeft")
    infix def >>(fb: F[B]): F[B] = fa >>= {(_: A) => fb}

  extension [A, B, C](f: A => F[B])
    @targetName("bindFlipped")
    infix def =<<(ma: F[A]): F[B] = ma >>= f

    @targetName("composeKleisli")
    def >=>(ff: B => F[C]): A => F[C] = (a: A) => f(a) >>= ff

    @targetName("composeKleisliFlipped")
    def <=<(ff: C => F[A]): C => F[B] = (c: C) => ff(c) >>= f

  extension [A](ffa: F[F[A]])
     def flatten = ffa.flatMap(identity)

end Monad


object Monad:
  def flatMap[M[_], A, B] = (M: Monad[M]) ?=> (ma: M[A]) => (f: A => M[B]) => ma >>= f
  def flatten[M[_], A] = (M: Monad[M]) ?=> (mma: M[M[A]]) => mma.flatten

  given Monad[Option] with
    def bind[A, B](f: A => Option[B]): Option[A] => Option[B] = (oa: Option[A]) => oa.flatMap(f)
