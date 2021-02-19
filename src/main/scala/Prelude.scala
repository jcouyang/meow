package meow

object prelude:
  type ~>[-F[_],+G[_]] = [A] => F[A] => G[A]

  export data.Functor.{given,_}
  export control.Applicative.{given,_}
  export control.Monad.{given,_}
  export control.MonadError.{given,_}
