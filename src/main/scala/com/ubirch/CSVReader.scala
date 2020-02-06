package com.ubirch

import java.io.File
import java.util.concurrent.LinkedBlockingQueue

import com.github.tototoshi.csv
import com.github.tototoshi.csv._
import com.google.common.eventbus.Subscribe
import com.typesafe.scalalogging.LazyLogging
import javax.inject._

trait CSVReader extends Thread {

  setName("CSVReader")

  def onNew(file: File): Unit
  def process(): Unit

}

@Singleton
class DefaultCSVReader extends CSVReader with LazyLogging {

  val work = new LinkedBlockingQueue[File]()

  @Subscribe
  def onNew(file: File): Unit = {
    logger.info("Received work " + file.getName)
    work.put(file)
  }

  def process() = {

    val file = work.poll()

    if (file != null) {

      val reader: csv.CSVReader = null

      try {

        val reader: csv.CSVReader = CSVReader.open(file)
        val iterator = reader.iterator
        while (iterator.hasNext) {
          println(iterator.next())
        }

      } catch {
        case e: Exception =>
          logger.error(s"Something happened processing file: ${file.getName}", e)
      } finally {
        if (reader != null) reader.close()
      }
    }

  }

  override def run(): Unit = {
    while (true) {
      process()
    }
  }
}
