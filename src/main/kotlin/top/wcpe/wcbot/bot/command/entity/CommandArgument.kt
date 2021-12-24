package top.wcpe.wcbot.bot.command.entity

/**
 * 由 WCPE 在 2021/11/24 19:28 创建
 * Created by WCPE on 2021/11/24 19:28
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
data class CommandArgument(
    val name: String,
    val describe: String = name,
    val ignoreArg: String? = null
)