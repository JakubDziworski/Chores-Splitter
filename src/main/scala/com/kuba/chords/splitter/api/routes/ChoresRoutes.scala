package com.kuba.chords.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.chords.splitter.api.routes.dto.JsonSupport
import com.kuba.chords.splitter.service.ChoresService


trait ChoresRoutes extends TaskRoutes with JsonSupport {

  val choresService: ChoresService

  val choresRoutes = pathPrefix("chores") {
    (post & entity(as[AddChoreDto])) { chore =>
      val id = choresService.addChore(chore)
      complete(Created, id)
    } ~ get {
      val list = choresService.getChores
      complete(OK, list)
    } ~ path(IntNumber) { choreId =>
      get {
        val chore = choresService.getChore(ChoreId(choreId))
        complete(chore)
      }
    }
  }
}