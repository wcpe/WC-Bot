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
data class QQMemberData(
    val qq: Long,
    var bindGamePlayerName: String,
    var wcBotPoint: Double = 0.0,
    var permissionList: MutableList<String> = mutableListOf(),
    var permissionGroups: MutableList<String> = mutableListOf(),
    var signDayTimeStamp: MutableMap<String, Long> = mutableMapOf()
) {
    constructor(qq: Long, cfg: Config) : this(
        qq,
        cfg.getString("bindGamePlayerName"),
        cfg.getDouble("wcBotPoint"),
        cfg.getStringList("permissionList"), cfg.getStringList("permissionGroups")
    ) {
        if (cfg.exists("signDayTimeStamp")) {
            var signDayTimeStampSection = cfg.getSection("signDayTimeStamp")
            for (key in signDayTimeStampSection.getKeys(false)) {
                signDayTimeStamp[key] = signDayTimeStampSection.getLong(key)
            }
        }
    }

    fun serialize(): Config {
        val cfg = Config()
        cfg["qq"] = qq
        cfg["bindGamePlayerName"] = this.bindGamePlayerName
        cfg["wcBotPoint"] = wcBotPoint
        cfg["permissionList"] = permissionList
        cfg["permissionGroups"] = permissionGroups
        for (entry in signDayTimeStamp.entries) {
            cfg["signDayTimeStamp.${entry.key}"] = entry.value
        }
        return cfg
    }
}