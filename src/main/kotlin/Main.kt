import implementation.ColorImplementation
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.http.Context
import java.awt.Color
import java.io.*
import java.util.*

val DEFAULT_PROPERTIES = Properties().apply {
    setProperty("pins.red", "0")
    setProperty("pins.green", "0")
    setProperty("pins.blue", "0")
    setProperty("port", "8080")
    setProperty("pigpiod.port", "8888")
    setProperty("pigpiod.ip", "localhost")
    setProperty("start_color", "#ff1e00")
}

fun main() {
    val file = File("controller.properties");
    println("Properties file: ${file.absoluteFile}")

    val properties = Properties(DEFAULT_PROPERTIES)

    if (file.exists()) {
        properties.load(InputStreamReader(FileInputStream(file)))
    } else {
        DEFAULT_PROPERTIES.store(OutputStreamWriter(FileOutputStream(file)), "Lighting controller properties")
    }
    val color = ColorImplementation(properties)

    val controller = LightningController(color, properties)

    Javalin.create {
        it.registerPlugin(RouteOverviewPlugin("routes"))
        it.contextPath = "/control"
    }.routes {
        path("lighting") {
            get(controller::getCurrentColor)
            post(controller::setCurrentColor)
            path("reset") {
                post(controller::reset)
            }
            path("fade") {
                post(controller::fadeColor)
            }
        }
        get(Global::getInfo)
    }.start(properties.getProperty("port").toInt())
}

object Global {

    fun getInfo(ctx: Context) {

    }
}