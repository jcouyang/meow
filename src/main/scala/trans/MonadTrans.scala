package meow.control

trait MonadTrans[T[_[_], _]]:
  def lift[M[_], A](using Monad[M]): M[A] => T[M, A]

object MonadTrans:
  def lift[T[_[_], _], M[_]:Monad, A](ma:M[A])(using mt: MonadTrans[[M[_], A] =>> T[M, A]]) =
    mt.lift[M, A](ma)
