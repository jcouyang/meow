package meow
package generic

import scala.deriving.*
import scala.compiletime.*

type Const = [A] =>> [T] =>> A
type Id[X] = X

type K1[F[_]] = Mirror { type MirroredType[X] = F[X] ; type MirroredElemTypes[_] <: Tuple }
type K1Sum[F[_]] = Mirror.Sum { type MirroredType[X] = F[X] ; type MirroredElemTypes[_] <: Tuple }
type K1Product[F[_]] = Mirror.Product { type MirroredType[X] = F[X] ; type MirroredElemTypes[_] <: Tuple }

/*
   * Cannot be a simple `Tuple.Map[Tuple, [Y]=>>Functor[[X]=>>Y]]` since the kind of Y info will be lost
   * helper matching type from [shapeless](https://github.com/milessabin/shapeless/blob/dca7fba1be1d77668b19e63549766666fb116bf9/modules/deriving/src/main/scala/shapeless3/deriving/kinds.scala#L152)
   */
type LiftP[F[_[_]], T <: [X] =>> Tuple] <: Tuple =
  T[Any] match
    case a *: _ => F[[X] =>> Tuple.Head[T[X]]] *: LiftP[F, [X] =>> Tuple.Tail[T[X]]]
    case _ => EmptyTuple

inline def summonKindAsList[T <: Tuple, K[_[_]]]: List[K[[X]=>> Any]] =
  inline erasedValue[T] match
    case _: EmptyTuple => Nil
    case _: (t *: ts) =>
      summonInline[t].asInstanceOf[K[[X]=>>Any]] :: summonKindAsList[ts, K]

inline def summonAsList[T <: Tuple, F[_]]: List[F[Any]] =
  inline erasedValue[T] match
    case _: EmptyTuple => Nil
    case _: (t *: ts) => summonInline[F[t]].asInstanceOf[F[Any]] :: summonAsList[ts, F]

def prodIterator[T](p: T) = p.asInstanceOf[Product].productIterator
