package top.wcpe.wcbot.nukkit

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerChatEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import top.wcpe.wcbot.WCBot
import top.wcpe.wcpelib.common.utils.string.StringUtil

/**
 * 由 WCPE 在 2021/12/2 20:08 创建
 *
 * Created by WCPE on 2021/12/2 20:08
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class NukkitListener : Listener {

    @EventHandler
    fun playerJoinServer(e: PlayerJoinEvent) {
        if (!WCBot.instance.config.getBoolean("server-join-player.enable")) {
            return
        }
        WCBot.botManager.sendMessageToEnableGroups(
            StringUtil.replaceString(
                WCBot.instance.config.getString("server-join-player.message"),
                "player_name:${e.player.name}"
            )
        )
    }

    @EventHandler
    fun playerQuitServer(e: PlayerQuitEvent) {
        if (!WCBot.instance.config.getBoolean("server-quit-player.enable")) {
            return
        }
        WCBot.botManager.sendMessageToEnableGroups(
            StringUtil.replaceString(
                WCBot.instance.config.getString("server-quit-player.message"),
                "player_name:${e.player.name}"
            )
        )
    }

    @EventHandler
    fun serverToQQGroupChat(e: PlayerChatEvent) {
        if (!WCBot.instance.config.getBoolean("server-to-qq-group.enable")) {
            return
        }
        var msg = e.message
        WCBot.instance.config.getString("server-to-qq-group.start-message").let {
            if (it.isNotEmpty() && !msg.startsWith(it)) {
                return
            }
        }

        if (WCBot.instance.config.getBoolean("server-to-qq-group.remove-color")) {
            msg = msg.replace("§[0-9]", "")
        }

        if (WCBot.instance.config.getBoolean("server-to-qq-group.send-success-tip.enable")) {
            e.player.sendMessage(WCBot.instance.config.getString("server-to-qq-group.send-success-tip.message"))
        }

        WCBot.botManager.sendMessageToEnableGroups(
            StringUtil.replaceString(
                WCBot.instance.config.getString("server-to-qq-group.format"), "player:${e.player.name}",
                "chat:$msg"
            )
        )
    }


}