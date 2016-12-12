package cmi.cm.controller

import akka.http.scaladsl.coding.Deflate
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import cmi.cm.model.RateIso._
import cmi.cm.{ChannelManagerConfig, ChannelManagerFacade}
import cmi.{Api, Controller}
import com.typesafe.scalalogging.LazyLogging

import scala.language.postfixOps
import scala.xml.NodeSeq

trait ChannelManagerController extends Controller with ScalaXmlSupport with LazyLogging {
  api: Api[ChannelManagerConfig] =>
  override def route =
    pathPrefix("test") {
      pathEnd {
        post {
          decodeRequest {
            entity(as[NodeSeq]) { xml =>
              api.run(ChannelManagerFacade.createRate(rateToElem.reverseGet(xml))).toFuture(rateToElem.get)
            }
          }
        }
      } ~
        path(LongNumber) { rateId =>
          get {
            encodeResponseWith(Deflate) {
              api.run(ChannelManagerFacade.findRate(rateId)).toFuture(rateToElem.get)
            }
          }
        }
    }
}
