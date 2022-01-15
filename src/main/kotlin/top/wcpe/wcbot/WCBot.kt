package top.wcpe.wcbot

import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import top.wcpe.wcbot.bot.BotCommands
import top.wcpe.wcbot.bot.BotListener
import top.wcpe.wcbot.bot.BotManager
import top.wcpe.wcbot.data.FileDataManager
import top.wcpe.wcbot.data.IDataManager
import top.wcpe.wcbot.entity.ServerData
import top.wcpe.wcbot.nukkit.NukkitCommands
import top.wcpe.wcbot.nukkit.NukkitListener
import top.wcpe.wcbot.nukkit.NukkitTask
import top.wcpe.wcpelib.nukkit.manager.MessageManager
import java.nio.file.Files
import java.nio.file.Path

/**
 * 由 WCPE 在 2021/11/24 20:14 创建
 * Created by WCPE on 2021/11/24 20:14
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
class WCBot : PluginBase() {

    companion object {

        @JvmStatic
        lateinit var instance: WCBot

        @JvmStatic
        lateinit var messageManager: MessageManager

        @JvmStatic
        lateinit var dataManager: IDataManager

        @JvmStatic
        lateinit var botManager: BotManager

        @JvmStatic
        lateinit var serverData: ServerData

    }


    private lateinit var configPath: Path
    private lateinit var config: Config
    private lateinit var customReplyPath: Path
    lateinit var customReplyConfig: Config
    private lateinit var groupsPath: Path
    lateinit var groupsConfig: Config
    private lateinit var commandsPath: Path
    lateinit var commandsConfig: Config

    override fun saveDefaultConfig() {
        if (Files.notExists(configPath)) {
            this.saveResource("config.yml", false)
        }
        if (Files.notExists(customReplyPath)) {
            this.saveResource("customReply.yml", false)
        }
        if (Files.notExists(groupsPath)) {
            this.saveResource("groups.yml", false)
        }
        if (Files.notExists(commandsPath)) {
            this.saveResource("commands.yml", false)
        }
    }

    override fun getConfig(): Config {
        return config
    }

    override fun reloadConfig() {
        config.reload()
        groupsConfig.reload()
        customReplyConfig.reload()
        commandsConfig.reload()
    }

    private fun initConfig() {
        configPath = dataFolder.toPath().resolve("config.yml")
        customReplyPath = dataFolder.toPath().resolve("customReply.yml")
        groupsPath = dataFolder.toPath().resolve("groups.yml")
        commandsPath = dataFolder.toPath().resolve("commands.yml")
        saveDefaultConfig()
        config = Config(configPath.toFile())
        customReplyConfig = Config(customReplyPath.toFile())
        groupsConfig = Config(groupsPath.toFile())
        commandsConfig = Config(commandsPath.toFile())
    }

    lateinit var botCommands: BotCommands
    lateinit var botListener: BotListener
    lateinit var nukkitCommands: NukkitCommands
    lateinit var hookOtherPlugin: HookOtherPlugin

    override fun onEnable() {
        val start = System.currentTimeMillis()
        logger.info("onEnable...")
        instance = this
        logger.info("初始化各项配置!")
        initConfig()
        logger.info("读取服务器数据!")
        serverData = ServerData()
        logger.info("创建消息管理器!")
        messageManager = MessageManager(this, "CN")
        logger.info("创建数据管理器!")
        dataManager = FileDataManager()
        logger.info("注册机器人管理器!")
        botManager = BotManager()
        logger.info("注册机器人指令!")
        botCommands = BotCommands()
        logger.info("注册机器人监听器!")
        botListener = BotListener()
        logger.info("注册 Nukkit 指令!")
        nukkitCommands = NukkitCommands()
        logger.info("注册 Nukkit 任务!")
        NukkitTask()
        logger.info("注册 Nukkit 监听器!")
        server.pluginManager.registerEvents(NukkitListener(), this)
        logger.info("启动 BStats!")
        bStats(this, 6812)

        logger.info("开始 Hook 其他插件!")
        hookOtherPlugin = HookOtherPlugin()

        server.consoleSender.sendMessage("§1  _       __   ______           ____           __ ")
        server.consoleSender.sendMessage("§2 | |     / /  / ____/          / __ )  ____   / /_")
        server.consoleSender.sendMessage("§3 | | /| / /  / /      ______  / __  | / __ \\ / __/")
        server.consoleSender.sendMessage("§4 | |/ |/ /  / /___   /_____/ / /_/ / / /_/ // /_  ")
        server.consoleSender.sendMessage("§5 |__/|__/   \\____/          /_____/  \\____/ \\__/  ")
        server.consoleSender.sendMessage("§6                                                  ")
        server.consoleSender.sendMessage("§6              §aServerVersion: " + server.version + "      ")
        server.consoleSender.sendMessage("§6              §aPluginVersion: ${description.version}      ")
        server.consoleSender.sendMessage("§a如果您喜欢这个插件 请在 Mcbbs 帖子: https://www.mcbbs.net/thread-971000-1-1.html 下方给我评个分~")
        logger.info("load time: " + (System.currentTimeMillis() - start) + " ms")
    }


    override fun onDisable() {
        logger.info("onDisable...")
    }
}