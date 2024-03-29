package com.ubirch

import java.nio.file.WatchEvent.Kind
import java.nio.file._
import com.google.common.eventbus.EventBus
import com.typesafe.scalalogging.LazyLogging

import java.io.File
import javax.inject._
import scala.jdk.CollectionConverters._

trait DirWatcher extends Thread {
  setName("DirWatcher")
  def create(path: String, watchKind: Kind[Path] = StandardWatchEventKinds.ENTRY_CREATE): DirWatcher
}

@Singleton
class DefaultDirWatcher @Inject() (eventBus: EventBus) extends DirWatcher with LazyLogging {

  private var folder: Path = _
  private var watcher: WatchService = _
  private var kind: Kind[Path] = _

  def create(path: String, watchKind: Kind[Path] = StandardWatchEventKinds.ENTRY_CREATE) = {
    logger.info("path=" + path)
    folder = Paths.get(path)
    watcher = FileSystems.getDefault.newWatchService()
    kind = watchKind
    folder.register(watcher, kind)
    this
  }

  def monitor() = {

    if (folder == null || watcher == null || kind == null) {
      throw new Exception("Error running monitor")
    } else {
      try {
        var valid: Boolean = true
        while (valid) {
          val key = watcher.take()
          for (event <- key.pollEvents().asScala) {
            val watchKind = event.kind()
            if (kind == watchKind) {
              val filename: Path = event.context().asInstanceOf[Path]
              if (!filename.toFile.isDirectory && !filename.toFile.isHidden) {
                logger.info("New file detected: {}", filename.getFileName.toFile.getName)
                eventBus.post(new File(folder.toAbsolutePath.toString + "/" + filename.getFileName.toFile.toString))
              }
            } else {
              logger.info("Received another event")
            }
          }
          valid = key.reset()
        }
      } finally {
        if (watcher != null) watcher.close()
      }
    }
  }

  override def run(): Unit = monitor()

}
