package implementation.backend

import PropertiesHandler
import org.apache.logging.log4j.LogManager
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PigpiodBackend(properties: PropertiesHandler) : ControlBackend {

    private val logger = LogManager.getLogger()

    private val socket: Socket

    init {
        logger.info("Using PigpiodBackend")
        val ip = properties.properties.getProperty("pigpiod.ip")
        val port = properties.properties.getProperty("pigpiod.port").toInt()
        logger.info("Connecting to pigpiod server at $ip:$port")
        socket = Socket(ip, port)
        getVersion()
    }

    private fun getVersion() {
        socket.getOutputStream().write(createPacket(26).array())
        val hwVersionRes = ByteArray(16)
        socket.getInputStream().read(hwVersionRes)
        val hwVersionBuffer = ByteBuffer.wrap(hwVersionRes)
        hwVersionBuffer.order(ByteOrder.LITTLE_ENDIAN)
        logger.info("pigpiod version: {}", hwVersionBuffer.getInt(12))
    }

    @Synchronized
    override fun send(pin: Int, value: Int) {
        when (value) {
            255 -> {
                send(pin, true)
            }
            0 -> {
                send(pin, false)
            }
            else -> {
                socket.getOutputStream().write(createPWMPacket(pin, value))
                socket.getInputStream().read(ByteArray(16))
            }
        }
    }

    @Synchronized
    override fun send(pin: Int, state: Boolean) {
        socket.getOutputStream().write(createWritePacket(pin, state))
        socket.getInputStream().read(ByteArray(16))
    }

    override fun close() {
        socket.close()
    }

    private fun createWritePacket(pin: Int, state: Boolean) = createPacket(4, pin, if (state) 1 else 0).array()

    private fun createPWMPacket(pin: Int, value: Int) = createPacket(5, pin, value).array()

    private fun createPacket(cmd: Int, p1: Int = 0, p2: Int = 0, p3: Int = 0): ByteBuffer {
        val buffer = ByteBuffer.allocate(16)

        buffer.put(intToUInt32T(cmd))
        buffer.put(intToUInt32T(p1))
        buffer.put(intToUInt32T(p2))
        buffer.put(intToUInt32T(p3))

        return buffer
    }

    private fun intToUInt32T(value: Int): ByteArray {
        val bx = ByteArray(4)

        val newValue = value.toLong() and 0xFFFFFFFF

        if (newValue >= 0) {
            bx[0] = (newValue and 0xff).toByte()
            bx[1] = (newValue.shr(8) and 0xff).toByte()
            bx[2] = (newValue.shr(16) and 0xff).toByte()
            bx[3] = (newValue.shr(24) and 0xff).toByte()
        }

        return bx
    }

}