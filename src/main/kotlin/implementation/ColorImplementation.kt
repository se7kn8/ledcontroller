package implementation

import java.awt.Color
import java.net.Socket
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.absoluteValue

class ColorImplementation(properties: Properties) {

    val pinRed = properties.getProperty("pins.red").toInt()
    val pinGreen = properties.getProperty("pins.green").toInt()
    val pinBlue = properties.getProperty("pins.blue").toInt()

    val socket = Socket(properties.getProperty("pigpiod.ip"), properties.getProperty("pigpiod.port").toInt())

    var currentColor = Color.BLACK

    fun setColor(color: Color) {
        if (color == currentColor) {
            return
        }
        currentColor = color
        sendColor(pinRed, color.red)
        sendColor(pinGreen, color.green)
        sendColor(pinBlue, color.blue)
    }

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
                sendColor(pinRed, it)
            }
        }

        if (deltaGreen != 0) {
            val greenSleepTime = ((ms) / deltaGreen.absoluteValue).toLong()
            sendColorUpdate(currentColor.green, deltaGreen, greenChangeRate, greenSleepTime) {
                sendColor(pinGreen, it)
            }
        }

        if (deltaBlue != 0) {
            val blueSleepTime = ((ms) / deltaBlue.absoluteValue).toLong()
            sendColorUpdate(currentColor.blue, deltaBlue, blueChangeRate, blueSleepTime) {
                sendColor(pinBlue, it)
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

    @Synchronized
    private fun sendColor(pin: Int, value: Int) {
        socket.getOutputStream().write(createPacketBuffer(pin, value).array())
        socket.getInputStream().read(ByteArray(16))
    }

    private fun createPacketBuffer(pin: Int, value: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(16)

        buffer.put(intToUInt32T(5)) // 5 = PIGPIOD CMD PWM
        buffer.put(intToUInt32T(pin))
        buffer.put(intToUInt32T(value))
        buffer.put(intToUInt32T(0))

        return buffer
    }

    private fun intToUInt32T(value: Int): ByteArray {
        val bx = ByteArray(4)

        val newValue = value.toLong() and 0xFFFFFFFF;

        if (newValue >= 0) {
            bx[0] = (newValue and 0xff).toByte()
            bx[1] = (newValue.shr(8) and 0xff).toByte()
            bx[2] = (newValue.shr(16) and 0xff).toByte()
            bx[3] = (newValue.shr(24) and 0xff).toByte()
        }

        return bx;
    }


}
