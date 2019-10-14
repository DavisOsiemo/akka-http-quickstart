package com.example

import com.example.UserRegistryActor.{ ActionPerformed, ActionPerformed2 }

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val itemJsonFormat = jsonFormat2(Item)
  implicit val itemsJsonFormat = jsonFormat1(Items)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
  implicit val actionPerformed2JsonFormat = jsonFormat1(ActionPerformed2)
}
//#json-support
