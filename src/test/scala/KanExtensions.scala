package meow.functor

import munit._
import org.scalacheck.Prop._

class KanExtensions extends ScalaCheckSuite {
  given Functor[Option] {
    def [A, B](r: Option[A]).map(f: A => B):Option[B] = r.map(f)
  }

  property("Right Kan") {
    forAll { (input: Option[List[Int]]) =>
      val nat: [A] => Option[List[A]] => Vector[A] = [A] => (a: Option[List[A]]) => a.map(_.toVector).getOrElse(Vector())
      val toRan: [A] => Option[A] => Ran[List, Vector, A] = [A] => (a: Option[A]) => Ran.toRan[Option, List, Vector, A](nat)(a)
      val fromRanToRan = Ran.fromRan[Option, List, Vector, Int](toRan) // since PolyFunction does not has compose function

      fromRanToRan(input) == nat(input)
    }
  }

  property("Left Kan") {
    forAll { (input: Vector[Int]) =>
      val lan: Lan[List, Vector, Int] = Lan.LeftKan((gb: List[String])=> gb.size, Vector("hehe"))
      val nat: [A] => Vector[A] => Option[List[A]] = [A] => (a: Vector[A]) => Some(a.toList)
      val toLan : [B] => Lan[List, Vector, B] => Option[B] =
        [B] => (a: Lan[List, Vector, B]) => Lan.toLan[Option, List, Vector, B](nat)(a)

      Lan.fromLan[Option, List, Vector, Int](toLan)(input) == nat(input)
    }
  }
}