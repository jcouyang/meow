package meow

object prelude:
  type ~>[-F[_],+G[_]] = [A] => F[A] => G[A]

  export data.Functor.{given,_}
  export control.Applicative.{given,_}
  export control.Monad.{given,_}
  export control.mtl.MonadError.{given,_}

  export control.trans.MonadTrans.{given,_}
  export control.trans.OptionT.{given,_}
  export control.trans.ReaderT.{given,_}
