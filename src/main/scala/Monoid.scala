package meow
package data

import scala.annotation.targetName
import generic.*
import scala.deriving.*

trait Monoid[A: Semigroup]:
  def mempty: A

  def mconcat(l: List[A]): A =
    l.fold(mempty)(_ <> _)

object Monoid:
  def mempty[A](using m: Monoid[A]): A = m.mempty

  given [A]: Monoid[List[A]] with
    def mempty: List[A] = Nil

  given Monoid[Unit] with
    def mempty = ()

  given Monoid[String] with
    def mempty = ""

  given [A: Monoid: Semigroup]: Monoid[Option[A]] with
    def mempty: Option[A] = None

  inline given derived[T](using m: Mirror.ProductOf[T]): Monoid[T] =
    lazy val insts = summonAsList[m.MirroredElemTypes, Monoid]
    new Monoid[T] {
      def mempty: T =
        val elems = insts.iterator.map {
            inst => inst.asInstanceOf[Monoid[Any]].mempty
        }
        m.fromProduct(Tuple.fromArray(elems.toArray))
    }
