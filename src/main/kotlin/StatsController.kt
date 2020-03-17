import implementation.StatsImplementation
import io.javalin.http.Context

class StatsController(private val stats: StatsImplementation) {

    fun remove(ctx: Context) {
        getStatName(ctx) {
            stats.removeStat(it)
        }
    }

    fun getLatest(ctx: Context) {
        getStatName(ctx) {
            val data = stats.getStatData(it)?.last()
            if (data == null) {
                ctx.status(404)
            } else {
                ctx.result(data.toString())
            }
        }
    }

    fun getAll(ctx: Context) {
        val maxData = ctx.queryParam("max", "-1")!!.toInt()
        val csv = ctx.queryParam("csv", "false") == "true"

        getStatName(ctx) { dataName ->
            val data = stats.getStatData(dataName)
            if (data == null) {
                ctx.status(404)
            } else {
                if (csv) {
                    val builder = StringBuilder()
                    builder.append("Time,Value\n")
                    if (maxData == -1) {
                        data.forEach {
                            builder.append("${it.time},${it.value}\n")
                        }
                    } else {
                        data.takeLast(maxData).forEach {
                            builder.append("${it.time},${it.value}\n")
                        }
                    }
                    ctx.result(builder.toString())
                } else {
                    if (maxData == -1) {
                        ctx.result(data.joinToString { "(${it.time}; ${it.value})" })
                    } else {
                        ctx.result(data.takeLast(maxData).joinToString { "(${it.time}; ${it.value})" })
                    }
                }
            }
        }
    }

    fun update(ctx: Context) {
        val data = ctx.queryParam("value", "XX")!!.toFloat()
        getStatName(ctx) { name ->
            stats.updateStat(name, data)
        }
    }

    fun list(ctx: Context) {
        ctx.result(stats.getStatNames().toString())
    }

    private fun getStatName(ctx: Context, handler: (name: String) -> Unit) {
        val name = ctx.queryParam("name", "")!!
        if (name == "") {
            ctx.status(404)
            ctx.result("No stat given")
        } else {
            handler(name)
        }
    }

}