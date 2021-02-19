package meow

import prelude.{given, _}

import munit._
import org.scalacheck.Prop._


class ApplicativeSpec extends munit.ScalaCheckSuite:
  
  def compose[A] = (f: A => A) => (g: A => A) => (c: A) => f(g(c))

  property("Identity") {
    forAll { (fa: Option[Int]) =>
      pure[Option](identity) <*> fa == fa
    }
  }

  property("Composition") {
    forAll { (fa: Option[Int => Int], fb: Option[Int=>Int], fc: Option[Int]) =>
      pure[Option](compose) <*> fa <*> fb <*> fc == fa <*> (fb <*> fc)
    }
  }

  property("Homomorphism") {
    forAll {(f: String => String, x: String) =>
      pure[Option](f) <*> pure(x) == (pure[Option](f(x)))
    }
  }

  property("Interchange") {
    forAll {(u: Option[String => String], y: String) =>
      u <*> pure(y) == pure[Option]((f: String => String) => f(y)) <*> u
    }
  }

  test("syntax") {
    val fa = pure[Option](1)
    val fb: Option[Int] = pure(2)
    val fc: Option[Int] = pure(3)
    val f = (x: Int) => (y: Int) => x + y
    val ff = liftA[Option]((x:Int) => x +1)
    val ff2 = liftA2[Option](f)
    val ff3 = liftA3[Option]((x: Int) => (y: Int) => (z: Int) => x + y + z)

    assertEquals(ff(fa), Option(2))
    assertEquals(ff2(fa)(fb), Option(3))
    assertEquals(ff3(fa)(fb)(fc), Option(6))
    assertEquals(f `<$>` fa <*> fb, Option(3))

  }
end ApplicativeSpec
