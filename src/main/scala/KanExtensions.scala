package meow.functor

// http://hackage.haskell.org/package/kan-extensions-5.2
// Right Kan
case class Ran[G[?], H[?], A](run: [B] => (A => G[B]) => H[B])

object Ran {
  // toRan :: Functor k => (forall a. k (g a) -> h a) -> k b -> Ran g h b
  def toRan[K[?], G[?], H[?], B](nat: [A] => K[G[A]] => H[A])(kb: K[B]) (using Functor[K]): Ran[G, H, B] =
    Ran([A]=> (k:B=>G[A]) => nat(kb.map(k)))

  // fromRan :: (forall a. k a -> Ran g h a) -> k (g b) -> h b
  def fromRan[K[?], G[?], H[?], B](kran: [A] => K[A] => Ran[G, H, A])(kgb: K[G[B]]): H[B] =
    kran(kgb).run(identity)
}

given [G[?],H[?]] as Functor[[A] =>> Ran[G, H, A]] {
  def [A, B](r: Ran[G, H, A]).map(f: A => B):Ran[G,H,B] =
    Ran([C] => (k:B=>G[C]) => r.run(k.compose(f)))
}