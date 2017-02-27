package su.t1001.daika

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object EntryPoint extends App {
  val log = LoggerFactory getLogger getClass
  val param = ConfigFactory.load().getString("app.param")
  log debug s"Hello $param"
}