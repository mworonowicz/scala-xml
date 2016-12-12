package cmi

import akka.http.scaladsl.model.ContentTypes.`text/xml(UTF-8)`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import cats.data.Reader
import cmi.cm.ChannelManagerApi
import cmi.cm.controller.ChannelManagerController
import cmi.cm.repository.RateRepositoryImpl
import cmi.infra.Database
import rx.lang.scala.Observable

import scala.concurrent.Promise
import scala.xml.Elem

trait Application {

  implicit val db: Database

  def rateRepository = new RateRepositoryImpl

  def channelManagerApi = new ChannelManagerApi(rateRepository) with ChannelManagerController

}

trait Api[T] {
  def run[S](reader: Reader[T, S]): S
}

trait Controller extends Directives {
  import scala.language.implicitConversions

  def route: Route

  implicit class ObservableTranslator[T](obs: Observable[T]) {
    def toFuture(transform: T  => Elem) = {
      val promise = Promise[HttpResponse]
      obs.subscribe(r => promise.success(HttpResponse(entity = HttpEntity(transform(r).toString).withContentType(`text/xml(UTF-8)`))),
        error => promise.failure(error),
        () => if (!promise.isCompleted) promise.success(HttpResponse(StatusCodes.NotFound)))
      complete(promise.future)
    }
  }

}