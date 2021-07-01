package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcpelib.common.utils.string.StringUtil;

/**
 * 玩家白名单管理
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class WhiteListManagerCommand extends BotCommandBase {
    public WhiteListManagerCommand() {
        super("whiteListManager");
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
        if(!getCheckString().equals(msgSplit[0])){
            return;
        }
        if (!isMainQQ(e.getSender().getId())&&!WCBot.getDataManager().getQQMemberData(e.getSender().getId()).isAdmin()) return;

        if (getSetting().getString("subCommand.list").equals(msgSplit[1])) {
            StringBuilder sb = new StringBuilder("当前白名单列表");
            for (String key : Server.getInstance().getWhitelist().getKeys(false)) {
                sb.append(System.lineSeparator() + key);
            }
            sendMessage(e, sb.toString());
            return;
        }
        if (getSetting().getString("subCommand.add").equals(msgSplit[1])) {
            Server.getInstance().addWhitelist(msgSplit[2]);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }

        if (getSetting().getString("subCommand.del").equals(msgSplit[1])) {
            Server.getInstance().removeWhitelist(msgSplit[2]);
            sendMessage(e, getSetting().getString("tip"));
            return;
        }


    }

}
