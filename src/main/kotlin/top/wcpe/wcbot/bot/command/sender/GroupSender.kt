package top.wcpe.wcbot.bot.command.sender

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.at
import top.wcpe.wcbot.entity.QQMemberData

/**
 * 由 WCPE 在 2021/11/26 18:42 创建
 * Created by WCPE on 2021/11/26 18:42
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
class GroupSender(qqMemberData: QQMemberData, var event: GroupMessageEvent) : PermissionUser(qqMemberData) {

    override fun getQQMemberData(): QQMemberData {
        return qqMemberData
    }

    override fun sendMessage(message: String) {
        sendMessage(event.sender.at().plus(message))
    }

    override fun sendMessage(message: Message) {
       runBlocking {
            event.group.sendMessage(message)
        }
    }


}