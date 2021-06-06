package meowspec

import meow.prelude.{given, _}

import munit._
import org.scalacheck.Prop._


class MonadSpec extends munit.ScalaCheckSuite:

  property("Identity") {
    forAll { (a: Int, f: Int => Option[Int], fa: Option[Int]) =>
      (pure[Option](a) >>= f) == f(a)
      (fa >>= {(x: Int) => pure[Option](x) }) == fa
    }
  }

  property("Associativity") {
    forAll { (m: Option[Int], k: Int => Option[Int], h: Int => Option[Int]) =>
      (m >>= { (x: Int) => k(x) >>= h }) == ((m >>= k) >>= h)
    }
  }

  test("syntax") {
    val fa = pure[Option](1)
    val fb: Option[Int] = pure(2)
    val fc: Option[Int] = pure(3)

    val ff1 = (x:Int) => Option(x +1)
    val ff2 = (x:Int) => Option(x +2)

    assertEquals(fa >>= ff1, Option(2))
    assertEquals(ff1 =<< fa, Option(2))
    assertEquals(fa >> fb, Option(2))
    assertEquals(fa >>= (ff1 >=> ff2), Option(4))
    assertEquals((ff2 <=< ff1) =<< fa, Option(4))
    assertEquals(pure[Option](fa).flatten, Option(1))
    assertEquals(flatten(pure[Option](fa)), Option(1))
  }

end MonadSpec
