package top.wcpe.wcbot.bot.command.sender

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.*
import top.wcpe.wcbot.entity.QQMemberData

/**
 * 由 WCPE 在 2021/11/26 17:40 创建
 * Created by WCPE on 2021/11/26 17:40
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
class UserSender(qqMemberData: QQMemberData, var event: UserMessageEvent) : PermissionUser(qqMemberData) {

    override fun getQQMemberData(): QQMemberData {
        return qqMemberData
    }

    override fun sendMessage(message: String) {
        sendMessage(message.toPlainText())
    }

    override fun sendMessage(message: Message) {
       runBlocking {
            event.sender.sendMessage(message)
        }
    }
}