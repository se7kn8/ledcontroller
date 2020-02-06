package implementation.backend

interface ControlBackend {

    fun send(pin: Int, value: Int)

    fun send(pin: Int, state: Boolean)

    fun close()

}