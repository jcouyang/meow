package meow

import munit._

class PhantomTypes extends FunSuite:
 
  test("phantom number at type level") {
    enum Nat:
      case Zero
      case Succ[A]() extends Nat
    import Nat._
    type Nat2 = Succ[Succ[Zero.type]]
    type Nat3 = Succ[Succ[Succ[Zero.type]]]

    enum Vector[N <: Nat, +A]:
      case Cons(head: A, tail: Vector[N, A]) extends Vector[Succ[N], A]
      case Nil extends Vector[Zero.type, Nothing]
    import Vector._
    val vector2: Vector[Nat2, Int] = Cons(1, Cons(2, Nil))
    val vector3: Vector[Nat3, Int] = Cons(1, Cons(2, Cons(3, Nil)))

    compileErrors("val vector2: Vector[Nat2, Int] = Cons(1, Cons(2, Cons(3, Nil)))")
  }
