package top.wcpe.wcbot.nukkit

import kotlinx.coroutines.runBlocking
import top.wcpe.wcbot.WCBot
import top.wcpe.wcpelib.nukkit.command.CommandPlus
import top.wcpe.wcpelib.nukkit.command.entity.Command
import top.wcpe.wcpelib.nukkit.command.entity.CommandArgument

/**
 * 由 WCPE 在 2021/12/2 19:35 创建
 *
 * Created by WCPE on 2021/12/2 19:35
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class NukkitCommands {

    private val logger = WCBot.instance.logger

    init {
        val cp = CommandPlus.Builder("WC-Bot", WCBot.instance).aliases("wb").build()
        cp.registerSubCommand(
            Command.Builder("setOwner", "设置主人QQ").executeComponent { sender, args ->
                WCBot.serverData.ownerQQ = args[0].toLong()
                WCBot.serverData.save()
                sender.sendMessage("成功设置 ${args[0]} 为主人")
            }.args(CommandArgument.Builder("主人QQ").build()).build()
        )
        cp.registerSubCommand(
            Command.Builder("addGroups", "添加开启群聊").executeComponent { sender, args ->
                val enableQQGroup = WCBot.serverData.enableQQGroup
                val addQQGroup = args[0].toLong()
                if (enableQQGroup.contains(addQQGroup)) {
                    sender.sendMessage("群聊 $addQQGroup 已存在于开启列表中!")
                    return@executeComponent
                }
                enableQQGroup.add(addQQGroup)
                WCBot.serverData.save()
                sender.sendMessage("将 $addQQGroup 添加进开启群聊成功 当前列表共有 ${enableQQGroup.size} 个群聊")
            }.args(CommandArgument.Builder("QQ群号").build()).build()
        )
        cp.registerSubCommand(
            Command.Builder("delGroups", "删除开启群聊").executeComponent { sender, args ->
                val enableQQGroup = WCBot.serverData.enableQQGroup
                val delQQGroup = args[0].toLong()
                if (!enableQQGroup.contains(delQQGroup)) {
                    sender.sendMessage("群聊 $delQQGroup 不存在于开启列表中!")
                    return@executeComponent
                }
                enableQQGroup.remove(delQQGroup)
                WCBot.serverData.save()
                sender.sendMessage("将 $delQQGroup 从开启群聊中删除成功 当前列表共有 ${enableQQGroup.size} 个群聊")
            }.args(CommandArgument.Builder("QQ群号").build()).build()
        )
        cp.registerSubCommand(
            Command.Builder("listGroups", "查询开启群聊").executeComponent { sender, _ ->
                sender.sendMessage("当前开启群聊有")
                for (l in WCBot.serverData.enableQQGroup) {
                    sender.sendMessage("-> $l")
                }
            }.build()
        )

        cp.registerSubCommand(
            Command.Builder("sendGroup", "发送消息至QQ群").executeComponent { sender, args ->
                WCBot.botManager.useSelectBot {
                    runBlocking {
                        it.getGroup(args[0].toLong())?.run {
                            sendMessage(args[1])
                            sender.sendMessage("发送成功")
                        }
                    }
                    return@useSelectBot
                }
                sender.sendMessage("群 ${args[0]} 不存在")
            }.args(CommandArgument.Builder("QQ群号").build(), CommandArgument.Builder("消息").build()).build()
        )
        cp.registerSubCommand(
            Command.Builder("sendQQ", "发送消息至QQ").executeComponent { sender, args ->
                WCBot.botManager.useSelectBot {
                    runBlocking {
                        it.getFriend(args[0].toLong())?.run {
                            sendMessage(args[1])
                            sender.sendMessage("发送成功")
                        }
                    }
                    return@useSelectBot
                }
                sender.sendMessage("好友 ${args[0]} 不存在")
            }.args(CommandArgument.Builder("QQ号").build(), CommandArgument.Builder("消息").build()).build()
        )

        cp.registerSubCommand(
            Command.Builder("reload", "重载机器人").executeComponent { sender, _ ->
                sender.sendMessage("开始重载所有配置文件!")
                WCBot.instance.reloadConfig()
                sender.sendMessage("开始重载消息管理器!")
                WCBot.messageManager.reload()
                sender.sendMessage("开始重载服务器数据!")
                WCBot.serverData.reload()
                sender.sendMessage("开始重载机器人!")
                WCBot.botManager.reload()
                sender.sendMessage("开始重载机器人指令!")
                WCBot.instance.botCommands.reload()
                sender.sendMessage("开始重载机器人监听!")
                WCBot.instance.botListener.reload()
                sender.sendMessage("开始重载其他插件Hook!")
                WCBot.instance.hookOtherPlugin.reload()
                sender.sendMessage("重载完成!")
                logger.info("${sender.name}重载了配置文件")
            }.build()
        )
        cp.registerThis()
    }
}