package meow

import scala.deriving._
import scala.compiletime.{erasedValue, summonInline,constValue}
import data.Functor
import generic.*

trait Show[A]:
  def show(a: A): String

object Show:

  given Show[Unit] with
    def show(a: Unit) = a.toString

  given Show[Boolean] with
    def show(a: Boolean) = a.toString

  given Show[Int] with
    def show(a: Int) = a.toString()

  given Show[String] with
    def show(a: String) = s"\"$a\""

  given Show[Long] with
    def show(a: Long) = a.toString + "l"

  given Show[Float] with
    def show(a: Float) = a.toString + "f"

  given Show[Double] with
    def show(a: Double) = a.toString() + "d"

  given Show[BigInt] with
    def show(a: BigInt) = a.toString

  given Show[Char] with
    def show(a: Char) = s"'${a.toString}'"

  given [A: Show]: Show[Option[A]] with
    def show(a: Option[A]) = a match
      case Some(v) => s"Some(${summon[Show[A]].show(v)})"
      case None => "None"

  given [A: Show]: Show[List[A]] with
    def show(a: List[A]) = "List(" ++
      a.map(summon[Show[A]].show(_)).mkString(", ") ++ ")"

  given [A: Show]: Show[Vector[A]] with
    def show(a: Vector[A]) = "Vector(" ++
      a.map(summon[Show[A]].show(_)).mkString(", ") ++ ")"

  given [A: Show, B: Show]: Show[Map[A, B]] with
    def show(a: Map[A,B]) = "Map(" ++ a.map{(k,v) => s"${summon[Show[A]].show(k)} -> ${summon[Show[B]].show(v)}"}.mkString(", ") ++ ")"

  given [F[_]] : Show[Functor[F]] with
    def show(a: Functor[F]) = a.getClass.getName

  inline given derived[T](using m: Mirror.Of[T]): Show[T] =
    lazy val showInsts = summonAsList[m.MirroredElemTypes, Show]
    lazy val name = constValue[m.MirroredLabel]
    inline m match
      case s: Mirror.SumOf[T] =>
        showCoproduct(s, showInsts, name)
      case p: Mirror.ProductOf[T] =>
        showProduct(p, showInsts, name)

  private def showCoproduct[T](s: Mirror.SumOf[T], insts: => List[Show[Any]], name: => String): Show[T] =
    new Show[T]:
      def show(a: T): String =
        val ord = s.ordinal(a)
        s"${insts(ord).asInstanceOf[Show[T]].show(a)}: ${name}"

  private def showProduct[T](p: Mirror.ProductOf[T], insts: => List[Show[Any]], name: => String): Show[T] =
    new Show[T]:
      def show(a: T): String =
        val elems = insts.iterator.zip(prodIterator(a)).map { _.show(_) }
        s"${name}(${elems.mkString(", ")})"

  def show[A](a: A)(using s: Show[A]) = s.show(a)
