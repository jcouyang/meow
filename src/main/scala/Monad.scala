package meow
package control

import data._
import scala.annotation.targetName
import Functor._

trait Monad[F[_]](using Applicative[F]) extends Applicative[F]:
  def bind[A, B](f: A => F[B]): F[A] => F[B]

  def pure[A](a: A): F[A] = Applicative.pure(a)
  def liftA2[A, B, C](f: A => B => C): F[A] => F[B] => F[C] = Applicative.liftA2(f)

  extension [A, B](fa: F[A])
    def flatMap(f: A => F[B]): F[B] = bind(f)(fa)
    
    @targetName("bind")
    infix def >>=(f: A => F[B]): F[B] = bind(f)(fa)
    
    @targetName("dropLeft")
    infix def >>(fb: F[B]): F[B] = fa >>= {(_: A) => fb}

  extension [A, B](f: A => F[B])
    @targetName("flippedFlatMap")
    infix def =<<(ma: F[A]): F[B] = ma >>= f
