package cmi.cm.repository

import java.util.{List => JList, Map => JMap}

import cmi.cm.model._
import monocle.Iso

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
object RateIso {

  private val id = "id"
  private val name = "name"
  private val value = "value"

  val rateToMap: Iso[Rate, JMap[String, Object]] = Iso[Rate, JMap[String, Object]] {
    (rate: Rate) =>
      Map[String, Object](id -> rate.id,
                          name -> rate.name,
                          value -> int2Integer(rate.value)
                          ).asJava
  } { (map: JMap[String, Object]) =>
    Rate(map.value(id), map.value(name), map.value(value))
  }



  implicit class MapAnyValueConverter(map: JMap[String, Object]) {
    def value[T: ClassTag](key: String): T = map.get(key).asInstanceOf[T]
  }

}
