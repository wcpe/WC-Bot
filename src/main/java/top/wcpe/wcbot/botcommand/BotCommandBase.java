package top.wcpe.wcbot.botcommand;

import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcbot.WCBot;

import java.util.HashMap;

/**
 * 机器人指令
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-23 11:17
 */
public abstract class BotCommandBase {


    public void registerCommand() {
        if (!isEnable()) {
            return;
        }
        botCommandBaseHashMap.put(getName(), this);
    }

    protected BotCommandBase(String name) {
        this.name = name;
    }

    @Getter
    private final String name;

    public String getCheckString() {
        return getSetting().getString("checkString");
    }

    public boolean isEnable() {
        return getSetting().getBoolean("enable");
    }

    public boolean isMainQQ(Long qq) {
        if (qq == null) {
            return false;
        }
        try {
            return qq.equals(Long.parseLong(WCBot.getInstance().getSetting().getString("mainQQ")));
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public ConfigSection getSetting() {
        return WCBot.getInstance().getSetting().getSection("command." + getName());
    }

    @Getter
    private static final HashMap<String, BotCommandBase> botCommandBaseHashMap = new HashMap<>();

    public void sendMessage(MessageEvent e, String msg) {
        if (e instanceof GroupMessageEvent) {
            GroupMessageEvent event = (GroupMessageEvent) e;
            event.getGroup().sendMessage(msg);
            return;
        }
        if (e instanceof FriendMessageEvent) {
            FriendMessageEvent event = (FriendMessageEvent) e;
            event.getFriend().sendMessage(msg);
            return;
        }

    }

    abstract public void runCommand(MessageEvent e);
}
