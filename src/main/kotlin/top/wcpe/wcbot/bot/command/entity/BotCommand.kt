package top.wcpe.wcbot.bot.command.entity

import top.wcpe.wcbot.bot.command.sender.CommandSender
import top.wcpe.wcbot.bot.command.type.UseRange

/**
 * 由 WCPE 在 2021/11/26 18:22 创建
 * Created by WCPE on 2021/11/26 18:22
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class BotCommand(var name: String, var describe: String) {

    val args: MutableList<CommandArgument> = mutableListOf()
    var hideNoPermissionHelp = false
    var permission: String? = null
    var noPermissionMessage = "你莫得 %permission% 权限!"
    var useRange = UseRange.ALL
    var executeComponent: ((sender: CommandSender, args: MutableList<String>) -> Unit)? = null

    fun args(vararg args: CommandArgument): BotCommand {
        for (arg in args) {
            this.args.add(arg)
        }
        return this
    }

    fun hideNoPermissionHelp(hideNoPermissionHelp: Boolean): BotCommand {
        this.hideNoPermissionHelp = hideNoPermissionHelp
        return this
    }

    fun permission(permission: String): BotCommand {
        this.permission = permission
        return this
    }

    fun noPermissionMessage(noPermissionMessage: String): BotCommand {
        this.noPermissionMessage = noPermissionMessage
        return this
    }

    fun useRange(useRange: UseRange): BotCommand {
        this.useRange = useRange
        return this
    }

    fun executeComponent(executeComponent: ((sender: CommandSender, args: MutableList<String>) -> Unit)): BotCommand {
        this.executeComponent = executeComponent
        return this
    }

}