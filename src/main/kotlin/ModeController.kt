import implementation.ColorImplementation
import implementation.mode.Mode
import io.javalin.http.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class ModeController(private val color: ColorImplementation, properties: Properties) {


    val NOP_MODE = object : Mode {
        override suspend fun start(color: ColorImplementation, multiplier: Float) {
            // NOP
        }

        override fun getName(): String {
            return "NOP"
        }

    }


    private var job: Job? = null

    private var currentMode: Mode = NOP_MODE

    private val modes = HashMap<String, Mode>()

    private val logger = LogManager.getLogger()

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

    fun stopCurrentMode() {
        if (job?.isActive == true) {
            job?.cancel()
            logger.info("Stop mode")
        }
    }

}