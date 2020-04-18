import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Feature
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import org.apache.logging.log4j.LogManager
import java.io.*
import kotlin.system.exitProcess

class ConfigManager(private val file: File) {

    object ControllerSpec : ConfigSpec("controller", description = "ledController config file") {

        object LightingSpec : ConfigSpec("lighting") {
            object PinsSpec : ConfigSpec("pins", description = "The pins that are used for the color channels") {
                val red by required<Int>()
                val green by required<Int>()
                val blue by required<Int>()
            }

            object TimeControlSpec : ConfigSpec("timecontrol") {
                val enable by required<Boolean>(description = "Enabled automatic turn on/off of the led strip")
                val time_on by required<String>(description = "Time (in local time/24h) at which the led strip should be enabled")
                val time_off by required<String>(description = "Time (in local time/24h) at which the led strip should be disabled")
            }

            val start_color by required<String>(description = "This is that color that is used when the software starts")
        }

        object BackendSpec : ConfigSpec("backend", description = "The backend to control pins.") {
            val backend by required<String>()

            object PigpiodSpec : ConfigSpec("pigpiod", description = "Attention: pigpiod uses BCM pin numbers") {
                val port by required<Int>()
                val host by required<String>()
            }
        }

        object ServerSpec : ConfigSpec("server") {
            val port by required<Int>(description = "Port to control the LedController")
        }

        object StatsSpec : ConfigSpec("stats") {
            val save_dir by required<String>(description = "In this directory the LedController will create the file the save the stats. It must be writable")
        }

    }

    private val logger = LogManager.getLogger()

    fun load(): Config {
        val config = Config {
            addSpec(ControllerSpec)
            enable(Feature.WRITE_DESCRIPTIONS_AS_COMMENTS)
        }

        if (file.exists()) {
            val realConfig = config.from.toml.file(file, false)
            if (realConfig.containsRequired()) {
                logger.info("Config {} loaded", file.absolutePath)
                return realConfig
            } else {
                logger.warn("Missing values in config... Saving default config")
                saveDefault(config)
                logger.info("Restart the program to use the new config")
                exitProcess(100)
            }
        } else {
            logger.warn("No config found... Saving default config")
            saveDefault(config)
            logger.info("Restart the program to use the new config")
            exitProcess(100)
        }
    }


    fun saveDefault(config: Config) {
        val defaultConfig = config.withLayer("default")

        defaultConfig[ControllerSpec.LightingSpec.PinsSpec.red] = 0
        defaultConfig[ControllerSpec.LightingSpec.PinsSpec.green] = 0
        defaultConfig[ControllerSpec.LightingSpec.PinsSpec.blue] = 0

        defaultConfig[ControllerSpec.LightingSpec.start_color] = "#FF1E00"

        defaultConfig[ControllerSpec.BackendSpec.backend] = "pigpiod"
        defaultConfig[ControllerSpec.BackendSpec.PigpiodSpec.host] = "localhost"
        defaultConfig[ControllerSpec.BackendSpec.PigpiodSpec.port] = 8888

        defaultConfig[ControllerSpec.ServerSpec.port] = 8080

        defaultConfig[ControllerSpec.StatsSpec.save_dir] = ""

        defaultConfig[ControllerSpec.LightingSpec.TimeControlSpec.enable] = false
        defaultConfig[ControllerSpec.LightingSpec.TimeControlSpec.time_on] = "18:00"
        defaultConfig[ControllerSpec.LightingSpec.TimeControlSpec.time_off] = "0:00"

        logger.info("Saving default properties to $file")

        defaultConfig.toToml.toFile(file)
    }


}