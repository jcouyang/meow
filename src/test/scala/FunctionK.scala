package meow

import munit._
import org.scalacheck.Prop._
import prelude.{~>}

class FunctionK extends munit.FunSuite with ScalaCheckSuite:
  
  val optionToList: Option ~> List = [A] => (a: Option[A]) => a.toList
  val listToVector: List ~> Vector = [A] => (a: List[A]) => a.toVector

  test("rankNTypes") {
    val doNothing = [A] => (a: A) => a
    def rank2[B,C](a: (B, C),
      doSomething: [A] => A => A): (B, C) =
      (doSomething(a._1), doSomething(a._2))
    assertEquals(rank2((1, "2"), doNothing), (1, "2"))
  }

  test("natureTransformation") {
    def tupledOptionToList[B,C](a: (Option[B], Option[C]), fnk: Option ~> List): (List[B], List[C]) =
        (fnk(a._1), fnk(a._2))
      val result = tupledOptionToList((Some(1), Some("2")), optionToList)
        assertEquals(result, (List(1), List("2")))
  }

  property("composition") {   
      forAll { (a: Int) =>
        listToVector(optionToList(Some(a))) == Vector(a)
      }
  }
