package implementation.mode

import implementation.ColorImplementation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.awt.Color
import kotlin.coroutines.coroutineContext

class RainbowMode : Mode {

    private var latestColor: Color = Color.BLACK

    override suspend fun start(color: ColorImplementation, multiplier: Float) {
        latestColor = color.currentColor
        var value = 0.0f
        color.setColor(Color.getHSBColor(value, 1f, 1f), 1000)

        delay(1000)

        try {
            while (coroutineContext.isActive) {
                value += 0.001f
                color.setColor(Color.getHSBColor(value, 1f, 1f))
                delay((50f * multiplier).toLong())
            }
            color.setColor(latestColor, 1000)
        } finally {
            color.setColor(latestColor, 1000)
        }
    }


    override fun getName(): String {
        return "Rainbow"
    }

}