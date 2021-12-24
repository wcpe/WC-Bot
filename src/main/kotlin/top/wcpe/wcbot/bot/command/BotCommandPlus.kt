package top.wcpe.wcbot.bot.command

import cn.nukkit.plugin.Plugin
import top.wcpe.wcbot.bot.command.entity.BotCommand
import top.wcpe.wcbot.bot.command.entity.CommandArgument
import top.wcpe.wcbot.bot.command.sender.CommandSender
import top.wcpe.wcbot.bot.command.sender.GroupSender
import top.wcpe.wcbot.bot.command.sender.UserSender
import top.wcpe.wcbot.bot.command.type.UseRange
import top.wcpe.wcpelib.common.utils.collector.ListUtil
import top.wcpe.wcpelib.common.utils.string.StringUtil
import java.util.stream.Collectors

/**
 * 由 WCPE 在 2021/11/24 20:10 创建
 * Created by WCPE on 2021/11/24 20:10
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class BotCommandPlus(
    val name: String,
    var plugin: Plugin,
    var aliases: MutableList<String> = mutableListOf()
) {

    constructor(mainCommand: BotCommand, plugin: Plugin, aliases: MutableList<String> = mutableListOf()) : this(
        mainCommand.name,
        plugin, aliases
    ) {
        this.mainCommand = mainCommand
    }

    private var mainCommand: BotCommand? = null
    private val subCommandMap = LinkedHashMap<String, BotCommand>()

    fun registerThis(): BotCommandPlus {
        BotCommandManager.registerCommandPlus(this)
        return this
    }

    fun registerSubCommand(subCommand: BotCommand): BotCommandPlus {
        subCommandMap[subCommand.name] = subCommand
        return this
    }

    fun registerSubCommands(vararg subCommands: BotCommand): BotCommandPlus {
        for (subCommand in subCommands) {
            subCommandMap[subCommand.name] = subCommand
        }
        return this
    }

    private fun getPermission(command: BotCommand): String {
        mainCommand?.run {
            return permission ?: "$name.use"
        }
        return command.permission ?: "$name.${command.name}.use"
    }

    private fun executeSender(command: BotCommand, sender: CommandSender): Boolean {
        when (command.useRange) {
            UseRange.PRIVATE -> {
                if (sender is UserSender) {
                    return true
                }
            }
            UseRange.GROUP -> {
                if (sender is GroupSender) {
                    return true
                }
            }
            UseRange.ALL -> {
                return true
            }
        }
        return false
    }

    private fun executePermission(command: BotCommand, sender: CommandSender): Boolean {
        val permission = getPermission(command)
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(StringUtil.replaceString(command.noPermissionMessage, "permission:$permission"))
            return false
        }
        return true
    }

    private fun requiredArgs(
        args: MutableList<String>,
        listCommandArgument: MutableList<CommandArgument>
    ): Int {
        for (i in listCommandArgument.indices) {
            if (listCommandArgument[i].ignoreArg != null) {
                continue
            }
            if (i >= args.size) {
                return i
            }
            if ("" == args[i] || " " == args[i]) {
                return i
            }
        }
        return -1
    }

    private fun ignoreArgReplace(
        args: MutableList<String>,
        listCommandArgument: MutableList<CommandArgument>
    ): MutableList<String> {

        for (i in listCommandArgument.indices) {
            val ignoreArg = listCommandArgument[i].ignoreArg ?: continue
            if (i >= args.size) {
                args.add(ignoreArg)
                continue
            }
            if ("" == args[i] || " " == args[i]) {
                args[i] = ignoreArg
            }
        }
        return args
    }

    fun execute(sender: CommandSender, args: MutableList<String>) {
        var argsArray = args
        if (!plugin.isEnabled) {
            sender.sendMessage("插件 " + plugin.name + " 已被卸载 无法使用命令!")
            return
        }
        mainCommand?.let {
            if (!executeSender(it, sender) || !executePermission(it, sender)) return

            val i = requiredArgs(argsArray, it.args)
            if (i != -1) {
                sender.sendMessage("指令执行错误 请填写必填参数 <" + it.args[i].name + "> !")
                return
            }
            it.executeComponent?.let { it1 -> it1(sender, ignoreArgReplace(argsArray, it.args)) }
            return
        }
        if (argsArray.size > 0) {
            subCommandMap[argsArray[0]]?.let {
                var subArgs: MutableList<String> =
                    if (argsArray.size == 1) mutableListOf() else argsArray.subList(1, argsArray.size)

                val i = requiredArgs(subArgs, it.args)
                if (i != -1) {
                    sender.sendMessage("指令执行错误 请填写必填参数 <" + it.args[i].name + "> !")
                    return
                }

                if (!executeSender(it, sender) || !executePermission(it, sender)) return

                it.executeComponent?.let { it1 -> it1(sender, ignoreArgReplace(subArgs, it.args)) }
                return
            }
        }
        if (argsArray.size == 0 || "help" == argsArray[0]) {
            var page = 1
            if (argsArray.size > 1) try {
                page = argsArray[1].toInt()
            } catch (e: NumberFormatException) {
            }
            val splitSubCommandList = ListUtil.splitList(subCommandMap.values.stream().collect(Collectors.toList()), 5)
            page = if (page > splitSubCommandList.size) splitSubCommandList.size else page

            var stringBuilder = StringBuilder()
            stringBuilder.append("===== $name 指令帮助 $page/${splitSubCommandList.size} 页 =====").append("\n")
            stringBuilder.append("/$name").append("\n")
            for (alias: String in aliases) {
                stringBuilder.append("/$alias").append("\n")
            }
            for (subCommand: BotCommand in splitSubCommandList[page - 1]) {
                stringBuilder.append(">${StringUtil.getRepeatString(" ", name.length)} ${subCommand.name} ${
                    subCommand.args.joinToString(" ") {
                        it.run {
                            if (ignoreArg == null)
                                "<${name}>"
                            else
                                "[${name}]"
                        }
                    }
                }").append("\n")

                stringBuilder.append(
                    ">${StringUtil.getRepeatString(" ", name.length)}" + " 描述: ${subCommand.describe}"
                ).append("\n")
                stringBuilder.append(
                    ">${
                        StringUtil.getRepeatString(
                            " ",
                            name.length
                        )
                    } 权限: ${getPermission(subCommand)}"
                ).append("\n")
            }
            stringBuilder.append("> []为选填参数 <>为必填参数").append("\n")
            sender.sendMessage(stringBuilder.toString())
            return
        }
        sender.sendMessage("指令不存在或参数错误! 请输入/$name help [页码] 进行查询")
        return
    }
}