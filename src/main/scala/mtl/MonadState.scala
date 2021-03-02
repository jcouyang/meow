package meow
package control
package mtl
import data._
/*
MonadState class.

This module is inspired by the paper /Functional Programming with Overloading and Higher-Order Polymorphism/, Mark P Jones (http://web.cecs.pdx.edu/~mpj/) Advanced School of Functional Programming, 1995. 
```haskell
class Monad m => MonadState s m | m -> s where
```
*/
trait MonadState[S,M[_]:Functor:Applicative:Monad]:
  def get: M[S]
  def put[A](s: S): M[Unit]

object MonadState:
  def get[M[_]:Functor:Applicative:Monad] = [S] => (ms: MonadState[S, M]) ?=> ms.get
  def put[M[_]:Functor:Applicative:Monad] = [S] => (ms: MonadState[S, M]) ?=> ms.put
  def modify[M[_]:Monad] = [S] => (f: S => S) => (ms: MonadState[S, M]) ?=> Monad.flatMap(ms.put)(ms.get)
  def gets[M[_]:Functor:Monad] = [S, A] => (f: S => A) => (ms: MonadState[S, M]) ?=> Functor.map(f)(ms.get)
