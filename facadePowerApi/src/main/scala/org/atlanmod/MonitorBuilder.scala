package org.atlanmod

import java.util.concurrent.TimeUnit

import org.powerapi.{PowerDisplay, PowerModule}
import org.powerapi.reporter.{ConsoleDisplay, JFreeChartDisplay}

import scala.concurrent.duration.FiniteDuration

class MonitorBuilder {
  val monitor: Monitor = new Monitor

  def withTdp(newTdp: Double): MonitorBuilder = {
    monitor.tdp = newTdp
    this
  }

  def withTdpFactor(newTdpFactor: Double): MonitorBuilder = {
    monitor.tdpFactor = newTdpFactor
    this
  }

  def withRefreshFrequency(t: Long, unit: TimeUnit): MonitorBuilder = {
    monitor.frequency = FiniteDuration(t, unit)
    this
  }

  def withDuration(t: Long, unit: TimeUnit): MonitorBuilder = {
    monitor.duration = FiniteDuration(t, unit)
    this
  }

  def withConsoleDisplay(): MonitorBuilder = {
    monitor.console = new ConsoleDisplay
    this
  }

  def withChartDisplay(): MonitorBuilder = {
    monitor.console = new JFreeChartDisplay
    this
  }

  def withCustomDisplay(display: PowerDisplay): MonitorBuilder = {
    monitor.console = display
    this
  }

  def withModule(powerModule: PowerModule): MonitorBuilder = {
    monitor.module = powerModule
    this
  }

  def build(): Monitor = {
    monitor
  }
}
