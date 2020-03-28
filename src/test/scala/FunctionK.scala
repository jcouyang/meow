package meow

import munit._
import org.scalacheck.Prop._

type ~>[-F[_],+G[_]] = [A] => F[A] => G[A]

class FunctionK extends munit.FunSuite with ScalaCheckSuite{
  
  val optionToList: Option ~> List = [A] => (a: Option[A]) => a.toList
  val listToVector: List ~> Vector = [A] => (a: List[A]) => a.toVector

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
}