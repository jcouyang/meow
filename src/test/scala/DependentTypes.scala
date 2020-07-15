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

  object EqSecond {
    def apply[L <: HList](value: L)(using inst: Second[L])(find: inst.Out) = inst(value) == find
  }

  test("dependent function") {
    given instanceSecond[A, B, C <: HList] as Second[A::B::C] {
      type Out = B
      def apply(value: A::B::C):Out = value.at(Nat._1)
    }
    assertEquals(Second(1::"2"::HNil), "2")
    assertEquals(Second("1"::2::HNil), 2)
    compileErrors(
      """EqSecond("1" :: "3" :: HNil)(3)"""
    )
  }
}
