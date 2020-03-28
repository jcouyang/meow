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
}