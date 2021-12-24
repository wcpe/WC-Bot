package top.wcpe.wcbot

import cn.nukkit.Server

/**
 * 由 WCPE 在 2021/12/20 8:33 创建
 *
 * Created by WCPE on 2021/12/20 8:33
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class HookOtherPlugin {

    private val logger = WCBot.instance.logger
    var tipsApi: Boolean = false

    init {
        reload()
    }

    fun reload() {
        tipsApi = hookLogger("Tips")
    }

    private fun hookLogger(hookName: String): Boolean {
        Server.getInstance().pluginManager.getPlugin(hookName)?.let {
            logger.info("Hook $hookName 成功!")
            true
        }
        logger.info("Hook $hookName 失败 该插件未加载!")
        return false
    }
}