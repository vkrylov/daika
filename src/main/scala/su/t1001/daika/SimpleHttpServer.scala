package su.t1001.daika

import java.net.InetSocketAddress
import java.nio.file.{Files, Paths}

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.config.Config

import scala.collection.mutable

abstract class SimpleHttpServerBase(config: Config) extends HttpHandler {
  private val address = createAddress()
  private val backlog = config getInt "backlog"
  private val server = HttpServer.create(address, backlog)

  server.createContext("/", this)

  private def createAddress() = {
    val sa = config getString "address"
    val port = config getInt "port"
    new InetSocketAddress(sa, port)
  }

  def respond(ex: HttpExchange, code: Int = 200, body: String = ""): Unit = {
    val bs = body.getBytes
    ex.sendResponseHeaders(code, bs.size)
    ex.getResponseBody write bs
    ex.getResponseBody.close()
    ex.close()
  }

  def start(): Unit = server.start()

  def stop(delay: Int = 1): Unit = server stop delay

}

abstract class SimpleHttpServer(config: Config) extends SimpleHttpServerBase(config) {
  private val binding = mutable.Map.empty[String, () => Any]

  def get(path: String)(action: => Any): Unit = binding += path -> (() => action)

  override def handle(ex: HttpExchange): Unit = {
    binding get ex.getRequestURI.getPath match {
      case None => respond(ex, 404)
      case Some(action) => try {
        respond(ex, 200, action().toString)
      } catch {
        case e: Exception => respond(ex, 500, e.toString)
      }
    }
  }
}

class TestServer(config: Config) extends SimpleHttpServer(config) {
  get("/") {
    "Main page"
  }

  get("/list") {
    Files.list(Paths.get(".")).map[String](p => p.toString).toArray.mkString("\n")
  }

  get("/hello") {
    "hello"
  }
}
