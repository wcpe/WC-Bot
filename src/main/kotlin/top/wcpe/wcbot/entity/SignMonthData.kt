package top.wcpe.wcbot.entity

import cn.nukkit.utils.ConfigSection

/**
 * 由 WCPE 在 2021/12/26 20:09 创建
 *
 * Created by WCPE on 2021/12/26 20:09
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class SignMonthData(val month: String, val signDay: MutableMap<String, Long> = mutableMapOf()) {
    constructor(month: String, configSection: ConfigSection) : this(month){
        if (configSection.exists("signDay")) {
            val signDaySection = configSection.getSection("signDay")
            for (key in signDaySection.getKeys(false)) {
                signDay[key] = signDaySection.getLong(key)
            }
        }
    }

    fun serialize(): ConfigSection {
        val configSection = ConfigSection()
        configSection.set("month", month)
        for (entry in signDay.entries) {
            configSection["signDay.${entry.key}"] = entry.value
        }
        return configSection
    }

}