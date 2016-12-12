package cmi

import com.typesafe.config.ConfigFactory

trait BootConfig {
  private val config = ConfigFactory.load().getConfig("http")
  val interface = config.getString("interface")
  val port = config.getInt("port")
}
