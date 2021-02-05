package meow
package control

import data._
import scala.annotation.targetName

trait Monad[F[_]] extends Applicative[F]:
  def bind[A, B](f: A => F[B]): F[A] => F[B]
  
  extension [A, B](fa: F[A])
    def flatMap(f: A => F[B]): F[B] = bind(f)(fa)
    
    @targetName("bind")
    infix def >>=(f: A => F[B]): F[B] = bind(f)(fa)
    
    @targetName("sequential compose")
    infix def >>(fb: F[B]): F[B] = fa >>= {(_: A) => fb}
