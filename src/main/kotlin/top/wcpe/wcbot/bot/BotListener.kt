package top.wcpe.wcbot.bot


import cn.nukkit.Server
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.WCBotApi
import top.wcpe.wcbot.bot.command.BotCommandManager
import top.wcpe.wcbot.bot.command.sender.CommandSender
import top.wcpe.wcbot.bot.command.sender.GroupSender
import top.wcpe.wcbot.bot.command.sender.UserSender
import top.wcpe.wcbot.bot.utils.chat.ChatAcceptParameterManager
import top.wcpe.wcpelib.common.utils.string.StringUtil

/**
 * 由 WCPE 在 2021/12/14 10:11 创建
 *
 * Created by WCPE on 2021/12/14 10:11
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class BotListener {

    private val logger = WCBot.instance.logger

    private var listenerList = mutableListOf<Listener<*>>()

    init {
        reload()
    }

    private fun clearListener() {
        for (listener in listenerList) {
            listener.cancel()
        }
        listenerList.clear()
    }

    fun reload() {
        if (!WCBot.instance.config.getBoolean("bot-manager.enable-listener")) {
            logger.info("已关闭插件监听器")
            return
        }
        clearListener()

        val chatAccept = { contentToString: String, commandSender: CommandSender ->
            commandSender.getQQMemberData().run {
                ChatAcceptParameterManager.memberTask[qq]?.let {
                    if (it.timeStamp < System.currentTimeMillis()) {
                        ChatAcceptParameterManager.memberTask.remove(qq)
                        return@run
                    }

                    if (it.cancelJudgeTask(commandSender, contentToString)) {
                        it.cancelSuccessTask(commandSender, contentToString)
                        ChatAcceptParameterManager.memberTask.remove(qq)
                        return@run
                    }
                    if (it.judge(commandSender, contentToString)) {
                        it.judgeTrueTask(commandSender, contentToString)
                        ChatAcceptParameterManager.memberTask.remove(qq)
                        return@run
                    }
                    it.judgeFalseTask(commandSender, contentToString)
                }
            }
        }

        listenerList.add(GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { WCBot.serverData.enableQQGroup.contains(it.group.id) }
            .subscribeAlways<GroupMessageEvent> { event ->
                val contentToString = event.message.contentToString()
                chatAccept(
                    contentToString,
                    GroupSender(WCBotApi.getQQMemberData(event.sender.id), event)
                )
                WCBot.instance.customReplyConfig.getString(contentToString).let {
                    if (it.isNotEmpty()) {
                        event.group.sendMessage(it)
                    }
                }
                if (contentToString.startsWith(WCBot.instance.config.getString("bot-manager.command-start"))) {
                    if (!BotCommandManager.dispatch(
                            GroupSender(WCBotApi.getQQMemberData(event.sender.id), event),
                            contentToString
                        )
                    ) event.group.sendMessage("未知命令!")
                }
            })

        listenerList.add(GlobalEventChannel.filter { it is FriendMessageEvent || it is GroupTempMessageEvent }
            .subscribeAlways<UserMessageEvent> { event ->
                val contentToString = event.message.contentToString()
                chatAccept(
                    contentToString,
                    UserSender(WCBotApi.getQQMemberData(event.sender.id), event)
                )
                WCBot.instance.customReplyConfig.getString("custom-reply.$contentToString").let {
                    if (it.isNotEmpty()) {
                        event.sender.sendMessage(it)
                    }
                }
                if (contentToString.startsWith(WCBot.instance.config.getString("bot-manager.command-start")))
                    if (!BotCommandManager.dispatch(
                            UserSender(WCBotApi.getQQMemberData(event.sender.id), event),
                            contentToString
                        )
                    ) event.sender.sendMessage("未知命令!")
            })

        listenerList.add(GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { WCBot.serverData.enableQQGroup.contains(it.group.id) }
            .subscribeAlways<GroupMessageEvent> { event ->
                val contentToString = event.message.contentToString()

                if (!WCBot.instance.config.getBoolean("qq-group-to-server.enable")) {
                    return@subscribeAlways
                }
                WCBot.instance.config.getString("qq-group-to-server.start-message").let {
                    if (it.isNotEmpty() && !contentToString.startsWith(it)) {
                        return@subscribeAlways
                    }
                }

                if (WCBot.instance.config.getBoolean("qq-group-to-server.send-success-tip.enable")) {
                    event.group.sendMessage(WCBot.instance.config.getString("qq-group-to-server.send-success-tip.message"))
                }

                Server.getInstance().broadcastMessage(
                    StringUtil.replaceString(
                        WCBot.instance.config.getString("qq-group-to-server.format"),
                        "qq:${sender.id}",
                        "qq_nick:${sender.nick}", "chat:$contentToString"
                    )
                )

            })

        listenerList.add(GlobalEventChannel
            .filterIsInstance<MemberLeaveEvent>()
            .filter { WCBot.serverData.enableQQGroup.contains(it.group.id) }
            .subscribeAlways<MemberLeaveEvent> { event ->
                if (Server.getInstance().hasWhitelist()) {
                    WCBotApi.getQQMemberData(event.member.id).run {
                        if (bindGamePlayerName.isNotEmpty()) {
                            if (Server.getInstance().isWhitelisted(bindGamePlayerName)) {
                                Server.getInstance().removeWhitelist(bindGamePlayerName)
                                event.group.sendMessage("QQ: ${event.member.id} 绑定玩家: $bindGamePlayerName 退出群聊 白名单自动去除!")
                            }
                        }
                    }
                }
            })

        logger.info("插件监听器注册完成!")
    }
}