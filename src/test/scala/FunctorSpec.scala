package meow

import prelude.{given, _}

import munit._
import org.scalacheck.Prop._
import Function._

class FunctorSpec extends munit.ScalaCheckSuite:
  case class Nested[A](a: A)
  case class F[A](a: A, b: Nested[A], c: Option[A], d: List[String]) derives data.Functor

  test("derive functor") {
    assertEquals(map[F]((a: Int) => a + 1)(F(1, Nested(2), Option(3), List("hehe"))), F(2, Nested(3), Option(4), List("hehe")))
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
