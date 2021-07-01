package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcpelib.common.utils.math.RandomUtil;
import top.wcpe.wcpelib.common.utils.string.StringUtil;

import java.time.LocalDate;

/**
 * 玩家签到指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-24 19:30
 */
public class PlayerSignCommand extends BotCommandBase {
    public PlayerSignCommand() {
        super("playerSign");
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
        String s = LocalDate.now().toString();
        if (qqMemberData.getSignDayTimeStamp().containsKey(s)) {
            sendMessage(e, getSetting().getString("alreadySign"));
            return;
        }
        int getPoints = 0;
        String signReward = getSetting().getString("signReward");
        String[] split = signReward.split("~");
        if (split.length == 2) {
            getPoints = RandomUtil.nextRandom(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } else {
            try {
                getPoints = Integer.parseInt(signReward);
            } catch (NumberFormatException exception) {
                sendMessage(e, "请在配置文件中配置正确的签到积分");
                return;
            }
        }
        qqMemberData.getSignDayTimeStamp().put(s, System.currentTimeMillis());
        sendMessage(e, getSetting().getString("message").replace("%reward%", ""+getPoints));
    }

}
