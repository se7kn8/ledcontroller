import implementation.ColorImplementation
import implementation.mode.Mode
import io.javalin.http.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.awt.Color
import java.util.*

class LightningController(private val color: ColorImplementation, properties: PropertiesHandler) {

    private val NOP_MODE = object : Mode {
        override suspend fun start(color: ColorImplementation, multiplier: Float) {
            // NOP
        }

        override fun getName(): String {
            return "NOP"
        }
    }

    private val startColor = fromHex(properties.properties.getProperty("start_color"))

    private var job: Job? = null

    private var currentMode: Mode = NOP_MODE

    private val modes = HashMap<String, Mode>()

    private val logger = LogManager.getLogger()


    init {
        color.setColor(startColor, 1000)
    }

    fun getCurrentColor(ctx: Context) {
        ctx.result(toHex(color.currentColor))
    }

    fun setColor(ctx: Context) {
        stopCurrentMode()
        if (ctx.queryParam("time", "-1")!!.toInt() > 0) {
            color.setColor(fromHex("#" + ctx.queryParam("color", "000000")!!.replace("#", "")), ctx.queryParam("time")!!.toInt())
        } else {
            color.setColor(fromHex("#" + ctx.queryParam("color", "000000")!!.replace("#", "")))
        }
    }

    fun reset(ctx: Context) {
        stopCurrentMode()
        if (ctx.queryParam("time", "-1")!!.toInt() > 0) {
            color.setColor(startColor, ctx.queryParam("time")!!.toInt())
        } else {
            color.setColor(startColor)
        }
        ctx.result(toHex(startColor))
    }

    fun addMode(mode: Mode) {
        modes[mode.getName()] = mode
    }

    fun getCurrentMode(ctx: Context) {
        ctx.result(currentMode.getName())
    }

    fun setMode(ctx: Context) {
        currentMode = modes.getOrDefault(ctx.queryParam("mode", "")!!, NOP_MODE)
        logger.info("Set mode to ${currentMode.getName()}")
    }

    fun getModes(ctx: Context) {
        ctx.result(modes.keys.joinToString { it })
    }

    fun start(ctx: Context) {
        stopCurrentMode()
        val multiplier = ctx.queryParam("multiplier", "1.0")!!.toFloat()
        logger.info("Start mode ${currentMode.getName()} with multiplier $multiplier")
        job = GlobalScope.launch { currentMode.start(color, multiplier) }
    }

    fun stop(ctx: Context) {
        stopCurrentMode()
    }

    private fun fromHex(hexString: String): Color {
        return Color(
                hexString.substring(1, 3).toInt(16),
                hexString.substring(3, 5).toInt(16),
                hexString.substring(5, 7).toInt(16)
        )
    }

    private fun toHex(color: Color): String {
        return String.format("#%02X%02X%02X", color.red, color.green, color.blue);
    }

    private fun stopCurrentMode() {
        if (job?.isActive == true) {
            job?.cancel()
            logger.info("Stop mode")
        }
    }

}