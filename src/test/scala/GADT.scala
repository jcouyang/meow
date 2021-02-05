package meow

import munit._
import org.scalacheck.Prop._
import munit.Clue.generate

class GADT extends FunSuite with ScalaCheckSuite:
 
  test("list/safe head") {
    // https://en.wikibooks.org/wiki/Haskell/GADT
    enum Size:
     case Empty
     case NonEmpty

    enum SafeList[+A, +S <: Size]:
      case Nil extends SafeList[Nothing, Size.Empty.type]
      case Cons(head: A, tail: SafeList[A, Size]) extends SafeList[A, Size.NonEmpty.type]

    import SafeList._

    def safeHead[A](list: SafeList[A, Size.NonEmpty.type]): A = list match
      case SafeList.Cons(head, tail) => head
    
    val list = Cons(1, Nil)
    val tail:SafeList[Int, Size.Empty.type] = Nil

    assertEquals(safeHead(list), 1)
    compileErrors("safeHead(tail)")
  }
