package meow
package data

import scala.annotation.targetName
import generic.*
import scala.deriving.*
/*
 * The class of semigroups (types with an associative binary operation).
 * 
 * Instances should satisfy the following:
 * 
 * ## Associativity
 * ```
 * x <> (y <> z) == (x <> y) <> z
 * ```
 * 
 */
trait Semigroup[A]:
  def scombine(x: A, y: A): A
  extension (x: A)
    @targetName("concat")
    infix def <> (y: A): A = scombine(x, y)

  def sconcat(nel: NonEmpty[A]): A = nel match
    case NonEmpty(head, Nil) => head
    case NonEmpty(head, h::tail) => scombine(head, tail.fold(h)(scombine))

trait Monoid[A: Semigroup]:
  def mempty: A

  def mconcat(l: List[A]): A =
    l.fold(mempty)(_ <> _)

object Semigroup:
  def scombine[A](x: A, y: A) = (s: Semigroup[A]) ?=> s.scombine(x,y)

  given [A]: Semigroup[List[A]] with
    def scombine(x: List[A], y: List[A]): List[A] =
      x.concat(y)

  given Semigroup[Int] with
    def scombine(x: Int, y: Int): Int = x + y

  given Semigroup[Unit] with
    def scombine(x: Unit, y: Unit): Unit = ()

  given Semigroup[String] with
    def scombine(x: String, y: String): String = x ++ y

  given [A: Semigroup]: Semigroup[Option[A]] with
    def scombine(x: Option[A], y: Option[A]): Option[A] = (x, y) match
      case (None, y) => y
      case (x, None) => x
      case (Some(x), Some(y)) => Some(x <> y)

  given [A, B: Semigroup]: Semigroup[A => B] with
    def scombine(fx: A => B, fy: A => B): A => B = (a: A) =>
      fx(a) <> fy(a)

  inline given derived[T](using m: Mirror.ProductOf[T]): Semigroup[T] =
    lazy val insts = summonAsList[m.MirroredElemTypes, Semigroup]
    new Semigroup[T] {
      def scombine(a: T, b: T): T =
        val elems = prodIterator(a).zip(prodIterator(b)).zip(insts.iterator).map {
            case ((x, y), inst) => inst.asInstanceOf[Semigroup[Any]].scombine(x, y)
        }
        m.fromProduct(Tuple.fromArray(elems.toArray))
    }
    

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
