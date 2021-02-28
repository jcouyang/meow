package meow
package control

import data._
/*
```haskell
class Monad m => MonadReader r m | m -> r where
```
*/
trait MonadReader[R, M[_]:Functor:Monad]:
  def ask: M[R]
  def local[A](rr: R => R): M[A] => M[A]
  def reader[A](f: R => A): M[A] = Functor.map(f)(ask)

object MonadReader {

}
