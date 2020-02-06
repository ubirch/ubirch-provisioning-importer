package com.ubirch

import java.nio.file.WatchEvent.Kind
import java.nio.file.{FileSystems, Path, Paths, StandardWatchEventKinds, WatchEvent, WatchKey, WatchService}

import com.google.common.eventbus.EventBus
import com.typesafe.scalalogging.{LazyLogging, StrictLogging}
import javax.inject._

import scala.collection.JavaConverters._

trait DirWatcher {
  def create(path: String, watchKind: Kind[Path] = StandardWatchEventKinds.ENTRY_CREATE): DirWatcher
  def monitor: Unit
}

@Singleton
class DefaultDirWatcher @Inject()(eventBus: EventBus) extends DirWatcher with LazyLogging {

  private var folder: Path = _
  private var watcher: WatchService = _
  private var kind: Kind[Path] = _

  def create(path: String, watchKind: Kind[Path] = StandardWatchEventKinds.ENTRY_CREATE)  = {
    folder = Paths.get(path)
    watcher = FileSystems.getDefault.newWatchService()
    kind = watchKind
    folder.register(watcher, kind)
    this
  }

  def monitor = {
    var valid: Boolean = true
    while(valid) {
      val key = watcher.take()
      for(event <- key.pollEvents().asScala){
        val watchKind = event.kind()
        if(kind == watchKind){
          val filename: Path =  event.context().asInstanceOf[Path]
          logger.info("New file detected: {}", filename.getFileName)
          eventBus.post(List(filename.getFileName.toFile))
        } else {
          logger.info("Received another event")
        }
      }
      valid = key.reset()
    }
  }

}
