package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcpelib.common.utils.collector.ListUtil;
import top.wcpe.wcpelib.common.utils.string.StringUtil;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 玩家申请白名单指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class WhiteListCommand extends BotCommandBase {
    public WhiteListCommand() {
        super("whiteList");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!getCheckString().equals(msg)) {
            return;
        }
        QQMemberData qqMemberData = WCBot.getDataManager().getQQMemberData(e.getSender().getId());
        if (qqMemberData.getQq() == null || qqMemberData.getQq() == 0L) {
            sendMessage(e, WCBot.getMessageManager().getMessage("youNotBindPlayer"));
            return;
        }
        if(Server.getInstance().getWhitelist().getKeys(false).contains(qqMemberData.getBindPlayerName())){
            sendMessage(e, StringUtil.replaceString(getSetting().getString("existsWhiteList")));
            return;
        }
        if (getSetting().getBoolean("applyWhiteSuccessTip.enable")) {
            sendMessage(e, StringUtil.replaceString(getSetting().getString("applyWhiteSuccessTip.message")));
        }
        Server.getInstance().addWhitelist(qqMemberData.getBindPlayerName());
    }

}
