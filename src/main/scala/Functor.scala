package meow
package data

import scala.annotation.targetName
import scala.concurrent.{ExecutionContext,Future}
import Function._
import scala.deriving._
import scala.compiletime.{erasedValue, summonInline,constValue}

opaque type Identity[A] = A

/**
  * A type `F` is a Functor if it provides a function `fmap` which, given any types `A` and `B`
  * lets you apply any function from ~A => B~ to turn an `F[A]` into an `F[B]`, preserving the
  * structure of `F`. Furthermore `F` needs to adhere to the following:
  *
  * ### Identity
  * ```scala
  * fa <#> identity == identity(fa)
  * ```
  * ### Composition
  * ```scala
  * (f compose g) `<$>` fa  == (map[F](f) compose map[F](g))(fa)
  * ```
  */
trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]
  extension [A, B](fa: F[A])
    infix def map(f: A => B): F[B] = fmap(f)(fa)

    /** Usually [[this.fmap]] takes function first, `mapFlipped` takes data first
      * @example ```scala
      * fa <#> identity == identity(fa)
      * ```
      */
    @targetName("mapFlipped")
    def <#>(f: A => B): F[B] = fmap(f)(fa)

    /** Drop whatever value `A` on left and return `F[B]` on the right
      * @example ```scala
      * Option(1) `$>` 3 == Option(3)
      * ```
      */
    @targetName("voidLeft")
    infix def `$>`(a: B): F[B] = fmap(const[B, A](a))(fa)

    /** Void `F[A]` and return `F[Unit]`
      * @example ```scala
      * Option(2).void == Option(())
      * ```
      */
    def void: F[Unit] = fmap(const[Unit, A](()))(fa)

end Functor

/** Instances and static functions for [[meow.data.Functor]] */
object Functor:
  /** Prefix static version of [[meow.data.Functor#fmap]]
    * @example ```scala
    * map[Option](f)(Option(1))
    * ```
    */
  def map[F[_]](using Functor[F]) = [A, B] => (f: A => B) => (fa: F[A]) => fa.map(f)

  extension [F[_], A, B](f: A => B)
    /** Infix syntax for [[meow.data.Functor#fmap]]
      * @example ```scala
      * (f compose g) `<$>` fa  == (map[Option](f) compose map[Option](g))(fa)
      * ```
      */
    @targetName("fmap")
    def `<$>`(fa: F[A])(using Functor[F]): F[B] = fa map f

  extension [F[_], A, B](a: A)
    /** Void right, pair of [[meow.data.Functor#$>]]
      * @example ```scala
      * 2 `<$` Option(3) == Option(2)
      * ```
      */
    @targetName("voidRight")
    def `<$`(fb: F[B])(using Functor[F]): F[A] = fb.map(const(a))

  given Functor[Option] with
    def fmap[A, B](f: A => B): Option[A] => Option[B] = (oa: Option[A]) => oa.map(f)

  given [R]: Functor[R => *] with
    def fmap[A, B](f: A => B): (R => A) => (R => B) = (fa: R => A) => f.compose(fa)

  given Functor[List] with
    def fmap[A, B](f: A => B): List[A] => List[B] = (la: List[A]) => la.map(f)

  given Functor[Vector] with
    def fmap[A, B](f: A => B): Vector[A] => Vector[B] = (la: Vector[A]) => la.map(f)

  given [R]: Functor[Tuple2[R, *]] with
    def fmap[A, B](f: A => B): Tuple2[R, A] => (R, B) = (ta: (R, A)) => (ta._1, f(ta._2))

  given [E]: Functor[Either[E, *]] with
    def fmap[A, B](f: A => B): Either[E, A] => Either[E, B] = (ea: Either[E, A]) => ea.map(f)

  // summonInline[Functor[[A]=>>List[A]]]

  type Const = [A] =>> [T] =>> A
  type Id = [A] =>> A

  inline given genFunctor[F[_]](using m: Mirror.Of[F[Any]]): Functor[F] =
    val name = constValue[m.MirroredLabel]
    val functors = summonAsList[m.MirroredElemTypes]
    inline m match
      case s: Mirror.SumOf[F[Any]] =>
        functorCoproduct(s, name, functors)
      case p: Mirror.ProductOf[F[Any]] =>
        functorProduct(p, name, functors)

  given Functor[Id] with
    def fmap[A, B](f: A => B): A => B = (a: A) => f(a)

  given [F[_], G[_]](using ff: Functor[F], fg: Functor[G]): Functor[[t] =>> F[G[t]]] with
    def fmap[A, B](f: A => B): F[G[A]] => F[G[B]] = (fga: F[G[A]]) => ff.fmap(ga => fg.fmap(f)(ga))(fga)

  given [T]: Functor[Const[T]] with
    def fmap[A, B](f: A => B): T => T = (ea: T) =>
      println(s"--const---${ea}")
      ea
  inline def derived[F[_]](using m: Mirror.Of[F[Any]])  = genFunctor[F]
  // type IdOrConst[F[[_]] = [T] =>> F[T] match
  //   case F[T] => Functor[Id]
  //   case AnyKind => Functor[Const[T]]

  private inline def summonAsList[T <: Tuple]: List[Functor[[X]=>> Any]] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts) =>
        summonOne[[X]=>> t, Any].asInstanceOf[Functor[[X]=>>Any]] :: summonAsList[ts]

  private inline def summonOne[F[_], A]: Functor[[X] =>> F[X]] =
    summonInline[Functor[F]]
  private def functorCoproduct[F[_]](s: Mirror.SumOf[F[Any]], name: String, functors: List[Functor[[X]=>> Any]]): Functor[F] =
    new Functor[F] {
      def fmap[A, B](f: A => B): F[A] => F[B] = (fa: F[A]) =>
        val ord = s.ordinal(fa.asInstanceOf[F[Any]])
        functors(ord).fmap(f)(fa).asInstanceOf[F[B]]
    }

  private def functorProduct[F[_], T](p: Mirror.ProductOf[F[T]], name: String, functors: List[Functor[[X] =>> Any]]): Functor[F] =
    new Functor[F] {
      def fmap[A, B](f: A => B): F[A] => F[B] = (fa: F[A]) =>
        val mapped = fa.asInstanceOf[Product].productIterator.zip(functors.iterator).map{
          (fa, F) => 
            println(s"-------${fa}---${F}")
            F.fmap(f)(fa)
        }
        p.fromProduct(Tuple.fromArray(mapped.toArray)).asInstanceOf[F[B]]
    }


