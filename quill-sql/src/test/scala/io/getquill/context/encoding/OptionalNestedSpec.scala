package io.getquill.context.encoding

import io.getquill._
import org.scalatest.BeforeAndAfterEach
import io.getquill.context.Context

trait OptionalNestedSpec extends Spec with BeforeAndAfterEach {

  val context: Context[_, _]
  import context._

  object Setup {
    case class Contact(firstName: String, lastName: String, age: Int, addressFk: Int)
  }

  object `1.Optional Inner Product` {
    case class LastNameAge(lastName: String, age: Int)
    case class Contact(firstName: String, opt: Option[LastNameAge], addressFk: Int)

    inline def data = quote { query[Contact] }

    inline def `1.Ex1 - Not null inner product insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', 'Bloggs', 123, 444)".as[Insert[Contact]]
    }
    inline def `1.Ex1 - Not null inner product result` =
      Contact("Joe", Some(LastNameAge("Bloggs", 123)), 444)

    inline def `1.Ex2 - null inner product insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', null, null, null)".as[Insert[Contact]]
    }
    inline def `1.Ex2 - null inner product result` =
      Contact("Joe", None, 0)
  }

  object `2.Optional Inner Product with Optional Leaf` {
    case class Age(age: Option[Int])
    case class LastNameAge(lastName: String, age: Age)
    case class Contact(firstName: String, opt: Option[LastNameAge], addressFk: Int)

    inline def data = quote { query[Contact] }

    inline def `2.Ex1 - not-null insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', 'Bloggs', 123, 444)".as[Insert[Contact]]
    }
    inline def `2.Ex1 - not-null result` =
      Contact("Joe", Some(LastNameAge("Bloggs", Age(Some(123)))), 444)

    inline def `2.Ex2 - Null inner product insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', null, null, 444)".as[Insert[Contact]]
    }
    inline def `2.Ex2 - Null inner product result` =
      Contact("Joe", None, 444)

    inline def `2.Ex3 - Null inner leaf insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', 'Bloggs', null, 444)".as[Insert[Contact]]
    }
    inline def `2.Ex3 - Null inner leaf result` =
      Contact("Joe", Some(LastNameAge("Bloggs", Age(None))), 444)
  }

  object `3.Optional Nested Inner Product` {
    case class Age(age: Int)
    case class LastNameAge(lastName: String, age: Option[Age])
    case class Contact(firstName: String, opt: Option[LastNameAge], addressFk: Int)

    inline def data = quote { query[Contact] }

    inline def `3.Ex1 - Null inner product insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', null, null, 444)".as[Insert[Contact]]
    }
    inline def `3.Ex1 - Null inner product result` =
      Contact("Joe", None, 444)

    inline def `3.Ex2 - Null inner leaf insert` = quote {
      infix"insert into Contact (firstName, lastName, age, addressFk) values ('Joe', 'Bloggs', null, 444)".as[Insert[Contact]]
    }
    inline def `3.Ex2 - Null inner leaf result` =
      Contact("Joe", Some(LastNameAge("Bloggs", None)), 444)
  }
}
