package implementation.mode

import implementation.ColorImplementation
import java.awt.Color

class RainbowMode : Mode {

    private var running = true
    private var latestColor: Color = Color.BLACK

    override fun start(color: ColorImplementation, multiplier: Int) {
        running = true
        latestColor = color.currentColor
        var value = 0.0f
        color.setColor(Color.getHSBColor(value, 1f, 1f), 1000)
        Thread.sleep(1000)
        while (running) {
            value += 0.001f
            color.setColor(Color.getHSBColor(value, 1f, 1f))
            Thread.sleep(50)
        }
        color.setColor(latestColor, 1000)
    }

    override fun stop() {
        running = false
    }


    override fun getName(): String {
        return "Rainbow"
    }

}