package com.ubirch

import java.io.File

import com.google.common.eventbus.Subscribe
import com.typesafe.scalalogging.LazyLogging
import javax.inject._

@Singleton
class CSVReader extends LazyLogging {

  @Subscribe
  def onNew(files: List[File]): Unit = {
    logger.info("Received new files")
    //println(path.getFileName.getFileName)
    println(files.map(_.getName))
  }

}
