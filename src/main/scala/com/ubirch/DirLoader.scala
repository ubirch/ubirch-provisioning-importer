package com.ubirch

import java.io.File

import com.google.common.eventbus.EventBus
import com.typesafe.scalalogging.LazyLogging
import javax.inject._

trait DirLoader {
  def load(path: String): List[File]
}

@Singleton
class DefaultDirLoader @Inject() (eventBus: EventBus) extends DirLoader with LazyLogging {

  def load(path: String): List[File] = {
    logger.info("Checking for current files")
    val d = new File(path)
    if (d.exists && d.isDirectory) {
      val currentFiles = d.listFiles(FileFilters.CSV).filter(_.isFile).toList
      currentFiles.foreach(file => eventBus.post(file))
      currentFiles
    } else {
      Nil
    }
  }

}
