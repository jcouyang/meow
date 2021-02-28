---
title: Meow
layout: main
---

Experimental catless Dotty/Scala 3 in Category Theory, meow ~

# Rationale
The most frustration thing about using Cats from my teammates complains are always about
> Q: What is missing? what should I import?

> A: Oh, if you are trying to use <+>, you need to import `cats.syntax.semigroupk._`

> A: Or, if you don't mind it slow down you IDE you can just `import cats.syntax.all._`

> Q: But it doesn't work, compiler is not happy and I can't make any sense out of it.

> A: Oh, you are trying to combineK List, you need `import cats.instances.list._` as well.

> A: Or, if you don't mind you IDE get even slower, just `import cats.implicits._`

This is extremely not friendly to lib user, or cause for lib maintainer it make perfect sense that you should know `<+>` should be in SemigroupK.
But as a user, why should I care where `<+>` lives in the package? I should just able to guess maybe if I need to choose this `Option[_]` and other `Option[_]` so I just want to `<+>` them, I can't remember if it is Semigroup, Semigroupal, SemigroupK or Alternative.

Why can't it be just like Haskell, all I need to know is I can `<|>` two `Maybe`, I can `>>=` or `<$>` `Maybe` without worrying anything about what to import.

# For users

All you have to do is `import prelude.{given, _}` same as importing `Prelude` in Haskell

```scala
import meow.prelude.{given, _}

val fa = pure[Option](1)
val fb: Option[Int] = pure(2)
val f = (x: Int) => (y: Int) => x + y

assertEquals(f `<$>` fa <*> fb, Option(3))
```

Same code in Cats would be like:
```scala
import cats._
import cats.implicits.__

val fa = Applicative[Option].pure(1)
val fb = 2.pure[Option]
val f = (x: Int) => (y: Int) => x + y

assertEquals(fa.map(f).ap(fb), Option(3))
```

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
