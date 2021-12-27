package top.wcpe.wcbot.bot.utils.chat

import top.wcpe.wcbot.bot.command.sender.CommandSender
import top.wcpe.wcbot.bot.utils.chat.entity.ChatAcceptParameterTask

/**
 * 由 WCPE 在 2021/12/26 23:27 创建
 *
 * Created by WCPE on 2021/12/26 23:27
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
object ChatAcceptParameterManager {

    @JvmStatic
    val memberTask: MutableMap<Long, ChatAcceptParameterTask> = mutableMapOf()

    fun putChatAcceptParameterTask(
        commandSender: CommandSender,
        inputTime: Long,
        cancelJudgeTask: (commandSender: CommandSender, message: String) -> Boolean,
        cancelSuccessTask: (commandSender: CommandSender, message: String) -> Unit,
        judge: (commandSender: CommandSender, message: String) -> Boolean,
        judgeTrueTask: (commandSender: CommandSender, message: String) -> Unit,
        judgeFalseTask: (commandSender: CommandSender, message: String) -> Unit
    ): Boolean {
        memberTask[commandSender.getQQMemberData().qq]?.let {
            if (it.timeStamp < System.currentTimeMillis()) {
                memberTask.remove(commandSender.getQQMemberData().qq)
                return false
            }
        }
        memberTask[commandSender.getQQMemberData().qq] = ChatAcceptParameterTask(
            System.currentTimeMillis() + inputTime * 1000,
            cancelJudgeTask,
            cancelSuccessTask,
            judge,
            judgeTrueTask,
            judgeFalseTask
        )
        return true
    }
}