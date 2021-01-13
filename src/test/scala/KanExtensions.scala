package meow.functor

import munit._
import org.scalacheck.Prop._

class KanExtensions extends FunSuite with ScalaCheckSuite {
  given Functor[Option] with
    extension [A, B](r: Option[A]) def map(f: A => B):Option[B] = r.map(f)

  property("Right Kan: fromRan . toRan") {
    forAll { (input: Option[List[Int]]) =>
      val nat: [A] => Option[List[A]] => Vector[A] = [A] => (a: Option[List[A]]) => a.map(_.toVector).getOrElse(Vector())
      val toRan: [A] => Option[A] => Ran[List, Vector, A] = [A] => (a: Option[A]) => Ran.toRan[Option, List, Vector, A](nat)(a)
      val fromRanToRan = Ran.fromRan[Option, List, Vector, Int](toRan) // since PolyFunction does not has compose function

      fromRanToRan(input) == nat(input)
    }
  }

  property("Right Kan: toRan . fromRan") {
    forAll { (input: Option[String], input2: List[String]) =>
      val nat: [A] => Option[List[A]] => Vector[A] = [A] => (a: Option[List[A]]) => a.toList.flatten.toVector
      val kran = [A] => (a: Option[A]) => Ran[List, Vector, A]([B] => (f:A => List[B])=> a.toList.flatMap(f).toVector)
      val fromRan: [A] => Option[List[A]] => Vector[A] = [A] => (a:Option[List[A]]) => Ran.fromRan[Option, List, Vector, A](kran)(a)
      val a = kran(input)
      val b = Ran.toRan[Option, List, Vector, String](fromRan)(input)
      a.run((s:String) => input2) == b.run((s: String) => input2)
    }
  }

  property("Left Kan: fromLan . toLan") {
    forAll { (input: Vector[Int]) =>
      val nat: [A] => Vector[A] => Option[List[A]] = [A] => (a: Vector[A]) => Some(a.toList)
      val toLan : [B] => Lan[List, Vector, B] => Option[B] =
        [B] => (a: Lan[List, Vector, B]) => Lan.toLan[Option, List, Vector, B](nat)(a)
      Lan.fromLan[Option, List, Vector, Int](toLan)(input) == nat(input)
    }
  }

  property("Left Kan: toLan . fromLan") {
    forAll { (input: Vector[String]) =>
      val lan: Lan[List, Vector, Int] = Lan.LeftKan((gb: List[String])=> gb.size, input)
      val nat: [A] => Lan[List, Vector, A] => Option[A] = [A] => (lan: Lan[List, Vector, A]) => lan match
        case Lan.LeftKan(f, v) => Option(f(v.toList))
      val fromLan: [A] => Vector[A] => Option[List[A]] =
        [A] => (a:Vector[A]) => Lan.fromLan[Option, List, Vector, A](nat)(a)

      Lan.toLan[Option, List, Vector, Int](fromLan)(lan) == Option(input.size)
    }
  }
}
