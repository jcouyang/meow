package meowspec

import meow.prelude.{given, _}
import meow.Show
class ShowSpec extends munit.ScalaCheckSuite:
  enum TestSum derives Show:
    case TestProduct(a: Int, b: String)
  val prod = TestSum.TestProduct(1, "2")
  test("show Int") {
    assertEquals(show(1), "1")
  }

  test("show Sum") {
    assertEquals(show(prod), """TestProduct(1, "2"): TestSum""")
  }

  test("show Option") {
    assertEquals(show(Option("2")), """Some("2")""")
  }

  test("show Either") {
    assertEquals(show(Left(prod)), """Left(TestProduct(1, "2"): TestSum)""")
  }

  test("show List") {
    assertEquals(show("1" :: "2":: Nil), """List("1", "2")""")
  }

  test("show Vector") {
    assertEquals(show(Vector(1,2,3)), """Vector(1, 2, 3)""")
  }

  test("show Map") {
    assertEquals(show(Map(1 -> 2, 3 -> 4)), """Map(1 -> 2, 3 -> 4)""")
  }
