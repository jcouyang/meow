package meow.control

import meow.data.Functor

trait MonadTrans[T[_[_], _], M[_]:Functor:Applicative:Monad]:
  def lift[A](ma:M[A]): T[M, A]

object MonadTrans:
  def lift[T[_[_], _], M[_]:Functor:Applicative:Monad, A](ma:M[A])(using mt: MonadTrans[[M[_], A] =>> T[M, A], M]) =
    mt.lift[A](ma)
