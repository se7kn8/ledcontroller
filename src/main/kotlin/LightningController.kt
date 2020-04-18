import com.uchuhimo.konf.Config
import implementation.ColorImplementation
import implementation.mode.Mode
import io.javalin.http.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class LightningController(private val color: ColorImplementation, private val config: Config) {

    private val NOP_MODE = object : Mode {
        override suspend fun start(color: ColorImplementation, multiplier: Float) {
            // NOP
        }

        override fun getName(): String {
            return "None"
        }
    }

    private val formatter = SimpleDateFormat("HH:mm")

    private val startColor = fromHex(config[ConfigManager.ControllerSpec.LightingSpec.start_color])

    private val enableTimeControl = config[ConfigManager.ControllerSpec.LightingSpec.TimeControlSpec.enable]

    private val startTime = Calendar.getInstance().apply {
        time = formatter.parse(config[ConfigManager.ControllerSpec.LightingSpec.TimeControlSpec.time_on])
    }
    private var startTimeNextDay = false

    private val endTime = Calendar.getInstance().apply {
        time = formatter.parse(config[ConfigManager.ControllerSpec.LightingSpec.TimeControlSpec.time_off])
    }

    private var endTimeNextDay = false

    private var job: Job? = null

    private var currentMode: Mode = NOP_MODE

    private val modes = HashMap<String, Mode>().apply {
        put("None", NOP_MODE)
    }

    private val logger = LogManager.getLogger()

    init {
        if (enableTimeControl) {
            val timeDiff = timeDiff(startTime, false)
            if (timeDiff > 0) {
                thread(start = true) {
                    Thread.sleep(timeDiff)
                    color.setColor(startColor, 2000)
                    timeControlLoop()
                }
            } else {
                color.setColor(startColor, 2000)
                thread(start = true) {
                    timeControlLoop()
                }
            }
        } else {
            color.setColor(startColor, 2000)
        }
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

    fun getDefaultColor(ctx: Context) {
        ctx.result(toHex(startColor))
    }

    fun setDefaultColor(ctx: Context) {
        startColor = fromHex("#" + ctx.queryParam("color", "000000")!!.replace("#", ""))
        logger.info("Set default color to ${toHex(startColor)}")
        config[ConfigManager.ControllerSpec.LightingSpec.start_color] = toHex(startColor)
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

    private fun timeControlLoop() {
        while (true) {
            val endTimeDiff = timeDiff(endTime)
            if (endTimeDiff > 0) {
                Thread.sleep(endTimeDiff)
                color.setColor(Color.BLACK, 2000)
                val startTimeDiff = timeDiff(startTime)
                if (startTimeDiff > 0) {
                    Thread.sleep(startTimeDiff)
                    color.setColor(startColor, 2000)
                } else {
                    logger.error("Negative start time diff. Stopping time control")
                    break
                }
            } else {
                logger.error("Negative end time diff. Stopping time control")
                break
            }
        }
    }

    private fun timeDiff(time: Calendar, nextDay: Boolean): Long {
        val timestamp = Calendar.getInstance()
        timestamp.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
        timestamp.set(Calendar.MINUTE, time.get(Calendar.MINUTE))
        timestamp.set(Calendar.SECOND, 0)
        if (nextDay) {
            timestamp.add(Calendar.DAY_OF_YEAR, 1)
        }
        println("Time: " + timestamp.time) // TODO remove this
        return timestamp.timeInMillis - System.currentTimeMillis()
    }

}