package cmi.infra

import cmi.infra.CouchbaseDatabase._
import com.couchbase.client.core.message.kv.MutationToken
import com.couchbase.client.java.document.JsonDocument
import com.couchbase.client.java.document.json.JsonObject
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment
import com.couchbase.client.java.{AsyncBucket, CouchbaseAsyncCluster, PersistTo}
import com.typesafe.scalalogging.LazyLogging
import rx.lang.scala.JavaConverters._
import rx.lang.scala.Observable
import spray.json.{JsValue, pimpString}

trait CouchbaseDatabase extends Database with LazyLogging {
  private val env = DefaultCouchbaseEnvironment.builder().mutationTokensEnabled(true).build()
  private val cluster = CouchbaseAsyncCluster.create(env, config.dbHosts)
  private val cmiBucket: Observable[AsyncBucket] = cluster.openBucket(config.cmiBucketName).asScala

  override def id(key: String): Observable[String] =
    cmiBucket.flatMap(_.counter(s"$key::id", 1, 0).asScala).map(c => s"$key::${c.content}")

  override def get(id: String): Observable[DbDocument] = cmiBucket.flatMap(_.get(id).asScala).toDbDocument

  override def insert(id: String, content: JsValue): Observable[DbDocument] =
    cmiBucket
      .flatMap(_.insert(JsonDocument.create(id, JsonObject.fromJson(content.toString)), PersistTo.ONE).asScala)
      .toDbDocument

  def close: Observable[Boolean] = cluster.disconnect.asScala.map(_.booleanValue())
}

object CouchbaseDatabase {

  implicit class DbDocumentConverter[T <: JsonDocument](jsonObs: Observable[T]) {
    def toDbDocument: Observable[DbDocument] =
      jsonObs.map(json =>
        CouchbaseDbDocument(json.id, json.content.toString.parseJson, json.cas, json.expiry, json.mutationToken))
  }
}

private[infra] case class CouchbaseDbDocument(override val id: String,
                                              override val content: JsValue,
                                              private val cas: Long = 0L,
                                              private val expiry: Int = 0,
                                              private var mutationToken: MutationToken = null)
    extends DbDocument {
  override def clone(content: JsValue): DbDocument = this.clone(content)
}
