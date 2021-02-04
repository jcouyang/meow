package meow
package control

import data._

import scala.annotation.targetName

trait Monad[F[_]] extends Applicative[F]{
  def flatMap[A, B](f: A => F[B]): F[A] => F[B]
  
  extension [A, B](fa: F[A])
    @targetName("bind")
    infix def >>=(f: A => F[B]): F[B] = flatMap(f)(fa)
    
    @targetName("sequential compose")
    def >>(fb: F[B]): F[B] = fa >>= {(_: A) => fb}
}
