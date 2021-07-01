package top.wcpe.wcbot.botcommand.command;

import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcbot.entity.ServerData;

import java.util.List;

/**
 * 开启机器人群列表管理
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class EnableGroupManagerCommand extends BotCommandBase {
    public EnableGroupManagerCommand() {
        super("enableManager");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!msg.startsWith(getCheckString())) {
            return;
        }
        String[] msgSplit = msg.split(" ");
        if (msgSplit.length != 3 && msgSplit.length != 2) {
            return;
        }
        if (!getCheckString().equals(msgSplit[0])) {
            return;
        }
        if (!isMainQQ(e.getSender().getId())) return;
        if (getSetting().getString("subCommand.list").equals(msgSplit[1])) {
            StringBuilder sb = new StringBuilder("当前开启群列表");
            for (Long enableQQGroup : WCBot.getDataManager().getServerData().getEnableQQGroup()) {
                sb.append(System.lineSeparator() + " -> " + enableQQGroup);
            }
            sendMessage(e, sb.toString());
            return;
        }
        if (msgSplit.length != 3) return;
        Long qqGroupID = null;
        try {
            qqGroupID = Long.parseLong(msgSplit[2]);
        } catch (NumberFormatException exception) {
            sendMessage(e, "请输入正确的QQ群号!");
            return;
        }
        if (WCBot.getBotManager().getDefaultBot().getGroup(qqGroupID) == null) {
            sendMessage(e, "机器人未加入该群聊!");
            return;
        }
        ServerData serverData = WCBot.getDataManager().getServerData();

        if (getSetting().getString("subCommand.add").equals(msgSplit[1])) {
            if (serverData.getEnableQQGroup().contains(qqGroupID)) {
                sendMessage(e, "该群聊已加入开启列表!");
                return;
            }
            serverData.getEnableQQGroup().add(qqGroupID);
            WCBot.getDataManager().saveServerData(serverData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }

        if (getSetting().getString("subCommand.del").equals(msgSplit[1])) {
            if (!serverData.getEnableQQGroup().contains(qqGroupID)) {
                sendMessage(e, "该群聊未已加入开启列表!");
                return;
            }
            serverData.getEnableQQGroup().remove(qqGroupID);
            WCBot.getDataManager().saveServerData(serverData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }


    }

}
