package meow

import prelude.{given, _}

import munit._
import scala.concurrent.{Future,Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import data.IO
class AlternativeSpec extends munit.ScalaCheckSuite:
  given ExecutionContext = ExecutionContext.global

  test("syntax") {
    val a = IO.puts("hehe")
    val err = new Exception("asdfsdf")
    val b = throwError[IO, Throwable](err)[Int]
    val c = IO.puts("hoho")
    val d = pure[IO](1)
    val e = a *> b *> c <|> d
    assertEquals(Await.result(e.run, 3.seconds), 1)
  }

end AlternativeSpec
