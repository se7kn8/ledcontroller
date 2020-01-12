package implementation

import implementation.backend.ColorBackend
import java.awt.Color
import java.net.Socket
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.absoluteValue

class ColorImplementation(properties: Properties, private val backend: ColorBackend) {

    val pinRed = properties.getProperty("pins.red").toInt()
    val pinGreen = properties.getProperty("pins.green").toInt()
    val pinBlue = properties.getProperty("pins.blue").toInt()

    val socket = Socket(properties.getProperty("pigpiod.ip"), properties.getProperty("pigpiod.port").toInt())

    var currentColor: Color = Color.BLACK

    @Synchronized
    fun setColor(color: Color) {
        if (color == currentColor) {
            return
        }
        currentColor = color
        backend.send(pinRed, color.red)
        backend.send(pinGreen, color.green)
        backend.send(pinBlue, color.blue)
    }

    @Synchronized
    fun setColor(color: Color, ms: Int) {
        if (color == currentColor) {
            return
        }

        val deltaRed = color.red - currentColor.red
        val deltaGreen = color.green - currentColor.green
        val deltaBlue = color.blue - currentColor.blue

        val redChangeRate = if (deltaRed < 0) -1 else 1
        val greenChangeRate = if (deltaGreen < 0) -1 else 1
        val blueChangeRate = if (deltaBlue < 0) -1 else 1



        if (deltaRed != 0) {
            val redSleepTime = ((ms) / deltaRed.absoluteValue).toLong()
            sendColorUpdate(currentColor.red, deltaRed, redChangeRate, redSleepTime) {
                backend.send(pinRed, it)
            }
        }

        if (deltaGreen != 0) {
            val greenSleepTime = ((ms) / deltaGreen.absoluteValue).toLong()
            sendColorUpdate(currentColor.green, deltaGreen, greenChangeRate, greenSleepTime) {
                backend.send(pinGreen, it)
            }
        }

        if (deltaBlue != 0) {
            val blueSleepTime = ((ms) / deltaBlue.absoluteValue).toLong()
            sendColorUpdate(currentColor.blue, deltaBlue, blueChangeRate, blueSleepTime) {
                backend.send(pinBlue, it)
            }
        }

        currentColor = color

    }

    private fun sendColorUpdate(startValue: Int, delta: Int, changeRate: Int, time: Long, sendFunc: (Int) -> Unit) {
        var value = startValue
        Thread {
            for (i in 1..delta.absoluteValue) {
                sendFunc(value)
                value += changeRate
                Thread.sleep(time)
            }
        }.start()
    }
}
