package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.PlayerData;
import top.wcpe.wcbot.entity.QQMemberData;

/**
 * 玩家绑定管理
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class BindManagerCommand extends BotCommandBase {
    public BindManagerCommand() {
        super("bindManager");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!msg.startsWith(getCheckString())) {
            return;
        }
        String[] msgSplit = msg.split(" ");
        if (msgSplit.length < 2 || msgSplit.length > 4) {
            return;
        }
        if (!getCheckString().equals(msgSplit[0])) {
            return;
        }
        if (!isMainQQ(e.getSender().getId())&&!WCBot.getDataManager().getQQMemberData(e.getSender().getId()).isAdmin()) return;

        if (getSetting().getString("subCommand.list").equals(msgSplit[1])) {
            StringBuilder sb = new StringBuilder("当前玩家绑定列表");
            for (PlayerData listPlayerData : WCBot.getDataManager().listPlayerData()) {
                sb.append(System.lineSeparator() + listPlayerData.getName() + " -> " + ((listPlayerData.getQqNumber() == null || listPlayerData.getQqNumber() == 0) ? "未绑定QQ账号" : listPlayerData.getQqNumber()));
            }
            sendMessage(e, sb.toString());
            return;
        }
        if (msgSplit.length != 3) return;
        Long qq = null;
        try {
            qq = Long.parseLong(msgSplit[2]);
        } catch (NumberFormatException exception) {
            sendMessage(e, "请输入正确的QQ账号!");
            return;
        }
        QQMemberData qqMemberData = WCBot.getDataManager().getQQMemberData(qq);

        if (getSetting().getString("subCommand.del").equals(msgSplit[1])) {
            qqMemberData.setBindPlayerName("");
            WCBot.getDataManager().saveQQMemberData(qqMemberData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }
        if (msgSplit.length != 4) return;
        Player playerExact = Server.getInstance().getPlayerExact(msgSplit[3]);
        if (playerExact == null) {
            sendMessage(e, "请输入有效的玩家名称!");
            return;
        }

        if (getSetting().getString("subCommand.add").equals(msgSplit[1])) {
            qqMemberData.setBindPlayerName(msgSplit[3]);
            qqMemberData.setAdmin(true);
            WCBot.getDataManager().saveQQMemberData(qqMemberData);
            PlayerData playerData = WCBot.getDataManager().getPlayerData(msgSplit[3]);
            playerData.setQqNumber(qq);
            WCBot.getDataManager().savePlayerData(playerData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }


    }

}
