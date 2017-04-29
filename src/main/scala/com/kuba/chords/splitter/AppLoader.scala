package com.kuba.chords.splitter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.kuba.chords.splitter.api.routes.Routes
import com.kuba.chords.splitter.service.{ChoresService, TasksService, UsersService}
import org.flywaydb.core.Flyway

trait AppLoader {
  implicit lazy val system = ActorSystem("my-system")
  implicit lazy val materializer = ActorMaterializer()
  val appConfig = new AppConfig {}
  lazy val db = dbSetup
  lazy val routes = new Routes {
    override val choresService: ChoresService = new ChoresService(db)
    override val usersService: UsersService = new UsersService(db)
    override val tasksService: TasksService = new TasksService(db)
  }

  def dbSetup() = {
    import slick.jdbc.H2Profile.api._
    val flyway = new Flyway
    flyway.setDataSource("jdbc:h2:mem:chore_splitter;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "SA", "")
    val db = Database.forDataSource(flyway.getDataSource(), None)
    flyway.migrate()
    db
  }
}
