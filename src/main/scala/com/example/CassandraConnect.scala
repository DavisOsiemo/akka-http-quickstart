import com.datastax.driver.core.{Cluster, PreparedStatement, ResultSet, Row, Session, SimpleStatement}
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}

import scala.concurrent.{Future, Promise}
import scala.language.implicitConversions
import scala.collection.JavaConverters._

object CassandraConnect {

  implicit def listenableFutureToFuture[T](
                                            listenableFuture: ListenableFuture[T]
                                          ): Future[T] = {
    val promise = Promise[T]()
    Futures.addCallback(listenableFuture, new FutureCallback[T] {
      def onFailure(error: Throwable): Unit = {
        promise.failure(error)
        ()
      }
      def onSuccess(result: T): Unit = {
        promise.success(result)
        ()
      }
    })
    promise.future
  }

  //Define string interpolation
  implicit class CqlStrings(val context: StringContext) extends AnyVal {
    def cql(args: Any*)(implicit session: Session): Future[PreparedStatement] = {
      val statement = new SimpleStatement(context.raw(args: _*))
      session.prepareAsync(statement)
    }
  }


  //Create Cassandra session in the implicit scope to be able to use our CQL strings
  implicit val session = new Cluster
  .Builder()
    .addContactPoints("localhost")
    .withPort(9042)
    .build()
    .connect("test_keyspace")
  import scala.concurrent.{ ExecutionContext, Future, Promise }
  //Bind the preparedStatement and execute it
  def execute(statement: Future[PreparedStatement], params: Any*)(
    implicit executionContext: ExecutionContext, session: Session
  ): Future[ResultSet] =
    statement
      .map(_.bind(params.map(_.asInstanceOf[Object])))
      .flatMap(session.executeAsync(_))






  //val statement = cql"SELECT * FROM test_keyspace.employee_by_uuid"
  //println("Query result " + statement)
}
object Obj2 extends App {
  import CassandraConnect._

  import scala.concurrent.ExecutionContext.Implicits.global

  val myKey = 3
  val resultSet = execute(
    cql"SELECT * FROM test_keyspace.employee_by_uuid;", myKey
  )
  val rows : Future[Iterable[Row]] = resultSet.map(_.asScala)
  println(resultSet)
  println(rows)
}
