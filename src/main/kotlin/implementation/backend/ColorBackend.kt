package implementation.backend

interface ColorBackend {

    fun send(pin: Int, value: Int)

    fun close()

}