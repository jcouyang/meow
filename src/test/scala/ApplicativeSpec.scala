package meow

import data.Functor
import control._
import Applicative._
import munit._
import org.scalacheck.Prop._
import Applicative._
import data.Functor._

class ApplicativeSpec extends munit.ScalaCheckSuite:
  given Functor[Option] with
      def fmap[A, B](f: A => B): Option[A] => Option[B] = (oa: Option[A]) => oa.map(f)

  given Applicative[Option] with
    def pure[A](a: A): Option[A] = Option(a)
    def liftA2[A, B, C](f: A => B => C) = (oa: Option[A]) => (ob: Option[B]) =>
      oa match {
        case Some(a) => ob match {
          case Some(b) => Option(f(a)(b))
          case None => None
        }
        case None => None
      }

  property("Identity") {
    forAll { (fa: Option[Int]) =>
      pure(identity) <*> fa == fa
    }
  }

  property("Composition") {
    forAll { (fa: Option[Int => Int], fb: Option[Int=>Int], fc: Option[Int]) =>
      pure((f: Int => Int) => (g: Int => Int) => (c: Int) => f(g(c)) ) <*> fa <*> fb <*> fc == fa <*> (fb <*> fc)
    }
  }

  property("Homomorphism") {
    forAll {(f: String => String, x: String) =>
      pure(f) <*> pure(x) == pure(f(x))
    }
  }

  property("Interchange") {
    forAll {(u: Option[String => String], y: String) =>
      u <*> pure(y) == pure((f: String => String) => f(y)) <*> u
    }
  }
end ApplicativeSpec
