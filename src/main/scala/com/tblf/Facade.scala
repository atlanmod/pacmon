package com.tblf

import java.util.concurrent.TimeUnit

import org.powerapi.PowerMeter
import org.powerapi.core.LinuxHelper
import org.powerapi.module.cpu.simple.CpuSimpleModule
import org.powerapi.reporter.JFreeChartDisplay

import scala.concurrent.duration.{Duration, FiniteDuration}

class Facade(tdp: Double, tdpFactor: Double) {
  def monitor(pid: Int, freq: Int, freqUnit: TimeUnit, waitingTime: Int, waitingTimeUnit: TimeUnit): Unit = {
    val module = new CpuSimpleModule(new LinuxHelper, tdp, tdpFactor)
    val cpu = PowerMeter.loadModule(module)
    val monitoring = cpu.monitor(pid).every(FiniteDuration(freq, freqUnit))
    val console = new JFreeChartDisplay

    monitoring.to(console)
    cpu.waitFor(Duration(waitingTime, waitingTimeUnit))

    monitoring.cancel()
    cpu.shutdown()
  }
}