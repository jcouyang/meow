import us.oyanglul.dhall.generic._
import org.dhallj.syntax._
import org.dhallj.codec.syntax._
import org.dhallj.codec.Decoder._


case class Config(
  version: String,
  scalaVersion: String,
)
object dhall {
  lazy val config = {
    val Right(decoded) = "./build.dhall".parseExpr.flatMap(_.resolve).flatMap(_.normalize().as[Config])
    decoded
  }
}
