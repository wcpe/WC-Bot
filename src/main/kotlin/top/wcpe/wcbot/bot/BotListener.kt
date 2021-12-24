package top.wcpe.wcbot.bot


import cn.nukkit.Server
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.WCBotApi
import top.wcpe.wcbot.bot.command.BotCommandManager
import top.wcpe.wcbot.bot.command.sender.GroupSender
import top.wcpe.wcbot.bot.command.sender.UserSender
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
        clearListener()

        listenerList.add(GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { WCBot.serverData.enableQQGroup.contains(it.group.id) }
            .subscribeAlways<GroupMessageEvent> { event ->
                val contentToString = event.message.contentToString()
                WCBot.instance.customReplyConfig.getString(contentToString).let {
                    if (it.isNotEmpty()) {
                        event.group.sendMessage(it)
                    }
                }
                if (contentToString.startsWith("/")) {
                    if (!BotCommandManager.dispatch(
                            GroupSender(WCBot.dataManager.getQQMemberData(event.sender.id), event),
                            contentToString
                        )
                    ) event.group.sendMessage("未知命令!")
                }
            })

        listenerList.add(GlobalEventChannel.filter { it is FriendMessageEvent || it is GroupTempMessageEvent }
            .subscribeAlways<UserMessageEvent> { event ->
                val contentToString = event.message.contentToString()
                WCBot.instance.customReplyConfig.getString("custom-reply.$contentToString").let {
                    if (it.isNotEmpty()) {
                        event.sender.sendMessage(it)
                    }
                }
                if (contentToString.startsWith("/"))
                    if (!BotCommandManager.dispatch(
                            UserSender(WCBot.dataManager.getQQMemberData(event.sender.id), event),
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

                WCBot.botManager.sendMessageToEnableGroups(
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
    }
}