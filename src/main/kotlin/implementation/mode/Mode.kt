package implementation.mode

import implementation.ColorImplementation

interface Mode {

    // Will be called from a coroutine
    suspend fun start(color: ColorImplementation, multiplier: Float)

    fun getName(): String

}