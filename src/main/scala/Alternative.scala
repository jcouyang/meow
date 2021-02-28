package meow
package control

import data._
import scala.annotation.targetName
import Functor._

trait Alternative[F[_]:Applicative]:
  def empty[A]: F[A]
  
  extension [A, B](fa: F[A])

    @targetName("alt")
    def <|>(fb: F[A]): F[A]
    
    def some: F[List[A]] = manyV[A](fa)
    def many: F[List[A]] = someV[A](fa)

  private def manyV[A](v: F[A]): F[List[A]] = someV[A](v) <|> Applicative.pure(List[A]())
  private def someV[A](v: F[A]): F[List[A]] = Applicative.liftA2((x: A) => (xs: List[A]) => x::xs)(v)(manyV(v))

end Alternative
// object Alternative:
