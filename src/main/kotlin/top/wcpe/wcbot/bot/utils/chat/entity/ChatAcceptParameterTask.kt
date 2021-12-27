package top.wcpe.wcbot.bot.utils.chat.entity

import top.wcpe.wcbot.bot.command.sender.CommandSender

/**
 * 由 WCPE 在 2021/12/26 23:53 创建
 *
 * Created by WCPE on 2021/12/26 23:53
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class ChatAcceptParameterTask(
    val timeStamp: Long,
    val cancelJudgeTask: (commandSender: CommandSender, message: String) -> Boolean,
    val cancelSuccessTask: (commandSender: CommandSender, message: String) -> Unit,
    val judge: (commandSender: CommandSender, message: String) -> Boolean,
    val judgeTrueTask: (commandSender: CommandSender, message: String) -> Unit,
    val judgeFalseTask: (commandSender: CommandSender, message: String) -> Unit
)
