package implementation.mode

import implementation.ColorImplementation

interface Mode {

    // Will be called from another thread
    suspend fun start(color: ColorImplementation, multiplier: Float)

    fun getName(): String

}