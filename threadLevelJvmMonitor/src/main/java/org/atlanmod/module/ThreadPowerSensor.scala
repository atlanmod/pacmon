package org.atlanmod.module

import java.util.UUID

import akka.actor.Actor
import com.jvmtop.monitor.VMInfo
import com.jvmtop.openjdk.tools.LocalVirtualMachine
import org.powerapi.core.MonitorChannel.{MonitorTick, subscribeMonitorTick, unsubscribeMonitorTick}
import org.powerapi.core.target.{Target, TargetUsageRatio}
import org.powerapi.core.{MessageBus, OSHelper}
import org.powerapi.module.Sensor
import org.powerapi.module.cpu.UsageMetricsChannel.publishUsageReport


class ThreadPowerSensor(eventBus: MessageBus, muid: UUID, target: Target, osHelper: OSHelper, tid: Int) extends Sensor(eventBus, muid, target) {

  private val vmInfo = VMInfo.processNewVM(LocalVirtualMachine.getLocalVirtualMachine(osHelper.getProcesses(target).head.pid), 1)
  private val proxyClient = vmInfo.getProxyClient
  private val threadMXBean = vmInfo.getThreadMXBean

  def init(): Unit = {
    println("Starting monitoring JVM process "+target+" on Thread ID "+tid)
    subscribeMonitorTick(muid, target)(eventBus)(self)
  }

  def terminate(): Unit = unsubscribeMonitorTick(muid, target)(eventBus)(self)

  def currentTimes(): (Long, Long) = {
    proxyClient.flush()
    (threadMXBean.getThreadCpuTime(tid), proxyClient.getProcessCpuTime)
  }

  def usageRatio(oldThread: Long, newThread: Long, oldCpu: Long, newCpu: Long): TargetUsageRatio = {
    val threadDeltaTime = if (newThread - oldThread > 0) newThread - oldThread else 0
    val cpuDeltaTime = if (newCpu - oldCpu > 0) newCpu - oldCpu else 0

    if (cpuDeltaTime > 0) {
      TargetUsageRatio(threadDeltaTime / cpuDeltaTime.toDouble)
    }
    else
      TargetUsageRatio(0)
  }

  def handler: Actor.Receive = {
    val initTimes = currentTimes()
    sense(initTimes._1, initTimes._2)
  }

  def sense(oldThreadTime: Long, oldCpuTime: Long): Actor.Receive = {
    case msg: MonitorTick =>
      val newTimes = currentTimes()
      publishUsageReport(muid, target, usageRatio(oldThreadTime, newTimes._1, oldCpuTime, newTimes._2), msg.tick)(eventBus)
      context.become(sense(newTimes._1, newTimes._2) orElse sensorDefault)
  }


}
