package cmi.infra

import com.typesafe.config.ConfigFactory
import rx.lang.scala.Observable
import java.util.{Map => JMap}

import spray.json.JsValue

import scala.util.parsing.json.JSONObject

trait Database {
  def config: DbConfig
  def id(key: String): Observable[String]
  def get(id: String): Observable[DbDocument]
  def insert(id: String, content: JsValue): Observable[DbDocument]
  def close: Observable[Boolean]
}

object Database extends DbConfig { dbConfig =>
  def apply(): Database =
    if (embed) new { val config: DbConfig = dbConfig } with MemoryDatabase
    else new { val config: DbConfig = dbConfig } with CouchbaseDatabase

}

trait DbConfig {
  private val config = ConfigFactory.load().getConfig("db")

  private[infra] val embed = config.getBoolean("embed")
  private[infra] val dbHosts = config.getStringList("hosts")
  private[infra] val cmiBucketName = config.getString("bucket")
}

trait DbDocument {
  def id: String
  def content: JsValue
  def clone(content: JsValue): DbDocument
}
