package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.PlayerData;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcpelib.common.utils.string.StringUtil;
import top.wcpe.wcpelib.nukkit.chat.ChatAcceptParameterManager;

/**
 * 玩家绑定游戏账号指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:45
 */
public class PlayerBindCommand extends BotCommandBase {
    public PlayerBindCommand() {
        super("playerBind");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!msg.startsWith(getCheckString())) {
            return;
        }
        String[] msgSplit = msg.split(" ");
        if (msgSplit.length != 2) {
            return;
        }

        Player playerExact = Server.getInstance().getPlayerExact(msgSplit[1]);
        if (playerExact == null || !playerExact.isOnline()) {
            sendMessage(e, WCBot.getMessageManager().getMessage("playerNotExistsOrNoOnline", "player:" + msgSplit[1]));
            return;
        }
        sendMessage(e,getSetting().getString("qqBindTip"));
        ChatAcceptParameterManager.putChatAcceptParameterTask(playerExact, getSetting().getLong("confirmBindTime"), getSetting().getString("gamePlayerBindTip"), "取消绑定", "§a取消成功!", (message) ->
                        "确认绑定".equals(message)
                , (player, message) -> {
                    QQMemberData qqMemberData = WCBot.getDataManager().getQQMemberData(e.getSender().getId());
                    qqMemberData.setBindPlayerName(playerExact.getName());
                    WCBot.getDataManager().saveQQMemberData(qqMemberData);
                    PlayerData playerData = WCBot.getDataManager().getPlayerData(playerExact.getName());
                    playerData.setQqNumber(e.getSender().getId());
                    WCBot.getDataManager().savePlayerData(playerData);
                    if (getSetting().getBoolean("bindSuccessTip.enable"))
                        sendMessage(e, StringUtil.replaceString(getSetting().getString("bindSuccessTip.message")));
                }, (player, message) -> {

                });

    }

}
