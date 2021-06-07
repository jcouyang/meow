package meow
package data

import scala.annotation.targetName
import generic.*
import scala.deriving.*

/*
The class of monoids (types with an associative binary operation that has an identity). Instances should satisfy the following:

### Right identity
    x <> mempty = x
### Left identity
    mempty <> x = x
### Associativity
    x <> (y <> z) = (x <> y) <> z (Semigroup law)
### Concatenation
    mconcat = foldr (<>) mempty

The method names refer to the monoid of lists under concatenation, but there are many other instances.

Some types can be viewed as a monoid in more than one way, e.g. both addition and multiplication on numbers. In such cases we often define newtypes and make those instances of Monoid, e.g. Sum and Product.
 */
trait Monoid[A: Semigroup]:
  def mempty: A

  def mconcat(l: List[A]): A =
    l.fold(mempty)(_ <> _)

object Monoid:
  def mempty[A](using m: Monoid[A]): A = m.mempty

  given [A]: Monoid[List[A]] with
    def mempty: List[A] = Nil

  given [A]: Monoid[Vector[A]] with
    def mempty: Vector[A] = Vector()

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
