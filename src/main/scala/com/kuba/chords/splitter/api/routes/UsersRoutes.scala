package com.kuba.chords.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.kuba.chords.splitter.api.routes.dto.JsonSupport
import com.kuba.chords.splitter.api.routes.dto.UserDtos._
import com.kuba.chords.splitter.service.UsersService


trait UsersRoutes extends JsonSupport {

  val usersService: UsersService

  val usersRoutes = pathPrefix("users") {
    (post & entity(as[AddUserDto])) { user =>
      complete(Created,usersService.addUser(user))
    }
  }
}