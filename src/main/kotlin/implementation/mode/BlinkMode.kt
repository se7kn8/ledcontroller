package implementation.mode

import implementation.ColorImplementation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.awt.Color
import kotlin.coroutines.coroutineContext

class BlinkMode : Mode {


    override suspend fun start(color: ColorImplementation, multiplier: Float) {
        val time = (1000f * multiplier).toLong()
        while (coroutineContext.isActive) {
            color.setColor(Color.RED)
            delay(time)
            color.setColor(Color.GREEN)
            delay(time)
            color.setColor(Color.BLUE)
            delay(time)
        }
    }

    override fun getName(): String {
        return "Blink"
    }
}