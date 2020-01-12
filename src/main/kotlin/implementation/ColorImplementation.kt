package implementation

import java.awt.Color
import java.net.Socket
import java.nio.ByteBuffer
import java.util.*

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

    // Time between color updates in ms
    val INTERVAL_SIZE = 100

    fun setColor(color: Color, ms: Int) {
        if (color == currentColor) {
            return
        }
        val steps = ms / INTERVAL_SIZE

        val redDiff = color.red - currentColor.red
        val greenDiff = color.green - currentColor.green
        val blueDiff = color.blue - currentColor.blue

        val redPerUpdate = redDiff / steps
        val greenPerUpdate = greenDiff / steps
        val bluePerUpdate = blueDiff / steps

        var deltaColor = currentColor

        if (steps > 1) {
            Thread {
                for (i in 0..steps) {
                    deltaColor = Color(deltaColor.red + redPerUpdate, deltaColor.green + greenPerUpdate, deltaColor.blue + bluePerUpdate)
                    println(deltaColor)
                    Thread.sleep(INTERVAL_SIZE.toLong())
                    setColor(deltaColor)
                }
                setColor(color)
            }.start()
        } else {
            setColor(color)
        }


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
