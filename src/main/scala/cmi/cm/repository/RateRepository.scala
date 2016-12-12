package cmi.cm.repository

import cmi.cm.model.Rate
import cmi.infra.Database
import com.typesafe.scalalogging.LazyLogging
import rx.lang.scala.Observable

trait RateRepository extends LazyLogging {

  def db: Database

  private val key = "cmi"
  private def keyPrefix(id: Long) = s"$key::$id"

  def create(rate: Rate): Observable[Rate] =
    db.id(key)
      .map(id => (id, rate.copy(id = id.split("::").last)))
      .flatMap { case (id, rate) => db.insert(id, RateIso.rateToMap.get(rate)) }
      .map(doc => RateIso.rateToMap.reverseGet(doc.content))

  def find(id: Long): Observable[Rate] = db.get(keyPrefix(id)).map(doc => RateIso.rateToMap.reverseGet(doc.content))

}

class RateRepositoryImpl(implicit override val db: Database) extends RateRepository
