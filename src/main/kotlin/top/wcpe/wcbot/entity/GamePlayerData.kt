package top.wcpe.wcbot.entity

import cn.nukkit.utils.Config

/**
 * 由 WCPE 在 2021/11/24 12:43 创建
 * Created by WCPE on 2021/11/24 12:43
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class GamePlayerData(val playerName: String, var bindQQ: Long? = null, val playerOnlineTime: Long = 0) {

    constructor(playerName: String, config: Config):this(playerName,config.getLong("bindQQ"),config.getLong("playerOnlineTime"))

    fun serialize(): Config {
        var config = Config()
        config.set("playerName", playerName)
        config.set("bindQQ", bindQQ)
        config.set("playerOnlineTime", playerOnlineTime)
        return config
    }

}