package top.wcpe.wcbot

import cn.nukkit.Server
import top.wcpe.wcpelib.common.utils.datatime.TimeUtil
import top.wcpe.wcpelib.common.utils.string.StringUtil
import top.wcpe.wcpelib.nukkit.otherpluginapi.economyapi.EconomyUtil
import javax.script.Invocable
import javax.script.ScriptEngineManager
import kotlin.jvm.Throws

/**
 * 由 WCPE 在 2021/12/24 23:43 创建
 *
 * Created by WCPE on 2021/12/24 23:43
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
object WCBotFunction {

    @Throws(ClassCastException::class)
    fun invokeJavaScriptFunction(javaScript: String, function: String, vararg args: Any): Any {
        val engine = ScriptEngineManager().getEngineByName("nashorn")
        engine.eval(javaScript)
        return (engine as Invocable).invokeFunction(function, *args)
    }

    fun formatPlayerInfoList(playerName: String, info: MutableList<String>): MutableList<String> {
        val gamePlayerData = WCBotApi.getGamePlayerData(playerName)
        val offlinePlayer = Server.getInstance().getOfflinePlayer(playerName)

        val messageList = mutableListOf<String>()
        for (s in info) {
            messageList.add(
                StringUtil.replaceString(
                    s,
                    "player_name:$playerName",
                    "money:${
                        EconomyUtil.getEconomy()?.run {
                            myMoney(offlinePlayer)
                        }
                    }",
                    "online:${if (offlinePlayer.isOnline) "在线" else "离线"}",
                    "offline_time:${TimeUtil.stampToTime(offlinePlayer.lastPlayed * 1000)}",
                    "first_time:${TimeUtil.stampToTime(offlinePlayer.firstPlayed * 1000)}",
                    "online_time:${TimeUtil.formatDate(gamePlayerData.playerOnlineTime * 1000)}"
                )
            )
        }
        return messageList
    }

}