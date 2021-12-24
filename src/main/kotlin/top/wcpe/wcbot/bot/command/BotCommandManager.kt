package top.wcpe.wcbot.bot.command

import top.wcpe.wcbot.bot.command.sender.CommandSender


/**
 * 此类由 WCPE 在 2021/11/24 12:38 创建
 * Created by WCPE on 2021/11/24 12:38
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
object BotCommandManager {

    private val commandMap: MutableMap<String, BotCommandPlus> = mutableMapOf()

    fun registerCommandPlus(botCommandPlus: BotCommandPlus) {
        commandMap[botCommandPlus.name.lowercase()] = botCommandPlus
        commandMap[botCommandPlus.plugin.name.lowercase() + ":" + botCommandPlus.name] = botCommandPlus
        for (s in botCommandPlus.aliases) {
            commandMap[s] = botCommandPlus
            commandMap[botCommandPlus.plugin.name.lowercase() + ":" + s] = botCommandPlus
        }
        commandMap[botCommandPlus.name] = botCommandPlus
    }

    fun dispatch(sender: CommandSender, cmdLine: String): Boolean {
        val parsed = parseArguments(cmdLine)
        if (parsed.size == 0) {
            return false
        }
        val target = commandMap[parsed.removeAt(0).substring(1).lowercase()] ?: return false
        try {
            target.execute(sender, parsed)
        } catch (e: Exception) {
            sender.sendMessage(e.message.toString())
            e.printStackTrace()
        }
        return true
    }

    private fun parseArguments(cmdLine: String): ArrayList<String> {
        val sb = StringBuilder(cmdLine)
        val args = ArrayList<String>()
        var notQuoted = true
        var start = 0
        var i = 0
        while (i < sb.length) {
            if (sb[i] == '\\') {
                sb.deleteCharAt(i)
                i++
                continue
            }
            if (sb[i] == ' ' && notQuoted) {
                val arg = sb.substring(start, i)
                if (arg.isNotEmpty()) {
                    args.add(arg)
                }
                start = i + 1
            } else if (sb[i] == '"') {
                sb.deleteCharAt(i)
                --i
                notQuoted = !notQuoted
            }
            i++
        }
        val arg = sb.substring(start)
        if (arg.isNotEmpty()) {
            args.add(arg)
        }
        return args
    }
}
