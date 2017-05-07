package com.kuba.dziworski.chores.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.kuba.dziworski.chores.splitter.api.routes.dto.JsonSupport
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos._
import com.kuba.dziworski.chores.splitter.service.UsersService


trait UsersRoutes extends JsonSupport {

  val usersService: UsersService

  val usersRoutes = pathPrefix("users") {
    get {
      complete(OK,usersService.getUsers)
    } ~ (post & entity(as[AddUserDto])) { user =>
      complete(Created,usersService.addUser(user))
    }
  }
}