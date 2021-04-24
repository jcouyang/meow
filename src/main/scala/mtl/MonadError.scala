package meow
package control
package mtl

import data._
import scala.annotation.targetName
import scala.concurrent.{ExecutionContext,Future}
import Functor._

/*
The strategy of combining computations that can throw exceptions by bypassing bound functions from the point an exception is thrown to the point that it is handled.

Is parameterized over the type of error information and the monad type constructor. It is common to use Either String as the monad type constructor for an error monad in which error descriptions take the form of strings. In that case and many other common cases the resulting monad is already defined as an instance of the MonadError class. You can also define your own error type and/or use a monad type constructor other than Either String or Either IOError. In these cases you will have to explicitly define instances of the MonadError class. (If you are using the deprecated Control.Monad.Error or Control.Monad.Trans.Error, you may also have to define an Error instance.)
 */
trait MonadError[E, M[_]](using monad: Monad[M]):
  /* Is used within a monadic computation to begin exception processing. */
  def throwError[A](e: E): M[A]
  /*A handler function to handle previous errors and return to normal execution.
   */
  def catchError[A](ma: M[A]): (E => M[A]) => M[A]
  extension [A](ma: M[A])
    def recover(f: E => M[A]): M[A] = catchError[A](ma)(f)

object MonadError:
  def catchError[M[_], E, A](ma: M[A])(using me:MonadError[E, M]): (E => M[A]) => M[A] = me.catchError(ma)(_)
  def throwError[M[_], E](e: E) = [A] => (m:MonadError[E, M]) ?=> m.throwError[A](e)

  given MonadError[Unit, Option] with
    def throwError[A](e: Unit) = None
    inline def catchError[A](ma: Option[A]) = f => inline ma match
      case None => f(())
      case a => a

  given [E]: MonadError[E, Either[E, *]] with
    def throwError[A](e: E) = Left(e)
    inline def catchError[A](ma: Either[E, A]) = f => inline ma match
      case Left(e) => f(e)
      case a => a
