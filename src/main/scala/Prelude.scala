package meow

object prelude:
  type ~>[-F[_],+G[_]] = [A] => F[A] => G[A]

  export data.Functor.{derived => _, given,*}
  export control.Applicative.{given,*}
  export control.Monad.{given,*}
  export control.mtl.MonadError.{given,*}

  export control.trans.MonadTrans.{given,*}
  export control.trans.OptionT.{given,*}
  export control.trans.ReaderT.{given,*}

  export data.IO.{given,*}
  export Show.{show}
  export data.Semigroup.{derived => _, given,*}
  export data.Monoid.{derived => _, given,*}
