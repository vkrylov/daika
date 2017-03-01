package su.t1001.daika

import org.slf4j.Logger

object ResourceUtils {

  implicit class AutoCloseableWrapper[T <: AutoCloseable](protected val c: T) {
    def map[B](f: T => B): B = {
      try {
        f(c)
      } finally {
        c.close()
      }
    }

    def foreach(f: T => Unit): Unit = map(f)

    def flatMap[B](f: T => B): B = map(f)

    def withFilter(f: T => Boolean): AutoCloseableWrapper[T] = this
  }

  def using[T <: AutoCloseable](resource: => T)(f: T => Unit)(implicit log: Logger): Unit = {
    var r = null.asInstanceOf[T]
    try {
      r = resource
      f(r)
    } catch {
      case e: Exception =>
        log error s"Fail to use $r cause ${e.getClass} ${e.getMessage}"
    } finally {
      if (null != r) r.close()
    }
  }
}
