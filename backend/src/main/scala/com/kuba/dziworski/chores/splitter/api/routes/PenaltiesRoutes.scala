package com.kuba.dziworski.chores.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.kuba.dziworski.chores.splitter.api.routes.dto.JsonSupport
import com.kuba.dziworski.chores.splitter.api.routes.dto.PenaltyDtos.AddPenaltyDto
import com.kuba.dziworski.chores.splitter.service.PenaltyService


trait PenaltiesRoutes extends JsonSupport {

  val penaltiesService: PenaltyService

  val penaltiesRoutes = pathPrefix("penalties") {
    get {
      complete(OK,penaltiesService.getPenalties)
    } ~ (post & entity(as[AddPenaltyDto])) { dto =>
      complete(Created,penaltiesService.addPenalty(dto))
    }
  }
}