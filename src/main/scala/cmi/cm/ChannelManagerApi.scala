package cmi.cm

import cats.data.Reader
import cmi.Api
import cmi.cm.repository.RateRepository
import cmi.cm.service.ChannelManagerService

class ChannelManagerApi(channelManagerRepository: RateRepository) extends Api[ChannelManagerConfig] {

  def run[S](reader: Reader[ChannelManagerConfig, S]): S = reader(ChannelManagerConfig(channelManagerRepository))

}

object ChannelManagerFacade extends ChannelManagerService

case class ChannelManagerConfig(rateRepository: RateRepository)
