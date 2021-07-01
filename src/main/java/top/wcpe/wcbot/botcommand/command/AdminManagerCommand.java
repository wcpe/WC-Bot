package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;

/**
 * 机器人管理员管理
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class AdminManagerCommand extends BotCommandBase {
    public AdminManagerCommand() {
        super("adnminManager");
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
            StringBuilder sb = new StringBuilder("当前管理员列表");
            for (QQMemberData listQQMemberDatum : WCBot.getDataManager().listQQMemberData()) {
                if (listQQMemberDatum.isAdmin())
                    sb.append(System.lineSeparator() + " -> " + listQQMemberDatum.getQq() + " -> " + ((listQQMemberDatum.getBindPlayerName() == null || listQQMemberDatum.getBindPlayerName().isEmpty()) ? "未绑定玩家账号" : listQQMemberDatum.getBindPlayerName()));
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
        if (getSetting().getString("subCommand.add").equals(msgSplit[1])) {
            qqMemberData.setAdmin(true);
            WCBot.getDataManager().saveQQMemberData(qqMemberData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }

        if (getSetting().getString("subCommand.del").equals(msgSplit[1])) {
            qqMemberData.setAdmin(false);
            WCBot.getDataManager().saveQQMemberData(qqMemberData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }


    }

}
