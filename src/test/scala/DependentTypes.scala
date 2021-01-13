package meow

import munit._

// === start https://github.com/lampepfl/dotty/blob/master/tests/run/HLists.scala
sealed trait HList
case class HCons[+HD, +TL](hd: HD, tl: TL) extends HList
case object HNil extends HList
sealed trait Num
case object Zero extends Num
case class Succ[N <: Num](pred: N) extends Num
type HNil = HNil.type
type Zero = Zero.type
trait At[Xs <: HList, N <: Num] {
  type Out
  def at(xs: Xs, n: N): Out
}
implicit def atZero[XZ, Xs <: HList]: At[HCons[XZ, Xs], Zero] { type Out = XZ } =
  new At[HCons[XZ, Xs], Zero] {
    type Out = XZ
    def at(xs: HCons[XZ, Xs], n: Zero) = xs.hd
  }
implicit def atSucc[XX, Xs <: HList, N <: Num](
  implicit ev: At[Xs, N]
): At[HCons[XX, Xs], Succ[N]] { type Out = ev.Out } = new At[HCons[XX, Xs], Succ[N]] {
  type Out = ev.Out
  def at(xs: HCons[XX, Xs], n: Succ[N]): Out = ev.at(xs.tl, n.pred)
}
def at[Xs <: HList, N <: Num](xs: Xs, n: N)(
  implicit ev: At[Xs, N]
): ev.Out = ev.at(xs, n)

// === end
class DependentTypes extends munit.FunSuite {

  test("dependent function") {
    assertEquals(at(HCons(1, HCons("2", HNil)), Succ(Zero)), "2")
    assertEquals(at(HCons("1", HCons(2, HNil)), Succ(Zero)), 2)
  }
}
