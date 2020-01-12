import implementation.ColorImplementation
import implementation.mode.Mode
import io.javalin.http.Context
import java.util.*
import kotlin.collections.HashMap

class ModeController(private val color: ColorImplementation, properties: Properties) {

    val NOP_MODE = object : Mode {
        override fun start(color: ColorImplementation) {
            // NOP
        }

        override fun stop() {
            // NOP
        }

        override fun getName(): String {
            return "NOP"
        }

    }

    private var currentMode: Mode = NOP_MODE

    private val modes = HashMap<String, Mode>()

    fun addMode(mode: Mode) {
        modes[mode.getName()] = mode
    }

    fun getCurrentMode(ctx: Context) {
        ctx.result(currentMode.getName())

    }

    fun setMode(ctx: Context) {
        currentMode = modes.getOrDefault(ctx.queryParam("mode", "")!!, NOP_MODE)
    }

    fun getModes(ctx: Context) {
        ctx.result(modes.keys.joinToString { it })
    }

    fun start(ctx: Context) {
        Thread {
            currentMode.start(color)
        }.start()
    }

    fun stop(ctx: Context) {
        currentMode.stop()
    }

}