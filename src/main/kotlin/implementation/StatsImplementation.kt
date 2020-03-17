package implementation

import PropertiesHandler
import org.apache.logging.log4j.LogManager
import java.io.PrintStream
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

data class StatPair(val time: Long, val value: Float)

data class Stat(val name: String, val data: MutableList<StatPair> = ArrayList()) {

    fun updateData(value: Float) {
        data.add(StatPair(System.currentTimeMillis(), value))
    }

}

class StatsImplementation(properties: PropertiesHandler) {

    private val logger = LogManager.getLogger()
    private val saveDir = Paths.get(properties.properties.getProperty("stats_save_directory"))
    private val stats: MutableSet<Stat> = HashSet()

    init {
        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir)
        }
        logger.info("Stats save dir: {}", saveDir)
        Files.newDirectoryStream(saveDir) {
            it.fileName.toString().contains("__stat__.txt")
        }.forEach {
            logger.info("Found stat file: {}", it.toAbsolutePath().toString())

            val stat = Stat(it.fileName.toString().replace("__stat__.txt", ""))

            val reader = Files.newBufferedReader(it)
            try {
                while (true) {
                    val line = reader.readLine() ?: break
                    val pair = line.split(":")
                    stat.data.add(StatPair(pair[0].toLong(), pair[1].toFloat()))
                }
            } catch (e: Exception) {
                logger.warn("Error while loading stats file {}", e, it.toAbsolutePath().toString())
            } finally {
                reader.close()
            }
            stats.add(stat)

        }
    }

    fun removeStat(name: String) {
        stats.removeIf { it.name == name }
    }

    fun updateStat(name: String, value: Float) {
        val stat = stats.find { it.name == name }
        if (stat == null) {
            val newStat = Stat(name)
            newStat.updateData(value)
            stats.add(newStat)
            saveStat(newStat)
        } else {
            stat.updateData(value)
            saveStat(stat)
        }
    }

    fun getStatData(name: String): List<StatPair>? {
        return stats.find { it.name == name }?.data
    }

    fun getStatNames(): Set<String> {
        return stats.map { it.name }.toSet()
    }

    @Synchronized
    private fun saveStat(stat: Stat) {
        val saveFile = saveDir.resolve(stat.name + "__stat__.txt")
        val ps = PrintStream(Files.newOutputStream(saveFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND), false, "UTF-8")
        try {
            val latestValue = stat.data.last()
            ps.println("${latestValue.time}:${latestValue.value}")
            ps.flush()
        } catch (e: Exception) {
            logger.warn("Error while saving {}", e, saveFile.toAbsolutePath().toString())
        } finally {
            ps.close()
        }
    }
}