package meow.functor

// http://hackage.haskell.org/package/kan-extensions-5.2
// Right Kan
case class Ran[G[_], H[_], A](run: [B] => (A => G[B]) => H[B])

object Ran {
  // toRan :: Functor k => (forall a. k (g a) -> h a) -> k b -> Ran g h b
  def toRan[K[_], G[_], H[_], B](nat: [A] => K[G[A]] => H[A])(kb: K[B]) (using Functor[K]): Ran[G, H, B] =
    Ran([A]=> (k:B=>G[A]) => nat(kb.map(k)))

  // fromRan :: (forall a. k a -> Ran g h a) -> k (g b) -> h b
  def fromRan[K[_], G[_], H[_], B](kran: [A] => K[A] => Ran[G, H, A])(kgb: K[G[B]]): H[B] =
    kran(kgb).run(identity)
}

// Free Functor
given [G[_],H[_]] as Functor[[A] =>> Ran[G, H, A]] {
  extension [A, B](r: Ran[G, H, A])
    def map(f: A => B):Ran[G,H,B] =
      Ran([C] => (k:B=>G[C]) => r.run(k.compose(f)))
}

//data Lan g h a where
//  Lan :: (g b -> a) -> h b -> Lan g h a
enum Lan[G[_], H[_], A] {
 case LeftKan[G[_], H[_], A, B](f: (G[B] => A), v: H[B]) extends Lan[G, H, A]
}
object Lan {
  // toLan :: Functor f => (forall a. h a -> f (g a)) -> Lan g h b -> f b
  def toLan[K[_], G[_], H[_], B](nat: [A] => H[A] => K[G[A]])(lan: Lan[G, H, B])(using Functor[K]): K[B] = lan match {
    case Lan.LeftKan(f, v) => nat(v).map(f)
  }

  // fromLan :: (forall a. Lan g h a -> f a) -> h b -> f (g b)
  def fromLan[K[_], G[_], H[_], B](s: [A] => Lan[G, H, A] => K[A])(hb: H[B]): K[G[B]] = s(glan(hb))

  // glan :: h a -> Lan g h (g a)
  def glan[G[_], H[_]]: [A] => H[A] => Lan[G, H, G[A]] = [A] => (ha: H[A]) => Lan.LeftKan[G, H, G[A], A](identity, ha)
}
