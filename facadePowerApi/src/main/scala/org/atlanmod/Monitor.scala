package org.atlanmod

import java.util.concurrent.TimeUnit

import org.powerapi.core.LinuxHelper
import org.powerapi.core.target.Process
import org.powerapi.module.cpu.simple.CpuSimpleModule
import org.powerapi.reporter.ConsoleDisplay
import org.powerapi.{PowerDisplay, PowerMeter, PowerMonitoring}

import scala.concurrent.duration.{Duration, FiniteDuration}

class Monitor {
  var tdp: Double = 0
  var tdpFactor: Double = 0.7
  var frequency: FiniteDuration = FiniteDuration(50, TimeUnit.MILLISECONDS)
  var duration: FiniteDuration = Duration(5, TimeUnit.MINUTES)
  var console: PowerDisplay = new ConsoleDisplay
  var monitoring : PowerMonitoring = _

  def run(pid: Int): Unit = {
    val module = new CpuSimpleModule(new LinuxHelper, tdp, tdpFactor)
    val cpu = PowerMeter.loadModule(module)
    monitoring = cpu.monitor(Process(pid))
                    .every(frequency)
                    .to(console)
  }

  def stop(): Unit = {
    monitoring.cancel()
  }
}
