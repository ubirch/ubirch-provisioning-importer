package com.ubirch

import java.util.concurrent.CountDownLatch

import com.google.common.eventbus.EventBus
import com.ubirch.Boot.Factory

object Importer {

  val factory = new Factory(List(new Binder)) {}

  def main(args: Array[String]): Unit = {

    val dir = "/home/carlos/sources/ubirch/ubirch-provisioning-importer/hola/"

    val dirLoader = factory.get[DirLoader]
    val dirWatcher = factory.get[DirWatcher]
    val csvReader = factory.get[CSVReader]
    val fileBus = factory.get[EventBus]

    fileBus.register(csvReader)

    csvReader.start()

    dirLoader.load(dir)

    dirWatcher
      .create(dir)
      .start()

    new CountDownLatch(1).await()

  }

}
