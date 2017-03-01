package su.t1001.daika

import java.io.{BufferedWriter, PrintWriter}
import javax.net.ServerSocketFactory

import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import su.t1001.daika.ResourceUtils._

class StaticHttpServer(config: Config) {
  val log = LoggerFactory getLogger getClass

  val port = config getInt "port"
  val content = config getString "content"
  val socket = ServerSocketFactory.getDefault.createServerSocket(port)

  while (true) {
    try {
      for {
        s <- socket.accept()
        os <- s.getOutputStream
        pw <- new PrintWriter(os)
        bw <- new BufferedWriter(pw)
      } {
        bw.write(content)
      }
    } catch {
      case e: Exception => log.error("Fail to work with socket", e)
    }
  }

}


