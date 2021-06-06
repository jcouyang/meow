package meowspec

import meow.prelude.{given, *}
import meow.data.{Monoid,Semigroup}

class MonoidSpec extends munit.ScalaCheckSuite:
  
  test("scombine Product") {
    case class TestProduct(a: Int, b: String, l: List[Int]) derives Semigroup
    val prod1 = TestProduct(1, "2", List(1))
    val prod2 = TestProduct(2, "3", Nil)

    assertEquals(scombine(prod1,prod2), TestProduct(3, "23", List(1)))
  }

  test("mcombine Product") {
    case class TestProduct(b: String, l: List[Int]) derives Monoid
    val prod1 = TestProduct("2", List(1))
    val prod2 = TestProduct("3", Nil)

    assertEquals(scombine(prod1,prod2), TestProduct("23", List(1)))
  }
