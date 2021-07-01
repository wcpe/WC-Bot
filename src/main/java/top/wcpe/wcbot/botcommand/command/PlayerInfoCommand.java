package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.onebone.economyapi.EconomyAPI;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcpelib.common.utils.collector.ListUtil;
import top.wcpe.wcpelib.common.utils.string.StringUtil;
import top.wcpe.wcpelib.nukkit.chat.ChatAcceptParameterManager;
import top.wcpe.wcpelib.nukkit.otherpluginapi.economyapi.EconomyUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 玩家详细信息指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:46
 */
public class PlayerInfoCommand extends BotCommandBase {
    public PlayerInfoCommand() {
        super("playerInfo");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!msg.equals(getCheckString())) {
            return;
        }
        QQMemberData qqMemberData = WCBot.getDataManager().getQQMemberData(e.getSender().getId());
        if (qqMemberData.getBindPlayerName() == null || qqMemberData.getBindPlayerName().isEmpty()) {
            sendMessage(e, WCBot.getMessageManager().getMessage("youNotBindPlayer"));
            return;
        }
        Player playerExact = Server.getInstance().getPlayerExact(qqMemberData.getBindPlayerName());

        EconomyAPI economy = EconomyUtil.getEconomy();
        List<String> message = ListUtil.replaceString(getSetting().getStringList("message"), "player:" + qqMemberData.getBindPlayerName(), "money:" + (economy == null ? 0 : economy.myMoney(qqMemberData.getBindPlayerName())), "points:" + qqMemberData.getWcBotPoint(), "online:" + (playerExact == null ? "离线" : playerExact.isOnline() ? "在线" : "离线"), "offline_time:" + 0, "first_time:" + 0, "sign_timer:" + qqMemberData.getSignDayTimeStamp().size()
        );
        sendMessage(e, message.stream().collect(Collectors.joining(System.lineSeparator())));
    }

}
