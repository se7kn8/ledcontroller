package implementation.backend

import java.net.Socket
import java.nio.ByteBuffer
import java.util.*

class PigpiodBackend(properties: Properties): ColorBackend {

    private val socket = Socket(properties.getProperty("pigpiod.ip"), properties.getProperty("pigpiod.port").toInt())

    @Synchronized
    override fun send(pin: Int, value: Int) {
        socket.getOutputStream().write(createPacketBuffer(pin, value).array())
        socket.getInputStream().read(ByteArray(16))
    }

    override fun close() {
        socket.close()
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