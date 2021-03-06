package com.olchovy.util

import java.net.InetSocketAddress
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import org.slf4j.LoggerFactory

trait Server {

  private val log = LoggerFactory.getLogger(getClass)

  def port: Int

  def service: Service[Request, Response]

  def run(): Unit = {
    val bindAddress = new InetSocketAddress(port)
    val server = Http.serve(bindAddress, service)
    try {
      log.info(s"Server listening at $bindAddress")
      Await.ready(server)
    } catch {
      case e: InterruptedException =>
        log.info("Server interrupted")
        Thread.currentThread.interrupt()
        throw e
    } finally {
      log.info("Server shutting down")
      server.close()
    }
  }
}
