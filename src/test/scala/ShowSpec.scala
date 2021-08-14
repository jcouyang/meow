package meowspec

import meow.prelude.{given, *}
import meow.Show
class ShowSpec extends munit.ScalaCheckSuite:
  enum Tree[T] derives Show:
    case Branch(left: Tree[T], right: Tree[T])
    case Leaf(elem: T)

  test("show Int") {
    assertEquals(show(1), "1")
  }

  test("show Option") {
    assertEquals(show(Option("2")), """Some("2")""")
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

  test("show Tree") {
    import Tree.*
    val prod: Tree[String] = Branch(Leaf("l0"), Branch(Leaf("l1"), Leaf("r1")))
    assertEquals(show(prod), """Branch(Leaf("l0"): Tree, Branch(Leaf("l1"): Tree, Leaf("r1"): Tree): Tree): Tree""")
  }
