package implementation.mode

import implementation.ColorImplementation

interface Mode {

    // Will be called from another thread
    fun start(color: ColorImplementation, multiplier: Float)


    // Will be call from main thread
    fun stop()

    fun getName(): String

}