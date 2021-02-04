package meow.control

import meow.data._
// http://hackage.haskell.org/package/kan-extensions-5.2
// Right Kan
case class Ran[G[_], H[_], A](run: [B] => (A => G[B]) => H[B])

object Ran:
  // toRan :: Functor k => (forall a. k (g a) -> h a) -> k b -> Ran g h b
  def toRan[K[_], G[_], H[_], B](nat: [A] => K[G[A]] => H[A])(kb: K[B]) (using Functor[K]): Ran[G, H, B] =
    Ran([A]=> (k:B=>G[A]) => nat(kb.map(k)))

  // fromRan :: (forall a. k a -> Ran g h a) -> k (g b) -> h b
  def fromRan[K[_], G[_], H[_], B](kran: [A] => K[A] => Ran[G, H, A])(kgb: K[G[B]]): H[B] =
    kran(kgb).run(identity)
end Ran

// Free Functor
given [G[_],H[_]]: Functor[[A] =>> Ran[G, H, A]] with
  def fmap[A, B](f: A => B) = (r: Ran[G, H, A]) =>
      Ran([C] => (k:B=>G[C]) => r.run(k.compose(f)))

// Free Monad
given [G[_]](using Functor[Ran[G, G, *]]): Monad[Ran[G, G, *]] with
  def pure[A](a: A): Ran[G, G, A] = Ran([C]=>(k:A=>G[C]) => k(a))
  def fmap[A, B](f: A => B) = (r: Ran[G, G, A]) => r.map(f)
  def liftA2[A, B, C](f: A => B => C): Ran[G,G,A] => Ran[G,G,B] => Ran[G,G, C] = (ra: Ran[G,G,A]) => (rb: Ran[G, G, B]) =>
    ???
  def flatMap[A, B](f: A => Ran[G,G,B]): Ran[G,G,A]=>Ran[G,G,B] = (fa: Ran[G, G, A]) =>
      Ran([C] => (k: B => G[C]) => fa.run((a)=> f(a).run(k)))
//data Lan g h a where
//  Lan :: (g b -> a) -> h b -> Lan g h a
enum Lan[G[_], H[_], A]:
  case LeftKan[G[_], H[_], A, B](f: (G[B] => A), v: H[B]) extends Lan[G, H, A]

object Lan:
  // toLan :: Functor f => (forall a. h a -> f (g a)) -> Lan g h b -> f b
  def toLan[K[_], G[_], H[_], B](nat: [A] => H[A] => K[G[A]])(lan: Lan[G, H, B])(using Functor[K]): K[B] =
    lan match
      case Lan.LeftKan(f, v) => nat(v).map(f)

  // fromLan :: (forall a. Lan g h a -> f a) -> h b -> f (g b)
  def fromLan[K[_], G[_], H[_], B](s: [A] => Lan[G, H, A] => K[A])(hb: H[B]): K[G[B]] = s(glan(hb))

  // glan :: h a -> Lan g h (g a)
  def glan[G[_], H[_]]: [A] => H[A] => Lan[G, H, G[A]] =
    [A] => (ha: H[A]) => Lan.LeftKan[G, H, G[A], A](identity, ha)
end Lan

