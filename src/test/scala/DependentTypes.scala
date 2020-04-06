package meow

import munit._
import shapeless._

class DependentTypes extends munit.FunSuite {
  trait Second[L <: HList] {
    type Out
    def apply(value: L): Out
  }
  
  object Second {
    def apply[L <: HList](value: L) = (inst: Second[L]) ?=> inst(value)
  }

  test("dependent function") {
    given instanceSecond[A, B, C <: HList] as Second[A::B::C] {
      type Out = B
      def apply(value: A::B::C):Out = value.at(Nat._1)
    }
    assertEquals(Second(1::"2"::HNil), "2")
    assertEquals(Second("1"::2::HNil), 2)
  }
}