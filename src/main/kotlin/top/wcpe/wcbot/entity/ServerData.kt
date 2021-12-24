package top.wcpe.wcbot.entity

import cn.nukkit.utils.Config
import top.wcpe.wcbot.WCBot
import java.io.File

/**
 * 由 WCPE 在 2021/12/2 19:40 创建
 *
 * Created by WCPE on 2021/12/2 19:40
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class ServerData(
    var ownerQQ: Long? = null,
    var enableQQGroup: MutableList<Long> = mutableListOf()
) {
    constructor() : this(null) {
        load()
    }

    fun load() {
        val cfg = Config(file)
        this.ownerQQ = cfg.getLong("ownerQQ")
        this.enableQQGroup = cfg.getLongList("enableQQGroup")
    }

    private val file = File(WCBot.instance.dataFolder, "serverData.yml")

    fun save(): Boolean {
        val cfg = Config(file)
        cfg["ownerQQ"] = ownerQQ
        cfg["enableQQGroup"] = enableQQGroup
        return cfg.save()
    }
}