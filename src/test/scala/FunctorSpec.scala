package meow

import prelude.{given, _}

import munit._
import org.scalacheck.Prop._
import Function._

class FunctorSpec extends munit.ScalaCheckSuite:
  
  property("Identity") {
    forAll { (fa: Option[Int]) =>
      fa <#> identity == identity(fa)
    }
  }

  property("Composition") {
    forAll { (fa: Option[Int], f: Int => Int, g: Int => Int) =>
      (f compose g) `<$>` fa  == (map[Option](f) compose map[Option](g))(fa)
    }
  }

  test("voidLeft and voidRight") {
    assertEquals(
      Option(1) `$>` 3,
      3 `<$` Option(1)
    )
  }

  test("void") {
    assertEquals(
      Option(2).void,
      Option(())
    )
  }

  test("<#>") {
    assertEquals(
      Option(1) <#> (_ + 1),
      Option(1) map (_ + 1),
    )
  }
end FunctorSpec
