package cmi.infra

import java.util.concurrent.ConcurrentHashMap
import java.util.{Map => JMap}

import cats.free.Free
import com.typesafe.scalalogging.LazyLogging
import rx.lang.scala.Observable

import scala.collection.JavaConverters._

trait MemoryDatabase extends Database with LazyLogging {
  private val map = new ConcurrentHashMap[String, JMap[String, Object]]()
  private var counters = new ConcurrentHashMap[String, Int]()

  override def id(key: String): Observable[String] = {
    val c = Option(counters.get(key)).getOrElse({
      counters.put(key, 0)
      0
    })
    counters.put(key, c + 1)
    Observable.just(s"$key::$c")
  }
  override def get(key: String): Observable[DbDocument] =
    map.asScala
      .find(_._1.contains(key))
      .map(m => MemoryDbDocument(m._1, m._2))
      .map(Observable.just(_))
      .getOrElse(Observable.empty)

  override def insert(id: String, content: JMap[String, Object]): Observable[DbDocument] =
    Free.pure(map.put(id, content)).map(_ => Observable.just(MemoryDbDocument(id, content))).run

  def close: Observable[Boolean] = Observable.just(true)
}

private case class MemoryDbDocument(id: String, content: JMap[String, Object]) extends DbDocument {
  override def clone(content: JMap[String, Object]): DbDocument = this.clone(content)
}
