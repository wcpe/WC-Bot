package top.wcpe.wcbot.nukkit

import cn.nukkit.Server
import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.WCBotApi

/**
 * 由 WCPE 在 2021/12/24 23:44 创建
 *
 * Created by WCPE on 2021/12/24 23:44
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class NukkitTask {

    init {
        //在线时间
        Server.getInstance().scheduler.scheduleRepeatingTask(
            WCBot.instance, {
                for ((_, value) in Server.getInstance().onlinePlayers) {
                    WCBotApi.useSaveGamePlayerData(value.name) {
                        it.playerOnlineTime++
                        return@useSaveGamePlayerData it
                    }
                }
            }, 20, true
        )

    }

}