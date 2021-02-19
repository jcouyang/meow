package meow
package control

import data._
import scala.annotation.targetName
import Functor._

trait Alternative[F[_]](using applicative: Applicative[F]):
  def empty[A]: F[A]
  
  //export applicative.{pure, liftA2}

  extension [A, B](fa: F[A])

    @targetName("alt")
    def <|>(fb: F[A]): F[A]
    
    def some: F[List[A]] = manyV[A](fa)
    def many: F[List[A]] = someV[A](fa)

  private def manyV[A](v: F[A]): F[List[A]] = someV[A](v) <|> applicative.pure(List[A]())
  private def someV[A](v: F[A]): F[List[A]] = applicative.liftA2((x: A) => (xs: List[A]) => x::xs)(v)(manyV(v))

end Alternative


// object Alternative:
