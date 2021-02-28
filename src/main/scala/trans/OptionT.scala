package meow.control
package trans

import meow.data._

opaque type OptionT[M[_], A] = M[Option[A]]

object OptionT:
  given [M[_]](using functorM: Functor[M]): Functor[OptionT[M, *]] with
    def fmap[A, B](f: A => B): OptionT[M, A] => OptionT[M, B] = functorM.fmap(Functor.map[Option](f))

  given [M[_]: Functor](using apM: Applicative[M]): Applicative[OptionT[M, *]] with
    def pure[A](a: A) = apM.pure(Option(a))
    def liftA2[A, B, C](f: A => B => C) = fa => fb => apM.liftA2(Applicative.liftA2[Option](f))(fa)(fb)

  given [M[_]:Applicative:Functor](using monad: Monad[M]): Monad[OptionT[M, *]] with
    def bind[A, B](f: A => OptionT[M, B]): OptionT[M, A] => OptionT[M, B] = (ma: OptionT[M, A]) =>
      monad.flatMap(ma) {
        case Some(v) => f(v)
        case None => Applicative.pure(None)
      }

  given [E, M[_]:Functor:Applicative:Monad](using me: MonadError[E, M]): MonadError[E, OptionT[M, *]] with
    def throwError[A](e: E): OptionT[M, A] = Functor.map(Option.apply[A])(me.throwError[A](e))
    def catchError[A](ma: OptionT[M, A]): (E => OptionT[M, A]) => OptionT[M, A] = f =>
      me.catchError(ma)((e: E) => f(e))

  given [M[_]:Functor:Applicative:Monad]: MonadTrans[OptionT, M] with
    def lift[A](ma: M[A]) = (Functor.map(Option.apply[A])(ma))

  given [R, M[_]:Monad:Applicative:Functor](using readM: MonadReader[R, M]): MonadReader[R, OptionT[M, *]] with
    def ask: OptionT[M, R] = Functor.map(Option.apply[R])(readM.ask)
    def local[A](rr: R => R) = (ma: OptionT[M, A]) => readM.local(rr)(ma)
