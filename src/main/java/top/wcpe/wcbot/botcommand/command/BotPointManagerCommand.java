package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.PlayerData;
import top.wcpe.wcbot.entity.QQMemberData;

/**
 * 机器人积分管理
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class BotPointManagerCommand extends BotCommandBase {
    public BotPointManagerCommand() {
        super("botPointManager");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!msg.startsWith(getCheckString())) {
            return;
        }
        String[] msgSplit = msg.split(" ");
        if (msgSplit.length < 3 || msgSplit.length > 4) {
            return;
        }
        if (!getCheckString().equals(msgSplit[0])) {
            return;
        }
        if (!isMainQQ(e.getSender().getId())&&!WCBot.getDataManager().getQQMemberData(e.getSender().getId()).isAdmin()) return;
        Long qq = null;
        try {
            qq = Long.parseLong(msgSplit[2]);
        } catch (NumberFormatException exception) {
            sendMessage(e, "请输入正确的QQ账号!");
            return;
        }
        QQMemberData qqMemberData = WCBot.getDataManager().getQQMemberData(qq);

        if (getSetting().getString("subCommand.get").equals(msgSplit[1])) {
            sendMessage(e, "QQ: " + qq + " -> 当前积分: " + qqMemberData.getWcBotPoint());
            return;
        }

        if (msgSplit.length != 4) return;
        Long amount = null;
        try {
            amount = Long.parseLong(msgSplit[3]);
        } catch (NumberFormatException exception) {
            sendMessage(e, "请输入正确的积分数量!");
            return;
        }

        if (getSetting().getString("subCommand.add").equals(msgSplit[1])) {
            qqMemberData.setWcBotPoint(qqMemberData.getWcBotPoint() + amount);
            WCBot.getDataManager().saveQQMemberData(qqMemberData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }
        if (getSetting().getString("subCommand.del").equals(msgSplit[1])) {
            Long wcBotPoint = qqMemberData.getWcBotPoint();
            if (wcBotPoint < amount) {
                sendMessage(e,"该玩家余额不足 "+amount);
                return;
            }
            qqMemberData.setWcBotPoint(qqMemberData.getWcBotPoint() - amount);
            WCBot.getDataManager().saveQQMemberData(qqMemberData);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }


    }

}
