package cmi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cmi.infra.Database
import com.typesafe.scalalogging.LazyLogging

object Boot extends App with Application with Routing with BootConfig with LazyLogging {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer.apply()
  implicit val executionContext = system.dispatcher

  val db = Database()
  val binding = Http().bindAndHandle(route, interface, port)

  logger.info(s"Bound to port $port on interface $interface")
  binding.failed.foreach(logger.error(s"Failed to bind to $interface:$port!", _))

  sys.addShutdownHook(() => {
    db.close
    system.terminate
  })

}
