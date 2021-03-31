package meow
package data

import scala.annotation.targetName
import scala.concurrent.Future
import scala.util.{Success,Failure}
import scala.concurrent.ExecutionContext
import scala.io.StdIn.readLine
import control.{
Applicative,Monad,Alternative
}
import control.mtl.MonadError

opaque type IO[A] = Function0[Future[A]]

object IO:
  def puts(s: String)(using ExecutionContext): IO[Unit] = () => Future(println(s))
  def gets(using ExecutionContext): IO[String] = () => Future(readLine())

  def fromFuture[A](f: Future[A]): IO[A] = () => f
  def toFuture[A](io: IO[A]): Future[A] = io()

  given (using ExecutionContext): Functor[IO] with
    def fmap[A, B](f: A => B): IO[A] => IO[B] = (ea: IO[A]) => () => ea().map(f)

  given (using ExecutionContext): Applicative[IO] with
    def pure[A](a: A): IO[A] = () => Future(a)
    def liftA2[A, B, C](f: A => B => C) = (fa: IO[A]) => (fb: IO[B]) =>
      () => for
        a <- fa()
        b <- fb()
      yield f(a)(b)
  given (using ExecutionContext): Monad[IO] with
    def bind[A, B](f: A => IO[B]): IO[A] => IO[B] = (oa: IO[A]) => () => oa().flatMap(f(_)())

  given (using ExecutionContext) : MonadError[Throwable, IO] with
    def throwError[A](e: Throwable): IO[A] = () => Future.failed[A](e)
    def catchError[A](ma: IO[A]):(Throwable => IO[A]) => IO[A] = f => () => ma().recoverWith({case e=>f(e)()})

  given (using ExecutionContext): Alternative[IO] with
    def empty[A]: IO[A] = () => Future.failed[A](new Exception("empty"))
    extension [A, B](fa: IO[A])
      @targetName("alt")
      def <|>(fb: => IO[A]): IO[A] = () => fa().recoverWith{
        case _ => fb()
      }

