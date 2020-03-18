import implementation.ColorImplementation
import implementation.StatsImplementation
import implementation.backend.PigpiodBackend
import implementation.mode.BlinkMode
import implementation.mode.RainbowMode
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.http.Context
import org.apache.logging.log4j.LogManager
import java.io.*

fun main(args: Array<String>) {
    val logger = LogManager.getLogger()

    val file = File(args[0])
    logger.info("Properties file: ${file.absoluteFile}")

    val propertiesHandler = PropertiesHandler(file)

    if (file.exists()) {
        propertiesHandler.load()
    } else {
        propertiesHandler.saveDefault()
        logger.warn("Program will exit. You have to edit the properties files for you needs!")
        return
    }

    val backend = PigpiodBackend(propertiesHandler)

    val colorController = LightningController(ColorImplementation(propertiesHandler, backend), propertiesHandler).apply {
        addMode(RainbowMode())
        addMode(BlinkMode())
    }

    val gpioController = GPIOController(backend)

    val statsController = StatsController(StatsImplementation(propertiesHandler))

    Javalin.create {
        it.registerPlugin(RouteOverviewPlugin("routes"))
        it.contextPath = "/control"
        it.logIfServerNotStarted = false
        it.showJavalinBanner = false
    }.routes {
        path("lighting") {
            get(colorController::getCurrentColor)
            post(colorController::setColor)
            path("default") {
                get(colorController::getDefaultColor)
                post(colorController::setDefaultColor)
            }
            path("reset") {
                post(colorController::reset)
            }
            path("mode") {
                get(colorController::getCurrentMode)
                post(colorController::setMode)
                path("start") {
                    post(colorController::start)
                }
                path("stop") {
                    post(colorController::stop)
                }
                path("list") {
                    get(colorController::getModes)
                }
            }
        }
        path("gpio") {
            path("write") {
                post(gpioController::write)
            }
        }
        path("stats") {
            path("remove") {
                post(statsController::remove)
            }
            path("get") {
                path("latest") {
                    get(statsController::getLatest)
                }
                path("all") {
                    get(statsController::getAll)
                }

            }
            path("update") {
                post(statsController::update)
            }
            path("list") {
                get(statsController::list)
            }
        }
        path("version") {
            get(Global::getVersion)
        }
        get(Global::getInfo)
    }.error(404) {
        if (it.resultString() == null) {
            it.result("404 Not found\nContext-Path is /control")
        }
    }.start(propertiesHandler.properties.getProperty("port").toInt())
    Runtime.getRuntime().exec(propertiesHandler.properties.getProperty("start_command"))
}

object Global {
    fun getInfo(ctx: Context) {
        ctx.result("LedController by se7kn8 (https://github.com/SE7-KN8/ledcontroller)\nAvailable routes are under /control/routes")
    }

    fun getVersion(ctx: Context) {
        val version = "1.6.1-snapshot"
        ctx.result(version)
    }
}