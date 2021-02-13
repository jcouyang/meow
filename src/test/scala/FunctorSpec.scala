package meow

import prelude.{given, _}

import munit._
import org.scalacheck.Prop._
import Function._

class FunctorSpec extends munit.ScalaCheckSuite:
  
  property("Identity") {
    forAll { (fa: Option[Int]) =>
      map(identity)(fa) == identity(fa)
    }
  }

  property("Composition") {
    forAll { (fa: Option[Int], f: Int => Int, g: Int => Int) =>
      map(f compose g)(fa) == (map(f) compose map(g))(fa)
    }
  }

  test("voidLeft and voidRight") {
    assertEquals(
      3 `<$` map((x: Int) => x + 1)(Option(1)),
      map((x: Int) => x + 1)(Option(1)) `$>` 3)
  }

  test("void") {
    assertEquals(
      Option(2).void,
      Option(())
    )
  }
end FunctorSpec
