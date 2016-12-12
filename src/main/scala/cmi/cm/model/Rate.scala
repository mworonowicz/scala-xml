package cmi.cm.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import monocle.Iso
import spray.json.DefaultJsonProtocol

import scala.xml.{Elem, NodeSeq}

case class Rate(id: String, name: String, value: Int)

object RateJson extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val rateJson = jsonFormat3(Rate)
}

object RateIso {

  val rateToElem: Iso[Rate, NodeSeq] = Iso[Rate, NodeSeq] { (rate: Rate) =>
    <Rate><Id>{rate.id}</Id><Name>{rate.name}</Name><Value>{rate.value}</Value></Rate>
  } { (xml: NodeSeq) =>
    Rate("", xml \ "Name" text, (xml \ "Value" text).toInt)
  }

}
