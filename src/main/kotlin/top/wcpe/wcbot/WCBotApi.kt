package top.wcpe.wcbot

import top.wcpe.wcbot.entity.QQMemberData
import java.util.function.Consumer
import java.util.function.UnaryOperator

/**
 * 由 WCPE 在 2021/12/18 18:47 创建
 *
 * Created by WCPE on 2021/12/18 18:47
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
object WCBotApi {
    fun getQQMemberData(qq: Long): QQMemberData {
        return WCBot.dataManager.getQQMemberData(qq)
    }

    fun saveQQMemberData(qqMemberData: QQMemberData): Boolean {
        return WCBot.dataManager.saveQQMemberData(qqMemberData)
    }

    fun useQQMemberData(qq: Long, callBack: Consumer<QQMemberData>) {
        return WCBot.dataManager.useQQMemberData(qq, callBack)
    }

    fun useSaveQQMemberData(qq: Long, callBack: UnaryOperator<QQMemberData>): Boolean {
        return WCBot.dataManager.useSaveQQMemberData(qq, callBack)
    }
}