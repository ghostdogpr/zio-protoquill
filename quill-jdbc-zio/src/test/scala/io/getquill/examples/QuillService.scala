package io.getquill.examples

import io.getquill.jdbczio.Quill
import io.getquill._
import zio._
import java.sql.SQLException

object QuillService {
  case class Person(name: String, age: Int)

  case class DataService(quill: Quill[PostgresDialect, Literal]) {
    import quill._
    val people = quote { query[Person] }
    def peopleByName = quote { (name: String) => people.filter(p => p.name == name) }
  }
  case class ApplicationLive(dataService: DataService) {
    import dataService.quill._
    def getPeopleByName(name: String): ZIO[Any, SQLException, List[Person]] = run(dataService.peopleByName(lift(name)))
    def getAllPeople(): ZIO[Any, SQLException, List[Person]] = run(dataService.people)
  }
  object Application {
    def getPeopleByName(name: String) =
      ZIO.serviceWithZIO[ApplicationLive](_.getPeopleByName(name))
    def getAllPeople() =
      ZIO.serviceWithZIO[ApplicationLive](_.getAllPeople())
  }

  object Layers {
    val dataServiceLive = ZLayer.fromFunction(DataService.apply _)
    val applicationLive = ZLayer.fromFunction(ApplicationLive.apply _)
    val dataSourceLive = Quill.DataSource.fromPrefix("testPostgresDB")
    val postgresServiceLive = Quill.PostgresService(Literal).live
  }
}