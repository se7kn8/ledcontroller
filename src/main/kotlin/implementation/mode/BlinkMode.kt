package implementation.mode

import implementation.ColorImplementation
import java.awt.Color

class BlinkMode : Mode {

    private var running = true

    override fun start(color: ColorImplementation, multiplier: Float) {
        running = true
        val time = (1000f * multiplier).toLong()
        while (running) {
            color.setColor(Color.RED)
            Thread.sleep(time)
            color.setColor(Color.GREEN)
            Thread.sleep(time)
            color.setColor(Color.BLUE)
            Thread.sleep(time)
        }
    }

    override fun stop() {
        running = false
    }

    override fun getName(): String {
        return "Blink"
    }
}