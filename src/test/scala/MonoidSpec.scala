package meow

import prelude.{given, *}
import data.Semigroup

class MonoidSpec extends munit.ScalaCheckSuite:
  
  test("scombine Product") {
    case class TestProduct(a: Int, b: String) derives Semigroup
    val prod1 = TestProduct(1, "2")
    val prod2 = TestProduct(2, "3")

    assertEquals(scombine(prod1,prod2), TestProduct(3, "23"))
  }
