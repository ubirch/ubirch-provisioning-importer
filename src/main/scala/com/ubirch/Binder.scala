package com.ubirch

import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule

class Binder extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DirWatcher]).to(classOf[DefaultDirWatcher])
    bind(classOf[DirLoader]).to(classOf[DefaultDirLoader])
    bind(classOf[CSVReader]).to(classOf[DefaultCSVReader])
    bind(classOf[EventBus]).toProvider(classOf[DefaultIncomingFileBus])
    ()
  }
}
