package top.wcpe.wcbot.botcommand.command;

import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcpelib.common.utils.math.RandomUtil;

import java.time.LocalDate;

/**
 * 玩家在线时间
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class PlayerGameTimeCommand extends BotCommandBase {
    public PlayerGameTimeCommand() {
        super("playerGameTime");
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

    }

}
