package su.t1001.daika

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object EntryPoint extends App {
  val log = LoggerFactory getLogger getClass

  log debug s"Program started"

  val config = ConfigFactory.load() getConfig "daika"
  new StaticHttpServer(config)
}