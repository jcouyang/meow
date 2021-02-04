package meow
package control

import control._

import scala.annotation.targetName
import Function._

trait Applicative[F[_]] extends Functor[F]:
  def pure[A](a: A): F[A]
  def liftA2[A, B, C](f: A => B => C): F[A] => F[B] => F[C]
  
  extension [A, B](fab: F[A => B])
    @targetName("sequential application")
    infix def <*>(fa: F[A]): F[B] = liftA2(identity[A => B])(fab)(fa)
  
  extension [A, B](fa: F[A])
    @targetName("product right")
    infix def *>(fb: F[B]): F[B] =  fa.as(identity[B]) <*> fb

    @targetName("product left")
    infix def <*(fb: F[B]): F[A] = liftA2(const[A, B])(fa)(fb)

    @targetName("reverse application")
    infix def <**>(fab: F[A => B]): F[B] = liftA2((a: A) => (f: A => B) => f(a))(fa)(fab)
    
end Applicative

object Applicative:
  def pure[F[_], A] = (A: Applicative[F]) ?=>
    (a: A) => A.pure(a)
  
  def liftA2[F[_], A, B, C] = (A: Applicative[F]) ?=> (f: A => B => C) => A.liftA2(f)
  
  def liftA[F[_], A, B] = (A: Applicative[F]) ?=> 
    (f: A => B) => (fa: F[A]) => A.pure[A => B](f) <*> fa
    
  def liftA3[F[_], A, B, C, D](f: A => B => C => D)(using A: Applicative[F]): F[A] => F[B] => F[C] => F[D] =
    (fa: F[A]) => (fb: F[B]) => (fc: F[C]) => A.liftA2(f)(fa)(fb) <*> fc
end Applicative