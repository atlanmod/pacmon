package org.atlanmod.module

import org.powerapi.PowerModule
import org.powerapi.core.OSHelper

class ThreadModule(osHelper: OSHelper, tdp: Double, tdpFactor: Double, tid: Int) extends PowerModule {
  override def sensor = Some((classOf[ThreadPowerSensor], Seq(osHelper, tid)))

  override def formula =  Some((classOf[ThreadFormula], Seq[Any](tdp, tdpFactor)))

}
