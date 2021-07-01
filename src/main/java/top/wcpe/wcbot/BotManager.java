package top.wcpe.wcbot;

import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import top.wcpe.wcbot.botcommand.BotCommandBase;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器人管理类
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 15:29
 */
public class BotManager {
    public BotManager() {
        Thread thread = new Thread(() -> {
            ConfigSection setting = WCBot.getInstance().getSetting();
            String account = setting.getString("bot.default.account");
            if (account.isEmpty() || "填写 QQ 号码".equals(account)) {
                WCBot.getInstance().getLogger().alert("请在配置文件中填写 QQ 账号 再重启服务器!");
                return;
            }
            String password = setting.getString("bot.default.password");
            if (password.isEmpty() || "填写 QQ 密码".equals(password)) {
                WCBot.getInstance().getLogger().alert("请在配置文件中填写 QQ 账号 再重启服务器!");
                return;
            }
            Long qqNumber = null;
            try {
                qqNumber = Long.parseLong(account);
            } catch (NumberFormatException e) {
                WCBot.getInstance().getLogger().alert("请在配置文件中填写正确的 QQ 账号 再重启服务器!");
                return;
            }


            this.defaultBot = BotFactory.INSTANCE.newBot(qqNumber,
                    password, new BotConfiguration() {
                        {
                            setWorkingDir(WCBot.getInstance().getDataFolder());
                            fileBasedDeviceInfo("deviceInfo.json");
                        }
                    });
            this.defaultBot.login();
            if (defaultBot.isOnline()) {
                WCBot.getInstance().getLogger().alert("§a机器人已成功在线 !");
                if (setting.getBoolean("sendQQGroupTip.enable")) {
                    for (String message : setting.getStringList("sendQQGroupTip.sendMessage")) {
                        sendMessageToEnableQQGroup(message);
                    }
                }
            }
            this.defaultBot.getEventChannel().registerListenerHost(WCBot.getWcBotBasicFunctions());

            GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
                for (Map.Entry<String, BotCommandBase> stringBotCommandBaseEntry : BotCommandBase.getBotCommandBaseHashMap().entrySet()) {
                    stringBotCommandBaseEntry.getValue().runCommand(event);
                }
            });
            GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
                for (Map.Entry<String, BotCommandBase> stringBotCommandBaseEntry : BotCommandBase.getBotCommandBaseHashMap().entrySet()) {
                    stringBotCommandBaseEntry.getValue().runCommand(event);
                }
            });
        });
        thread.setName("WC-Bot Thread");
        thread.start();
    }


    @Getter
    private Bot defaultBot;


    private final HashMap<String, WCBotConsoleSender> wcBotConsoleSenderHashMap = new HashMap<>();

    public WCBotConsoleSender getWcBotConsoleSender(MessageEvent e) {
        String sendKey = "defaultKey";
        if (e instanceof GroupMessageEvent) {
            sendKey = "Group:" + ((GroupMessageEvent) e).getGroup().getId();
        }
        if (e instanceof FriendMessageEvent) {
            sendKey = "Friend:" + ((FriendMessageEvent) e).getFriend().getId();
        }
        WCBotConsoleSender wcBotConsoleSender = this.wcBotConsoleSenderHashMap.get(sendKey);
        if (wcBotConsoleSender == null) {
            wcBotConsoleSender = new WCBotConsoleSender();
            this.wcBotConsoleSenderHashMap.put(sendKey, wcBotConsoleSender);
        }
        wcBotConsoleSender.setMessageEvent(e);
        return wcBotConsoleSender;
    }


    /**
     * 消息
     *
     * @param message
     */
    public void sendMessageToEnableQQGroup(String message) {
        for (Long qqGroup : WCBot.getDataManager().getServerData().getEnableQQGroup()) {
            Group group = this.defaultBot.getGroup(qqGroup);
            if (group == null) {
                continue;
            }
            group.sendMessage(message);
        }
    }

    /**
     * 给 QQ 号发送消息
     *
     * @param qq
     * @param msg
     */
    public void botSendMessage(Long qq, String msg) {
        Friend friend = defaultBot.getFriend(qq);
        if (friend != null) {
            friend.sendMessage(msg);
        } else {
            for (Group group : defaultBot.getGroups()) {
                NormalMember normalMember = group.get(qq);
                if (normalMember != null) {
                    normalMember.sendMessage(msg);
                    break;
                }
            }
        }
    }
}
