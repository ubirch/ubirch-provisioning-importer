package com.ubirch

import com.google.common.eventbus.EventBus
import com.google.inject.Provider
import javax.inject._

@Singleton
class DefaultIncomingFileBus extends Provider[EventBus]{
  val eb = new EventBus("incoming-files")
  override def get(): EventBus = eb
}
