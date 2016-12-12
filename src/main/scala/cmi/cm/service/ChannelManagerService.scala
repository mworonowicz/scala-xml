package cmi.cm.service

import cats.data.Reader
import cmi.cm.ChannelManagerConfig
import cmi.cm.model.Rate

trait ChannelManagerService {

  def findRate(id: Long) = Reader((config: ChannelManagerConfig) => config.rateRepository.find(id))

  def createRate(info: Rate) =
    Reader((config: ChannelManagerConfig) => config.rateRepository.create(info))

}
