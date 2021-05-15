package meow

import prelude.{given, _}

import munit._
import org.scalacheck.Prop._
import Function._

class FunctorSpec extends munit.ScalaCheckSuite:
  case class Inner[A](a: A)
  case class Outer[A](a: A, b: Inner[A], c: List[A], d: Option[String]) derives data.Functor

  test("derive functor") {
    assertEquals(
      map[Outer]((a: Int) => a + 1)(Outer(1, Inner(2), List(3,4), Option("hehe"))),
      Outer(2, Inner(3), List(4,5), Option("hehe")))
  }

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
