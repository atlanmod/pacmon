package org.atlanmod.module

import java.util.UUID

import akka.event.LoggingReceive
import org.powerapi.core.MessageBus
import org.powerapi.core.power._
import org.powerapi.core.target.Target
import org.powerapi.module.Formula
import org.powerapi.module.PowerChannel.publishRawPowerReport
import org.powerapi.module.cpu.UsageMetricsChannel.{SimpleUsageReport, subscribeSimpleUsageReport, unsubscribeSimpleUsageReport}

class ThreadFormula(eventBus: MessageBus, muid: UUID, target: Target, tdp: Double, tdpFactor: Double)
  extends Formula(eventBus, muid, target) {
    override def init(): Unit = subscribeSimpleUsageReport(muid, target)(eventBus)(self) //Launch continuous measure

    override def terminate(): Unit = unsubscribeSimpleUsageReport(muid, target)(eventBus)(self) //Stop continuous measure

    //Handle power
    override def handler: Receive = LoggingReceive {

      case msg: SimpleUsageReport =>
        val power = {

          try {
            (((tdp * tdpFactor) * msg.targetRatio.ratio).W)/1000 //translating from mW to W
          }
          catch {
            case _: Exception =>
              log.warning("The power value is out of range. Skip.")
              0.W
          }
        }

        publishRawPowerReport(msg.muid, msg.target, power, "cpu", msg.tick)(eventBus)
    }
  }
