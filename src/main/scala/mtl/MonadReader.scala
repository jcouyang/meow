package meow
package control
package mtl
import data._
/*
The reader monad transformer. Can be used to add environment reading functionality to other monads. 
```haskell
class Monad m => MonadReader r m | m -> r where
```

```haskell
-- The Reader/IO combined monad, where Reader stores a string.
printReaderContent :: ReaderT String IO ()
printReaderContent = do
    content <- ask
    liftIO $ putStrLn ("The Reader Content: " ++ content)

main = do
    runReaderT printReaderContent "Some Content"

```
*/
trait MonadReader[R, M[_]:Functor:Monad]:
  def ask: M[R]
  def local[A](rr: R => R): M[A] => M[A]
  def reader[A](f: R => A): M[A] = Functor.map(f)(ask)
