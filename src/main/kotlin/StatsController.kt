import io.javalin.http.Context

class StatsController {

    private val stats = HashMap<String, MutableList<Pair<Long, Float>>>()

    fun remove(ctx: Context) {
        getStatName(ctx) {
            if (stats.containsKey(it)) {
                stats.remove(it)
            } else {
                ctx.status(404)
            }
        }
    }

    fun getLatest(ctx: Context) {
        getStat(ctx) { data ->
            ctx.result(data.maxBy { it.first }!!.toString())
        }
    }

    fun getAll(ctx: Context) {
        val maxData = ctx.queryParam("max", "-1")!!.toInt()

        getStat(ctx) { data ->
            if (maxData == -1) {
                ctx.result(data.joinToString())
            } else {
                ctx.result(data.takeLast(maxData).joinToString())
            }
        }
    }

    fun update(ctx: Context) {
        val data = ctx.body().toFloat();
        getStatName(ctx) { name ->
            stats.putIfAbsent(name, ArrayList())
            getStat(ctx) {
                it.add(Pair(System.currentTimeMillis(), data))
            }
        }
    }

    fun list(ctx: Context) {
        ctx.result(stats.keys.joinToString())
    }

    private fun getStat(ctx: Context, handler: (stats: MutableList<Pair<Long, Float>>) -> Unit) {
        getStatName(ctx) {
            if (stats.containsKey(it)) {
                handler(stats[it]!!)
            } else {
                ctx.status(404)
                ctx.result("Stat not found")
            }
        }
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