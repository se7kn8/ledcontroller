import implementation.backend.ControlBackend
import io.javalin.http.Context

class GPIOController(private val controlBackend: ControlBackend) {

    fun write(ctx: Context) {
        val pin = ctx.queryParam("pin", "XX")!!.toInt()

        val state = ctx.queryParam("state", "low")!!
        if (state == "low" || state == "high") {
            if (state == "low") {
                controlBackend.send(pin, false)
            } else if (state == "high") {
                controlBackend.send(pin, true)
            }
        } else {
            throw IllegalArgumentException()
        }

    }

}