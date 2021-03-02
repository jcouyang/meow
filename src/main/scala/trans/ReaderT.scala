package meow.control
package trans

import meow.data._
import mtl._

opaque type ReaderT[R, M[_], A] = R => M[A]
opaque type Reader[R, A] = ReaderT[R, Identity, A]

object ReaderT:
  def ask[R, M[_]](using apM: Applicative[M]):ReaderT[R, M, R] = (r: R) => apM.pure(r)

  given [R, M[_]](using functorM: Functor[M]): Functor[ReaderT[R, M, *]] with
    def fmap[A, B](f: A => B): ReaderT[R, M, A] => ReaderT[R, M, B] = (fa: ReaderT[R, M, A]) => functorM.fmap[A, B](f) compose fa

  given [R, M[_]: Functor](using apM: Applicative[M]): Applicative[ReaderT[R, M, *]] with
    def pure[A](a: A) = apM.pure[A] compose Function.const(a)
    def liftA2[A, B, C](f: A => B => C) = fa => fb => (r: R) => apM.liftA2(f)(fa(r))(fb(r))

  given [R, M[_]:Applicative:Functor](using monad: Monad[M]): Monad[ReaderT[R, M, *]] with
    def bind[A, B](f: A => ReaderT[R, M, B]): ReaderT[R, M, A] => ReaderT[R, M, B] = ma =>
      (r: R) => monad.bind((a: A) => f(a)(r))(ma(r))

  given [M[_]:Functor:Applicative:Monad, R]: MonadTrans[[M[_], A] =>> ReaderT[R, M, A], M] with
    def lift[A](ma: M[A]):ReaderT[R, M, A] = Function.const[M[A], R](ma)

  given [E, R, M[_]:Applicative:Functor:Monad](using me: MonadError[E, M], mt: MonadTrans[[M[_], A] =>> ReaderT[R, M, A], M]): MonadError[E, ReaderT[R, M, *]] with
    def throwError[A](e: E): ReaderT[R,M,A] = mt.lift(me.throwError(e))
    def catchError[A](ma: ReaderT[R, M, A]): (E => ReaderT[R, M, A]) => ReaderT[R, M, A] = f =>
     (r: R) => me.catchError(ma(r))((e: E) => f(e)(r))

  given [R, M[_]: Monad:Applicative:Functor]: MonadReader[R, ReaderT[R, M, *]] with
    def ask = ReaderT.ask[R,M]
    def local[A](rr: R => R) = (ma: ReaderT[R, M, A]) => (r: R) => ma(rr(r))
