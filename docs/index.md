---
title: Meow
layout: main
---

Catless Category Theory in Scala 3, meow ~

# Rationale
The most frustrating thing about using Cats is always such complaints from my teammates:
> Q: What's missing? What should I import?

> A: Oh, if you are trying to use <+>, you should import `cats.syntax.semigroupk._`

> A: Or, if you don't mind it slowing down you IDE you can just `import cats.syntax.all._`

> Q: But it doesn't work, the compiler is not happy and I can't make sense of it.

> A: Oh, you are trying to combineK a List, you need `import cats.instances.list._` as well, since blah blah blah.

> A: Or, if you don't mind you IDE get even slower, just `import cats.implicits._`.

This is extremely not user-friendly, eventhough for lib maintainer it makes perfect sense that you should know `<+>` should be in SemigroupK.
But as a user, why should I care where `<+>` is in the package? I should be able to guess that this `Option[_]` and other `Option[_]` can be glue together with `<+>`, I don't need to remember if it is Semigroup, Semigroupal, SemigroupK or Alternative.

Why can't it be just like Haskell, all I need to know is that I can `<|>` two `Maybe`, I can `>>=` or `<$>` `Maybe` without worrying anything about what to import.

# :heart: for users

Meow is completely new design to be user-friendly, not cat-friendly, by using newest Scala 3 features.

Meow provides idiomatic haskell like typeclasses by using functions over methods, contextual binding over hierarchy.

## Use Typeclasses without worrying about what Typeclasses are

All you have to do is `import prelude.{given, _}` same as importing `Prelude` in Haskell.

> Don't worry, it won't slow down the compiler I promise, no weird class hierarchy and unnecessary syntax lookup.

```scala
import meow.prelude.{given, *}

val fa = pure[Option](1)
val fb: Option[Int] = pure(2)
val f = (x: Int) => (y: Int) => x + y

assertEquals(f `<$>` fa <*> fb, Option(3))
```
## Generic Deriving Typeclasses

With the super powerful of Scala 3 `inline` and `Tuple`, we no longer need shapeless to auto derive typeclasses
instance for data type.

```scala
  case class Inner[A](a: A)
  case class Outer[A](a: A, b: Inner[A], c: List[A], d: Option[String]) derives Functor

  test("derive functor") {
    assertEquals(
      map[Outer]((a: Int) => a + 1)(Outer(1, Inner(2), List(3,4), Option("hehe"))),
      Outer(2, Inner(3), List(4,5), Option("hehe")))
  }
```

Simply just `derives Functor` and your data type will be mappable over `A`.

[More Details...](https://oyanglul.us/meow)

# For lib maintainer

You may want to refresh your Scala 3 knowledge before jump into the source code.

- [Rank-N Types](https://blog.oyanglul.us/scala/dotty/en/rank-n-type)
- [FunctionK](https://blog.oyanglul.us/scala/dotty/en/functionk)
- [GADT](https://blog.oyanglul.us/scala/dotty/en/gadt)
- [Phantom Types](https://blog.oyanglul.us/scala/dotty/en/phantomtype)
- [Dependent Types](https://blog.oyanglul.us/scala/dotty/en/dependent-types)

```
sbt test
```
