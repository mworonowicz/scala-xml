package cmi

trait Routing extends Controller { app: Application =>

  def route =
    pathPrefix("cmi") {
      app.channelManagerApi.route
    }

}
