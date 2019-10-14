package com.example

//#user-registry-actor
import akka.actor.{ Actor, ActorLogging, Props }

//#user-case-classes
final case class User(name: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User])

final case class Item(name: String, amount: Int)
final case class Items(items: Seq[Item])
//#user-case-classes

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case class ActionPerformed2(message: String)

  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  final case class CreateItem(item: Item)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  var users = Set.empty[User]

  var items = Set.empty[Item]

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(users.toSeq)
    case CreateUser(user) =>
      users += user
      sender() ! ActionPerformed(s"User ${user.name} created.")

    case CreateItem(item) =>
      items += item
      sender() ! ActionPerformed2(s"Item ${}")

    case GetUser(name) =>
      sender() ! users.find(_.name == name)
    case DeleteUser(name) =>
      users.find(_.name == name) foreach { user => users -= user }
      sender() ! ActionPerformed(s"User ${name} deleted.")
  }
}
//#user-registry-actor