package top.wcpe.wcbot.bot

import cn.nukkit.Server
import cn.nukkit.utils.ConfigSection
import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.WCBotApi
import top.wcpe.wcbot.bot.command.BotCommandPlus
import top.wcpe.wcbot.bot.command.entity.BotCommand
import top.wcpe.wcbot.bot.command.entity.CommandArgument
import top.wcpe.wcbot.bot.command.sender.CommandSender
import top.wcpe.wcbot.bot.command.type.UseRange
import top.wcpe.wcpelib.common.utils.collector.ListUtil
import top.wcpe.wcpelib.common.utils.datatime.TimeUtil
import top.wcpe.wcpelib.common.utils.string.StringUtil
import top.wcpe.wcpelib.nukkit.chat.ChatAcceptParameterManager
import top.wcpe.wcpelib.nukkit.otherpluginapi.economyapi.EconomyUtil
import java.util.*


/**
 * 由 WCPE 在 2021/12/14 10:06 创建
 *
 * Created by WCPE on 2021/12/14 10:06
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class BotCommands {
    companion object {
        const val COMMAND_LIST = "list"
        const val COMMAND_SUDO = "sudo"
        const val COMMAND_APPLY_WHITELIST = "apply-whitelist"
        const val COMMAND_BIND_GAME_PLAYER = "bind-game-player"
        const val COMMAND_WHITE_LIST_MANAGER = "white-list-manager"
    }

    private val logger = WCBot.instance.logger


    init {
        reload()
    }

    fun reload() {
        createBotMainCommand(COMMAND_LIST) { sender, _, section ->
            Server.getInstance().onlinePlayers
            val onlinePlayers = Server.getInstance().onlinePlayers
            sender.sendMessage(
                ListUtil.replaceString(
                    section.getStringList("format"),
                    "online_number:" + onlinePlayers.size,
                    "online_player:" + onlinePlayers.entries.map { it.value.name }.joinToString(separator = ", ")
                ).joinToString(separator = System.lineSeparator())
            )
            logger.info("${sender.getQQMemberData().qq} 执行了 list 指令")
        }
        createBotMainCommand(
            COMMAND_SUDO,
            CommandArgument("指令"),
            CommandArgument("参数...", ignoreArg = "")
        ) { sender, args, _ ->
            val sb: StringBuilder = StringBuilder(args[0])
            for (i in 1 until args.size) {
                sb.append(" ").append(args[i])
            }
            Server.getInstance().scheduler.scheduleTask(WCBot.instance) {
                Server.getInstance().dispatchCommand(WCBot.botManager.getNukkitConsoleSender(sender), sb.toString())
            }
            logger.info("${sender.getQQMemberData().qq} 执行了 sudo 指令")
        }

        createBotMainCommand(
            COMMAND_APPLY_WHITELIST
        ) { sender, _, _ ->
            if (Server.getInstance().hasWhitelist()) {
                sender.sendMessage(WCBot.messageManager.getMessage("server-not-enable-white-list"))
                return@createBotMainCommand
            }
            sender.getQQMemberData().run {
                if (bindGamePlayerName.isNotEmpty()) {
                    if (Server.getInstance().isWhitelisted(bindGamePlayerName)) {
                        sender.sendMessage(WCBot.messageManager.getMessage("you-is-have-white-list"))
                    } else {
                        Server.getInstance().addWhitelist(bindGamePlayerName)
                        sender.sendMessage(WCBot.messageManager.getMessage("success-add-white-list"))
                        logger.info("${sender.getQQMemberData().qq} 执行了 apply-whitelist 指令")
                    }
                    return@createBotMainCommand
                }
            }
            sender.sendMessage(WCBot.messageManager.getMessage("you-not-bind-player"))
        }

        createBotMainCommand(COMMAND_BIND_GAME_PLAYER, CommandArgument("游戏玩家名称")
        ) { sender, args, section ->
            val qqMemberData = sender.getQQMemberData()
            if (qqMemberData.bindGamePlayerName.isNotEmpty()) {
                sender.sendMessage(WCBot.messageManager.getMessage("you-exist-bind-player"))
                return@createBotMainCommand
            }

            val playerExact = Server.getInstance().getPlayerExact(args[0])
            if (playerExact == null || !playerExact.isOnline) {
                sender.sendMessage(WCBot.messageManager.getMessage("you-must-online"))
                return@createBotMainCommand
            }
            playerExact.sendMessage(
                StringUtil.replaceString(
                    WCBot.messageManager.getMessage("game-player-tip-bind-player"),
                    "qq:${qqMemberData.qq}",
                    "time:${section.getInt("confirm-bind-time")}",
                    "agree_input:${section.getString("agree")}",
                    "cancel_input:${section.getString("cancel")}"
                )
            )
            sender.sendMessage(
                WCBot.messageManager
                    .getMessage(
                        "tip-bind-player",
                        "player_name:${args[0]}",
                        "time:${
                            section.getInt(
                                "confirm-bind-time"
                            )
                        }",
                        "agree_input:${section.getString("agree")}",
                        "cancel_input:${section.getString("cancel")}"
                    )
            )
            ChatAcceptParameterManager.putChatAcceptParameterTask(
                playerExact,
                section.getLong("confirm-bind-time"),
                { _, message -> message == section.getString("cancel") },
                { player, _ ->
                    player.sendMessage(
                        WCBot.messageManager
                            .getMessage("game-player-cancel-bind-player", "qq:${qqMemberData.qq}")
                    )
                    sender.sendMessage(
                        WCBot.messageManager.getMessage("cancel-bind-player", "player_name:${player.name}")
                    )
                },
                { _, message -> message == section.getString("agree") },
                { player, _ ->
                    qqMemberData.bindGamePlayerName = player.name
                    WCBotApi.saveQQMemberData(qqMemberData)
                    player.sendMessage(
                        WCBot.messageManager
                            .getMessage("game-player-success-bind-player", "qq:${qqMemberData.qq}")
                    )
                    sender.sendMessage(
                        WCBot.messageManager.getMessage("success-bind-player", "player_name:${player.name}")
                    )
                },
                { player, _ ->
                    player.sendMessage(
                        WCBot.messageManager.getMessage("game-player-input-error-bind-player")
                    )
                })
            logger.info("${sender.getQQMemberData().qq} 执行了 bind-game-player 指令")
        }

        createBotMainCommand(
            "player-info"
        ) { sender, _, section ->
            sender.getQQMemberData().run {
                if (bindGamePlayerName.isNotEmpty()) {

                    val offlinePlayer = Server.getInstance().getOfflinePlayer(bindGamePlayerName)

                    val messageList = mutableListOf<String>()
                    for (s in section.getStringList("format")) {
                        messageList.add(
                            StringUtil.replaceString(
                                s,
                                "player_name:$bindGamePlayerName",
                                "money:${
                                    EconomyUtil.getEconomy()?.let {
                                        it.myMoney(offlinePlayer)
                                    }
                                }",
                                "online:${if (offlinePlayer.isOnline) "在线" else "离线"}",
                                "offline_time:${TimeUtil.stampToTime(offlinePlayer.lastPlayed * 1000)}",
                                "first_time:${TimeUtil.stampToTime(offlinePlayer.firstPlayed * 1000)}"
                            )
                        )

                    }
                    sender.sendMessage(messageList.joinToString(separator = System.lineSeparator()))
                    logger.info("${sender.getQQMemberData().qq} 执行了 player-info 指令")
                    return@createBotMainCommand
                }
            }
            sender.sendMessage(WCBot.messageManager.getMessage("you-not-bind-player"))
        }

        createBotSubMainCommand(COMMAND_WHITE_LIST_MANAGER, createBotSubCommand(
            COMMAND_WHITE_LIST_MANAGER, "add",
            CommandArgument("玩家游戏名称")
        ) { sender, args, _ ->
            Server.getInstance().addWhitelist(args[0])
            sender.sendMessage("添加 ${args[0]} 成功")
            logger.info("${sender.getQQMemberData().qq} 执行了 white-list-manager add 指令")
        }, createBotSubCommand(
            COMMAND_WHITE_LIST_MANAGER, "del",
            CommandArgument("玩家游戏名称")
        ) { sender, args, _ ->
            Server.getInstance().removeWhitelist(args[0])
            sender.sendMessage("删除 ${args[0]} 成功")
            logger.info("${sender.getQQMemberData().qq} 执行了 white-list-manager del 指令")
        }, createBotSubCommand(
            COMMAND_WHITE_LIST_MANAGER, "list"
        ) { sender, args, section ->
            val keys = Server.getInstance().whitelist.getKeys(false)
            sender.sendMessage(
                ListUtil.replaceString(
                    section.getStringList("format"),
                    "white_number:" + keys.size,
                    "white_player:" + keys.joinToString(separator = ", ")
                ).joinToString(separator = System.lineSeparator())
            )

            sender.sendMessage("删除 ${args[0]} 成功")
            logger.info("${sender.getQQMemberData().qq} 执行了 white-list-manager list 指令")
        })

//        createBotSubMainCommand(COMMAND_WHITE_LIST_MANAGER, createBotSubCommand(
//            COMMAND_WHITE_LIST_MANAGER, "add",
//            CommandArgument("")
//        ) { sender, args ->
//
//        }, createBotSubCommand(
//            COMMAND_WHITE_LIST_MANAGER, "del",
//            CommandArgument("")
//        ) { sender, args ->
//
//        }, createBotSubCommand(
//            COMMAND_WHITE_LIST_MANAGER, "list",
//            CommandArgument("")
//        ) { sender, args ->
//
//        })
    }

    private fun createBotMainCommand(
        mainCommandKey: String, vararg commandArgument: CommandArgument,
        executeComponent: ((sender: CommandSender, args: MutableList<String>, section: ConfigSection) -> Unit)
    ) {
        BotCommandPlus(
            BotCommand(
                WCBot.instance.commandsConfig.getString("$mainCommandKey.main-command"),
                WCBot.instance.commandsConfig.getString("$mainCommandKey.describe")
            ).executeComponent { sender, args ->
                if (!WCBot.instance.commandsConfig.getBoolean("$mainCommandKey.enable")) {
                    sender.sendMessage(
                        WCBot.instance.commandsConfig.getString("$mainCommandKey.no-enable-message")
                    )
                    return@executeComponent
                }
                executeComponent(
                    sender, args, WCBot.instance.commandsConfig
                        .getSection(mainCommandKey)
                )
            }.args(*commandArgument)
                .useRange(
                    UseRange.valueOf(
                        WCBot.instance.commandsConfig.getString("$mainCommandKey.use-range")
                    )
                ),
            WCBot.instance, WCBot.instance.commandsConfig.getStringList("$mainCommandKey.aliases")
        ).registerThis()
    }

    private fun createBotSubMainCommand(
        mainCommandKey: String, vararg subCommands: BotCommand
    ) {
        BotCommandPlus(
            WCBot.instance.commandsConfig.getString("$mainCommandKey.main-command"),
            WCBot.instance,
            WCBot.instance.commandsConfig.getStringList("$mainCommandKey.aliases")
        ).registerSubCommands(*subCommands).registerThis()
    }

    private fun createBotSubCommand(
        mainCommandKey: String, subCommandKey: String, vararg commandArgument: CommandArgument,
        executeComponent: ((sender: CommandSender, args: MutableList<String>, section: ConfigSection) -> Unit)
    ): BotCommand {
        return BotCommand(
            subCommandKey,
            WCBot.instance.commandsConfig.getString("$mainCommandKey.sub-commands.$subCommandKey.describe")
        ).executeComponent { sender, args ->
            if (!WCBot.instance.commandsConfig
                    .getBoolean("$mainCommandKey.sub-commands.$subCommandKey.enable")
            ) {
                sender.sendMessage(
                    WCBot.instance.commandsConfig
                        .getString("$mainCommandKey.sub-commands.$subCommandKey.no-enable-message")
                )
                return@executeComponent
            }
            executeComponent(
                sender, args, WCBot.instance.commandsConfig
                    .getSection("$mainCommandKey.sub-commands.$subCommandKey")
            )
        }.args(*commandArgument)
            .useRange(
                UseRange.valueOf(
                    WCBot.instance.commandsConfig
                        .getString("$mainCommandKey.sub-commands.$subCommandKey.use-range")
                )
            )
    }

}