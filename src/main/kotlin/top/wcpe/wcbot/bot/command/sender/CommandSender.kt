package top.wcpe.wcbot.bot.command.sender

import net.mamoe.mirai.message.data.Message
import top.wcpe.wcbot.entity.QQMemberData

/**
 * 由 WCPE 在 2021/11/24 20:28 创建
 * Created by WCPE on 2021/11/24 20:28
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
interface CommandSender {

    fun getQQMemberData(): QQMemberData

    fun hasPermission(permission: String): Boolean

    fun sendMessage(message: String)

    fun sendMessage(message: Message)

}