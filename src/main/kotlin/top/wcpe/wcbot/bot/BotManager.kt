package top.wcpe.wcbot.bot

import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.bot.command.sender.CommandSender
import top.wcpe.wcbot.nukkit.NukkitConsoleSender
import java.io.File

/**
 * 由 WCPE 在 2021/11/26 22:28 创建
 * Created by WCPE on 2021/11/26 22:28
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class BotManager(
    private var selectBot: Bot? = null,
    private val botMap: MutableMap<String, Bot> = mutableMapOf()
) {

    private val logger = WCBot.instance.logger

    init {
        reload()
    }

    private var nukkitConsoleSender: NukkitConsoleSender = NukkitConsoleSender()

    fun getNukkitConsoleSender(commandSender: CommandSender): NukkitConsoleSender {
        nukkitConsoleSender.commanderSender = commandSender
        return nukkitConsoleSender
    }

    fun reload() {
        botMap.clear()
        var botManagerSection = WCBot.instance.config.getSection("bot-manager")
        var settingSection = botManagerSection.getSection("setting")
        for (key in settingSection.getKeys(false)) {
            logger.info("机器人 $key -> ${settingSection.getLong("$key.account")} 开始读取")
            botMap[key] =
                BotFactory.newBot(settingSection.getLong("$key.account"), settingSection.getString("$key.password")) {
                    workingDir = WCBot.instance.dataFolder
                    fileBasedDeviceInfo("device.json")
                    redirectNetworkLogToDirectory(File(workingDir, "network-logs"))
                    redirectBotLogToDirectory(File(workingDir, "bot-logs"))
                }.also {
                    if (settingSection.getBoolean("$key.auto-login")) {
                        logger.info("机器人 $key 开启自动登录 尝试登录...")
                        runBlocking {
                            it.login()
                        }
                        if (it.isOnline) {
                            logger.info("机器人 $key 登录成功!")
                        } else {
                            logger.info("机器人 $key 登录失败!")
                        }
                    }
                }
        }
        selectBot = botMap[botManagerSection.getString("default")]
        logger.info("默认使用机器人 ${botManagerSection.getString("default")}")
        var sendLoadFinishTipSection = WCBot.instance.config.getSection("send-load-finish-tip")
        if (sendLoadFinishTipSection.getBoolean("enable")) {
            sendMessageToEnableGroups(sendLoadFinishTipSection.getStringList("message")
                .joinToString(System.lineSeparator()))
        }
    }

    fun useSelectBot(callBack: (Bot) -> Unit) {
        selectBot?.let(callBack)
    }

    fun sendMessageToEnableGroups(msg: String) {
        for (l in WCBot.serverData.enableQQGroup) {
            WCBot.botManager.useSelectBot {
                it.getGroup(l)?.let {
                    runBlocking {
                        it.sendMessage(msg)
                    }
                }
            }
        }
    }
}