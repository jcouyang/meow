package meow
package control

import data._
import scala.annotation.targetName
import Functor._

trait Alternative[F[_]:Applicative]:
  def empty[A]: F[A]
  
  extension [A, B](fa: F[A])

    @targetName("alt")
    def <|>(fb: => F[A]): F[A]
    
    def some: F[List[A]] = someV[A](fa)
    def many: F[List[A]] = manyV[A](fa)

  private def manyV[A](v: F[A]): F[List[A]] = someV[A](v) <|> Applicative.pure(List[A]())
  private def someV[A](v: F[A]): F[List[A]] = Applicative.liftA2((x: A) => (xs: List[A]) => x::xs)(v)(manyV(v))

  given Alternative[List] with
    def empty[A]: List[A] = Nil
    extension [A, B](fa: List[A])
      @targetName("alt")
      def <|>(fb: => List[A]): List[A] = fa concat fb


  given Alternative[Vector] with
    def empty[A]: Vector[A] = Vector()
    extension [A, B](fa: Vector[A])
      @targetName("alt")
      def <|>(fb: => Vector[A]): Vector[A] = fa concat fb


  given Alternative[Option] with
    def empty[A]: Option[A] = None
    extension [A, B](fa: Option[A])
      @targetName("alt")
      def <|>(fb: => Option[A]): Option[A] = if fa.isEmpty then fb else fa

end Alternative
// object Alternative:
