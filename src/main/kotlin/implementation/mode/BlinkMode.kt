package implementation.mode

import implementation.ColorImplementation
import java.awt.Color

class BlinkMode : Mode {

    private var running = true

    override fun start(color: ColorImplementation, multiplier: Int) {
        running = true
        while (running) {
            color.setColor(Color.RED)
            Thread.sleep(1000)
            color.setColor(Color.GREEN)
            Thread.sleep(1000)
            color.setColor(Color.BLUE)
            Thread.sleep(1000)
        }
    }

    override fun stop() {
        running = false
    }

    override fun getName(): String {
        return "Blink"
    }
}