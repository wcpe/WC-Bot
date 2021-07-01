package top.wcpe.wcbot.botcommand.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.botcommand.BotCommandBase;
import top.wcpe.wcpelib.common.utils.collector.ListUtil;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 玩家在线列表指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-23 11:44
 */
public class ListCommand extends BotCommandBase {
    public ListCommand() {
        super("list");
    }

    @Override
    public void runCommand(MessageEvent e) {
        String msg = e.getMessage().contentToString();
        if (!getCheckString().equals(msg)) {
            return;
        }
        Map<UUID, Player> onlinePlayers = Server.getInstance().getOnlinePlayers();
        sendMessage(e, ListUtil.replaceString(getSetting().getStringList("format"), "online_number:" + onlinePlayers.size(), "online_player:" + onlinePlayers.entrySet().stream().map(Map.Entry::getValue).map(Player::getName).collect(Collectors.joining(","))).stream().collect(Collectors.joining("\n")));
    }

}
