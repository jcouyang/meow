package meow.control

trait MonadTrans[T[_[_], _]]:
  def lift[M[_], A](using Monad[M]): M[A] => T[M, A]

object MonadTrans {
  // def lift[T[_], M[_]](using m: MonadTrans[M]): [A] => M[A] => T[M[A]] = m.lift
}
