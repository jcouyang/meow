package meow

import data.Functor
import Functor._
import munit._
import org.scalacheck.Prop._
import Function._

class FunctorSpec extends munit.ScalaCheckSuite:
  given Functor[Option] with
      def fmap[A, B](f: A => B): Option[A] => Option[B] = (oa: Option[A]) => oa.map(f)
  
  property("Identity") {
    forAll { (fa: Option[Int]) =>
      map(identity)(fa) == identity(fa)
    }
  }

  property("Composition") {
    forAll { (fa: Option[Int], rnd1: Int, rnd2: Int) =>
      val f = (a: Int) => a + rnd1/2
      val g = (a: Int) => a + rnd2/2
      map(f compose g)(fa) == (map(f) compose map(g))(fa)
    }
  }

  test("voidLeft and voidRight") {
    assertEquals(
      3 voidRight map((x: Int) => x + 1)(Option(1)),
      map((x: Int) => x + 1)(Option(1)) voidLeft 3)
  }

  test("void") {
    assertEquals(
      Option(2).void,
      Option(())
    )
  }
end FunctorSpec
