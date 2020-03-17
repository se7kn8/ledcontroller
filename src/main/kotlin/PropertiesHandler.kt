import org.apache.logging.log4j.LogManager
import java.io.*
import java.util.*

class PropertiesHandler(private val file: File) {

    private val logger = LogManager.getLogger()

    private val DEFAULT_PROPERTIES = Properties().apply {
        setProperty("pins.red", "0")
        setProperty("pins.green", "0")
        setProperty("pins.blue", "0")
        setProperty("port", "8080")
        setProperty("pigpiod.port", "8888")
        setProperty("pigpiod.ip", "localhost")
        setProperty("start_color", "#ff1e00")
        setProperty("stats_save_directory", "stats/")
    }

    val properties = Properties(DEFAULT_PROPERTIES)

    fun load() {
        properties.load(InputStreamReader(FileInputStream(file)))
    }

    fun save() {
        properties.store(OutputStreamWriter(FileOutputStream(file)),"Lighting controller properties. PINs are broadcom GPIO numbers")
    }

    fun saveDefault() {
        logger.info("Saving default properties to $file")
        DEFAULT_PROPERTIES.store(OutputStreamWriter(FileOutputStream(file)), "Lighting controller properties. PINs are broadcom GPIO numbers")
    }


}