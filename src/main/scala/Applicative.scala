package meow
package control

import data.Functor
import control._
import scala.concurrent.{ExecutionContext,Future}
import scala.annotation.targetName
import Function._
/** [A functor with application](https://hackage.haskell.org/package/base-4.14.1.0/docs/Control-Applicative.html#t:Applicative), providing operations to
  - embed pure expressions ('pure'), and
  - sequence computations and combine their results ('<*>' and 'liftA2').
  
  A minimal complete definition must include implementations of 'pure'
  and 'liftA2'.

  Further, any definition must satisfy the following:
  
  ### Identity
  ```scala
  pure[F](identity) <*> fa == fa
  ```
  ### Composition
  ```scala
  pure[F](compose) <*> fa <*> fb <*> fc == fa <*> (fb <*> fc)
  ```  
  ### Homomorphism
  ```scala
  pure[F](f) <*> pure(x) == (pure[F](f(x)))
  ```  
  ### Interchange
  ```scala
  u <*> pure(y) == pure[F]((f: String => String) => f(y)) <*> u
  ```
  */
trait Applicative[F[_]:Functor]:
  /** Lift a value. 
    * @example ```scala
    * pure[Option](1) == Option(1)
    * (pure(1): Option[Int]) == Option(1)
    * ```
    */
  def pure[A](a: A): F[A]

  /** Lift a binary function to actions.
    * @example ```scala
    * val f = (x: Int) => (y: Int) => x + y
    * val ff = liftA2[Option](f)
    * ff(Option(1), Option(2)) == Option(3)
    * ```
    */
  def liftA2[A, B, C](f: A => B => C): F[A] => F[B] => F[C]

  extension [A, B](fab: F[A => B])
    /** Sequential application.
      * @example ```scala
      * val f = (x: Int) => (y: Int) => x + y
      * f `<$>` Option(1) <*> Option(2) == Option(3)
      * ```
      */
    @targetName("ap")
    infix def <*>(fa: F[A]): F[B] = liftA2(identity[A => B])(fab)(fa)

  extension [A, B](fa: F[A])
    /** Sequence actions, discarding the value of the first argument.
      * @example ```scala
      * Option(1) *> Option(2) <* Option(3) == Option(2)
      * ```
      */
    @targetName("productRight")
    infix def *>(fb: F[B]): F[B] =  fa.`$>`(identity[B]) <*> fb
    /** Sequence actions, discarding the value of the second argument.
      * @example ```scala
      * Option(1) *> Option(2) <* Option(3) == Option(2)
      * ```
      */
    @targetName("productLeft")
    infix def <*(fb: F[B]): F[A] = liftA2(const[A, B])(fa)(fb)

    /** Flipped version of [[this.ap]] */
    @targetName("apFlipped")
    infix def <**>(fab: F[A => B]): F[B] = liftA2((a: A) => (f: A => B) => f(a))(fa)(fab)

    /** Conditional execution of 'Applicative' expressions.
      * @example {{{ doSomething.when(cond) }}}
      */
    inline def when(cond: Boolean): F[Unit] = inline if cond then fa.void else pure(())
    /** Conditional execution of 'Applicative' expressions.
      * @example {{{ doSomething.unless(condFailed) }}}
      */
    inline def unless(cond: Boolean): F[Unit] = fa.when(!cond)

end Applicative

object Applicative:
  /** Static version of [[meow.control.Applicative#pure]] */
  def pure[F[_]] =
    [A] => (a: A) => (A: Applicative[F]) ?=> A.pure(a)
  /** Static version of [[meow.control.Applicative#liftA2]] */
  def liftA2[F[_]] = [A, B, C] => (f: A => B => C) => (A: Applicative[F]) ?=> A.liftA2(f)
  /** Lift unary function */
  def liftA[F[_]] =
    [A, B] => (f: A => B) => (fa: F[A]) => (A: Applicative[F]) ?=> A.pure[A => B](f) <*> fa
  /** Lift a ternary function */
  def liftA3[F[_]] = [A, B, C, D] => (f: A => B => C => D) =>
    (fa: F[A]) => (fb: F[B]) => (fc: F[C]) => (A: Applicative[F]) ?=> A.liftA2(f)(fa)(fb) <*> fc
  /** Static version of [[meow.control.Applicative#when]] */
  inline def when[F[_]] = (cond: Boolean) => [A] => (doThing: F[A]) =>
    (A: Applicative[F]) ?=> doThing.when(cond)
  /** Static version of [[meow.control.Applicative#unless]] */
  inline def unless[F[_]] = (cond: Boolean) => [A] => (doThing: F[A]) =>
    (A: Applicative[F]) ?=> doThing.unless(cond)

  given Applicative[List] with
    def pure[A](a: A): List[A] = List(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: List[A]) => (fb: List[B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)

  given Applicative[Vector] with
    def pure[A](a: A): Vector[A] = Vector(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: Vector[A]) => (fb: Vector[B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)

  given [R]: Applicative[R => *] with
    def pure[A](a:A): R => A = (r: R) => a
    def liftA2[A, B, C](f: A => B => C) = (fa: R => A) => (fb: R => B) => (r: R) =>
      f(fa(r))(fb(r))

  given Applicative[Option] with
    def pure[A](a: A): Option[A] = Option(a)
    def liftA2[A, B, C](f: A => B => C) = (oa: Option[A]) => (ob: Option[B]) =>
      oa match
        case Some(a) => ob match
          case Some(b) => Option(f(a)(b))
          case None => None
        case None => None

  given [E]: Applicative[Either[E, *]] with
    def pure[A](a: A): Either[E, A] = Right(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: Either[E, A]) => (fb: Either[E, B]) =>
      for
        a <- fa
        b <- fb
      yield f(a)(b)
end Applicative
