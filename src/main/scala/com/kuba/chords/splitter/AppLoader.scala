package com.kuba.chords.splitter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.kuba.chords.splitter.api.routes.Routes
import com.kuba.chords.splitter.service._
import org.flywaydb.core.Flyway

trait AppLoader {
  implicit lazy val system = ActorSystem("my-system")
  implicit lazy val materializer = ActorMaterializer()
  val appConfig = new AppConfig {}
  lazy val db = dbSetup
  private val chService = new ChoresService(db)
  private val uService = new UsersService(db)
  private val tService = new TasksService(db)
  private val pService = new PenaltyService(db)
  lazy val routes = new Routes {
    override val choresService: ChoresService = chService
    override val usersService: UsersService = uService
    override val tasksService: TasksService = tService
    override val penaltiesService : PenaltyService = pService
  }
  lazy val taskDispatcher = new TasksDispatcherService(db,uService,chService,pService,tService)

  def dbSetup() = {
    import slick.jdbc.H2Profile.api._
    val flyway = new Flyway
    flyway.setDataSource("jdbc:h2:mem:chore_splitter;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "SA", "")
    val db = Database.forDataSource(flyway.getDataSource(), None)
    flyway.migrate()
    db
  }
}
