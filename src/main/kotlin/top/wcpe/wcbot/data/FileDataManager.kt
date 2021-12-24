package top.wcpe.wcbot.data

import cn.nukkit.utils.Config
import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.entity.GamePlayerData
import top.wcpe.wcbot.entity.QQMemberData
import java.nio.file.Files
import java.nio.file.Path


/**
 * 由 WCPE 在 2021/11/26 22:19 创建
 * Created by WCPE on 2021/11/26 22:19
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
class FileDataManager : IDataManager {

    private val qqMemberDataMap: MutableMap<Long, QQMemberData> = mutableMapOf()

    private fun getQQMemberDataDirPath(): Path {
        val resolve = WCBot.instance.dataFolder.toPath().resolve("QQMemberData")
        if (Files.notExists(resolve)) {
            Files.createDirectories(resolve)
        }
        return resolve
    }

    private fun getQQMemberDataFilePath(qq: Long): Path {
        val resolve = getQQMemberDataDirPath().resolve("$qq.yml")
        if (Files.notExists(resolve)) {
            Files.createFile(resolve)
        }
        return resolve
    }

    override fun getQQMemberData(qq: Long): QQMemberData {
        var qqMemberData = qqMemberDataMap[qq]
        if (qqMemberData != null) {
            return qqMemberData
        }
        qqMemberData = QQMemberData(qq, Config(getQQMemberDataFilePath(qq).toFile()))
        qqMemberDataMap[qq] = qqMemberData
        return qqMemberData
    }

    override fun saveQQMemberData(qqMemberData: QQMemberData): Boolean {
        return qqMemberData.serialize().save(
            getQQMemberDataFilePath(
                qqMemberData.qq
            ).toFile()
        )
    }

    private val gamePlayerDataMap: MutableMap<String, GamePlayerData> = mutableMapOf()

    private fun getGamePlayerDataDirPath(): Path {
        val resolve = WCBot.instance.dataFolder.toPath().resolve("GamePlayerData")
        if (Files.notExists(resolve)) {
            Files.createDirectories(resolve)
        }
        return resolve
    }

    private fun getGamePlayerDataFilePath(playerName: String): Path {
        val resolve = getGamePlayerDataDirPath().resolve("$playerName.yml")
        if (Files.notExists(resolve)) {
            Files.createFile(resolve)
        }
        return resolve
    }

    override fun getGamePlayerData(playerName: String): GamePlayerData {
        var gamePlayerData = gamePlayerDataMap[playerName]
        if (gamePlayerData != null) {
            return gamePlayerData
        }
        gamePlayerData = GamePlayerData(playerName, Config(getGamePlayerDataFilePath(playerName).toFile()))
        gamePlayerDataMap[playerName] = gamePlayerData
        return gamePlayerData
    }

    override fun saveGamePlayerData(gamePlayerData: GamePlayerData): Boolean {
        return gamePlayerData.serialize().save(
            getGamePlayerDataFilePath(
                gamePlayerData.playerName
            ).toFile()
        )
    }
}