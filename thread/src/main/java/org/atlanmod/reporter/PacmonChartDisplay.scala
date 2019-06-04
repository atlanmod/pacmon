package org.atlanmod.reporter

import java.awt.{BasicStroke, Dimension}
import java.util.UUID
import javax.swing.SwingUtilities

import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.time.{FixedMillisecond, TimeSeries, TimeSeriesCollection, TimeSeriesDataItem}
import org.jfree.ui.ApplicationFrame
import org.powerapi.PowerDisplay
import org.powerapi.core.power.Power
import org.powerapi.core.target.Target
import org.powerapi.module.PowerChannel.AggregatePowerReport
import org.powerapi.reporter.JFreeChartDisplay




class Chart(title: String) {
  val dataset = new TimeSeriesCollection
  val chart = ChartFactory.createTimeSeriesChart(title,
    Chart.xValues, Chart.yValues, dataset, true, true, false)
  val timeSeries = collection.mutable.HashMap[String, TimeSeries]()

  def process(values: Map[String, Double], timestamp: Long) {
    values.foreach({ value =>
      if (!timeSeries.contains(value._1)) {
        val serie = new TimeSeries(value._1)
        dataset.addSeries(serie)
        timeSeries += (value._1 -> serie)
        chart.getXYPlot().getRenderer().setSeriesStroke(dataset.getSeriesCount() - 1, new BasicStroke(3))
      }
      timeSeries(value._1).addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(timestamp), value._2))
    })
  }
}

object Chart {
  lazy val chart = {
    val ch = new Chart(title)
    val plot = ch.chart.getXYPlot()
    plot.setBackgroundPaint(java.awt.Color.WHITE)
    plot.setDomainGridlinesVisible(true)
    plot.setDomainGridlinePaint(java.awt.Color.GRAY)
    plot.setRangeGridlinesVisible(true)
    plot.setRangeGridlinePaint(java.awt.Color.GRAY)
    ch
  }
  val xValues = "Time (s)"
  val yValues = "Power (mW)"
  val title = "Pacmon"
  val chartPanel = {
    val panel = new ChartPanel(chart.chart)
    panel.setMouseWheelEnabled(true)
    panel.setDomainZoomable(true)
    panel.setFillZoomRectangle(true)
    panel.setRangeZoomable(true)
    panel
  }

  val applicationFrame = {
    val app = new ApplicationFrame(title)
    app
  }

  def run() {
    applicationFrame.setContentPane(chartPanel)
    applicationFrame.setSize(new Dimension(800, 600))
    applicationFrame.setVisible(true)
  }

  def process(values: Map[String, Double], timestamp: Long) {
    chart.process(values, timestamp)
  }
}

class PacmonChartDisplay extends PowerDisplay {

  SwingUtilities.invokeLater(new Runnable {
    def run() {
      Chart.run()
    }
  })

  override def display(muid: UUID, timestamp: Long, targets: Set[Target], devices: Set[String], power: Power): Unit = {
    Chart.process(Map(s"${targets.mkString(",")}" -> power.toMilliWatts), timestamp)
  }
}
