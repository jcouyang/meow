package meow

import prelude.{given, _}

import munit._
import org.scalacheck.Prop._


class ApplicativeSpec extends munit.ScalaCheckSuite:
  
  def compose[A] = (f: A => A) => (g: A => A) => (c: A) => f(g(c))

  property("Identity") {
    forAll { (fa: Option[Int]) =>
      pure(identity) <*> fa == fa
    }
  }

  property("Composition") {
    forAll { (fa: Option[Int => Int], fb: Option[Int=>Int], fc: Option[Int]) =>
      pure(compose) <*> fa <*> fb <*> fc == fa <*> (fb <*> fc)
    }
  }

  property("Homomorphism") {
    forAll {(f: String => String, x: String) =>
      pure(f) <*> pure(x) == pure(f(x))
    }
  }

  property("Interchange") {
    forAll {(u: Option[String => String], y: String) =>
      u <*> pure(y) == pure((f: String => String) => f(y)) <*> u
    }
  }
end ApplicativeSpec
