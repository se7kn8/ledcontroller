import implementation.ColorImplementation
import io.javalin.http.Context
import java.awt.Color
import java.util.*

class LightningController(val color: ColorImplementation, properties: Properties) {

    private val startColor = fromHex(properties.getProperty("start_color"))


    init {
        color.setColor(startColor, 1000)
    }

    fun getCurrentColor(ctx: Context) {
        ctx.result(toHex(color.currentColor))
    }

    fun setColor(ctx: Context) {
        if (ctx.queryParam("time", "-1")!!.toInt() > 0) {
            color.setColor(fromHex("#" + ctx.queryParam("color", "000000")!!.replace("#", "")), ctx.queryParam("time")!!.toInt())
        } else {
            color.setColor(fromHex("#" + ctx.queryParam("color", "000000")!!.replace("#", "")))
        }
    }

    fun reset(ctx: Context) {
        if (ctx.queryParam("time", "-1")!!.toInt() > 0) {
            color.setColor(startColor, ctx.queryParam("time")!!.toInt())
        } else {
            color.setColor(startColor)
        }
        ctx.result(toHex(startColor))
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

}