package meow

import munit._

class DependentTypes2 extends munit.FunSuite {

  /** idris
  isSingleton : Bool -> Type
  isSingleton True = Nat
  isSingleton False = List Nat
  */
  type IsSingleton[X <: Boolean] = X match {
    case true => Int
    case false => List[Int]
  }

  /**
    sum : (single : Bool) -> isSingleton single -> Nat
    sum True x = x
    sum False [] = 0
    sum False (x :: xs) = x + sum False xs
    */
  def sum(single: Boolean, x: IsSingleton[single.type]): Int = (single, x) match {
    case (true, x: IsSingleton[true]) =>  x
    case (false, Nil) => 0
    case (false, ((a:Int)::(as: IsSingleton[false]))) => {
      sum(false, as) + a
    } 
  }

  test("singleton true should return x") {
    assertEquals(sum(true, 1), 1)
  }

  test("singleton false should return sum of x") {
    assertEquals(sum(false, List(1, 2, 3)), 6)
  }

  // inline Vector
  import scala.compiletime.ops.int
  import scala.compiletime.{S}

  enum Vector[Nat, +A] {
    case Cons[N <: Int, AA](head: AA, tail: Vector[N, AA]) extends Vector[S[N], AA]
    case Nil extends Vector[0, Nothing]
  }
  import Vector._

  import int._
  def combine[N <:Int, M<:Int,A](a: Vector[N, A], b: Vector[M, A]): Vector[N + M, A] =
    (a, b) match {
    case (Nil, b) => b.asInstanceOf[Vector[N+M, A]]
    case (a: Vector[S[n], A], b) => a match {
      case Cons(head: A, tail: Vector[n, A]) =>
        val rest = combine(tail, b).asInstanceOf[Vector[n + M, A]]
        (Cons[n+M, A](head, rest): Vector[S[n+ M], A]).asInstanceOf[Vector[N+M, A]]
      }
  }
  test("vector combine") {
    val v1 = Cons(1,Cons(2, Nil))
    val v2 = Cons(3, Cons(4, Cons(5,Nil)))
    assertEquals(combine(v1, v2), Cons(1,Cons(2, Cons(3, Cons(4, Cons(5,Nil))))))
  }
}
