package cmi.cm.controller

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import cmi.cm.model.Rate
import cmi.cm.{ChannelManagerConfig, ChannelManagerFacade}
import cmi.{Api, Controller}
import com.typesafe.scalalogging.LazyLogging

import scala.xml.NodeSeq

trait ChannelManagerController extends Controller with ScalaXmlSupport with LazyLogging {
  api: Api[ChannelManagerConfig] =>
  override def route =
    pathPrefix("test") {
      pathEnd {
        post {
          decodeRequest {
            entity(as[NodeSeq]) { xml =>
              api.run(ChannelManagerFacade.createRate(Rate("", xml \ "Name" text, (xml \ "Value" text).toInt))).toFuture(
                rate => <Rate><Id>{rate.id}</Id><Name>{rate.name}</Name><Value>{rate.value}</Value></Rate>
              )
            }
          }
        } ~
          get {
            complete(<test>1</test>)
          }
      }
    }
}
