package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.WCBotConsoleSender;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcpelib.common.utils.collector.ListUtil;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 远程执行指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-23 11:44
 */
public class SudoCommand extends BotCommandBase {
    public SudoCommand() {
        super("sudo");
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
        if (!isMainQQ(e.getSender().getId())&&!WCBot.getDataManager().getQQMemberData(e.getSender().getId()).isAdmin()) return;

        WCBotConsoleSender wcBotConsoleSender = WCBot.getBotManager().getWcBotConsoleSender(e);
        Server.getInstance().getScheduler().scheduleTask(() -> {
            Server.getInstance().dispatchCommand(wcBotConsoleSender, msgSplit[1]);
        });


    }

}
