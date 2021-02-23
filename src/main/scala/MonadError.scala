package meow
package control

import data._
import scala.annotation.targetName
import scala.concurrent.{ExecutionContext,Future}
import Functor._


trait MonadError[E, M[_]](using monad: Monad[M]):
  def throwError[A](e: E): M[A]
  def catchError[A](ma: M[A]): (E => M[A]) => M[A]
  extension [A](ma: M[A])
    def catchError(f: E => M[A]): M[A] = this.catchError[A](ma)(f)

object MonadError:
  def catchError[M[_], E, A](ma: M[A])(using MonadError[E, M]): (E => M[A]) => M[A] = ma.catchError(_)
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

  given (using ExecutionContext) : MonadError[Throwable, Future] with
    def throwError[A](e: Throwable): Future[A] = Future.failed[A](e)
    def catchError[A](ma: Future[A]):(Throwable => Future[A]) => Future[A] = f => ma.recoverWith({case e=>f(e)})
