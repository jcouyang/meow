package meow
package control

import data._
import scala.annotation.targetName
import scala.concurrent.{Future, ExecutionContext}
import Functor._

/**
The [Monad](https://hackage.haskell.org/package/base-4.14.1.0/docs/Control-Monad.html#t:Monad) trait defines the basic operations over a /monad/,
a concept from a branch of mathematics known as /category theory/.
From the perspective of a Haskell programmer, however, it is best to
think of a monad as an /abstract datatype/ of actions.
Scala's `for` expressions provide a convenient syntax for writing
monadic expressions.

Instances of 'Monad' should satisfy the following:

### Left identity
```scala
(pure[F](a) >>= f) == f(a)
```
### Right identity
```scala
(fa >>= {(x: A) => pure[F](x) }) == fa
```
### Associativity
```scala
(m >>= { (x: A) => k(x) >>= h }) == ((m >>= k) >>= h)
```

Furthermore, the 'Monad' and 'Applicative' operations should relate as follows:

```scala
map[F](f)(xs) == (xs >>= (pure[F] compose f))
```
```scala
>> == *>
```
and that [[meow.control.Applicative#pure]] and [[meow.control.Applicative#<*>]] satisfy the Applicative functor laws.
  */
trait Monad[M[_]:Applicative]:
  /** Sequentially compose two actions, passing any value produced by the first as an argument to the second.
    */
  def bind[A, B](f: A => M[B]): M[A] => M[B]

  extension [A, B](fa: M[A])
    /** alias of [[this.>>=]] for `for` comprehension */
    def flatMap(f: A => M[B]): M[B] = bind(f)(fa)

    /** Sequentially compose two actions, passing any value produced by the first as an argument to the second.
      * @example ```
      * val f = (x:Int) => Option(x +1)
      * Option(1) >>= f == Option(2)
      * ```
      */
    @targetName("bind")
    def >>=(f: A => M[B]): M[B] = bind(f)(fa)
    /** Sequentially compose two actions, discarding any value produced by the first, like sequencing operators (such as the semicolon) in imperative languages.
      * @example ```
      *  Option(1) >> Option(2) == Option(2)
      * ```
      */
    @targetName("dropLeft")
    def >>(fb: M[B]): M[B] = fa >>= {(_: A) => fb}

  extension [A, B, C](f: A => M[B])
    /** Flipped [[#>>=]] */
    @targetName("bindFlipped")
    def =<<(ma: M[A]): M[B] = ma >>= f
    /** Left-to-right composition of Kleisli arrows.
      * @example ```
      * val ff1 = (x:Int) => Option(x +1)
      * val ff2 = (x:Int) => Option(x +2)
      * Option(1) >>= (ff1 >=> ff2) == Option(4)
      * ```
      */
    @targetName("composeKleisli")
    def >=>(ff: B => M[C]): A => M[C] = (a: A) => f(a) >>= ff
    /** Right-to-left composition of Kleisli arrows.
      * @example ```
      * val ff1 = (x:Int) => Option(x +1)
      * val ff2 = (x:Int) => Option(x +2)
      * (ff2 <=< ff1) =<< Option(1) == Option(4)
      * ```
      */
    @targetName("composeKleisliFlipped")
    def <=<(ff: C => M[A]): C => M[B] = (c: C) => ff(c) >>= f

  extension [A](ffa: M[M[A]])
     /** Flatten a nested Monad
       * @example
       * ```
       * Option(Option(1)).flatten == Option(1)
       * ```
       */
     def flatten = ffa.flatMap(identity)

end Monad

/** Instances and static functions for [[meow.control.Monad]] */
object Monad:
  /** Static version of [[meow.control.Monad#flatMap]] */
  def flatMap[M[_]] = [A, B] => (f: A => M[B]) => (M: Monad[M]) ?=> (ma: M[A]) => M.bind(f)(ma)
  /** Static version of [[meow.control.Monad#flatten]] */
  def flatten[M[_], A] = (mma: M[M[A]]) => (M: Monad[M]) ?=> mma.flatten

  given Monad[Option] with
    def bind[A, B](f: A => Option[B]): Option[A] => Option[B] = (oa: Option[A]) => oa.flatMap(f)

  given Monad[List] with
    def bind[A, B](f: A => List[B]): List[A] => List[B] = (oa: List[A]) => oa.flatMap(f)

  given [E]: Monad[Either[E, *]] with
    def bind[A, B](f: A => Either[E, B]): Either[E, A] => Either[E, B] = (oa: Either[E, A]) => oa.flatMap(f)
