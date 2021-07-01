package top.wcpe.wcbot;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.ConfigSection;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import top.wcpe.wcpelib.common.utils.string.StringUtil;


/**
 * 基础功能
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 21:39
 */
public class WcBotBasicFunctions implements Listener, ListenerHost {
    @EventHandler
    public void playerJoinServer(PlayerJoinEvent e) {
        if (!WCBot.getInstance().getSetting().getBoolean("serverJoinPlayer.enable")) {
            return;
        }
        WCBot.getBotManager().sendMessageToEnableQQGroup(StringUtil.replaceString(WCBot.getInstance().getSetting().getString("serverJoinPlayer.message"), "player:" + e.getPlayer().getName()));
    }

    @EventHandler
    public void playerQuitServer(PlayerQuitEvent e) {
        if (!WCBot.getInstance().getSetting().getBoolean("serverQuitPlayer.enable")) {
            return;
        }
        WCBot.getBotManager().sendMessageToEnableQQGroup(StringUtil.replaceString(WCBot.getInstance().getSetting().getString("serverQuitPlayer.message"), "player:" + e.getPlayer().getName()));
    }

    @EventHandler
    public void serverToQQGroupChat(PlayerChatEvent e) {
        ConfigSection setting = WCBot.getInstance().getSetting();
        if (!setting.getBoolean("serverToQQGroup.enable")) {
            return;
        }
        String msg = e.getMessage();
        String checkString = setting.getString("serverToQQGroup.checkMessage");
        if (!checkString.isEmpty() && !msg.startsWith(checkString)) {
            return;
        }
        if (setting.getBoolean("serverToQQGroup.isRemoveColor")) {
            msg = msg.replace("§[0-9]", "");
        }
        if (setting.getBoolean("serverToQQGroup.sendSuccessTip.enable")) {
            e.getPlayer().sendMessage(setting.getString("serverToQQGroup.sendSuccessTip.message"));
        }
        WCBot.getBotManager().sendMessageToEnableQQGroup(StringUtil.replaceString(setting.getString("serverToQQGroup.sendQQGroupMessage"), "player:" + e.getPlayer().getName(), "chat:" + msg));
    }


    @net.mamoe.mirai.event.EventHandler
    public ListeningStatus privateMessage(FriendMessageEvent e) {
        groupToServer(e);
        customReply(e);
        return ListeningStatus.LISTENING;
    }

    @net.mamoe.mirai.event.EventHandler
    public ListeningStatus groupMessage(GroupMessageEvent e) {
        groupToServer(e);
        customReply(e);
        return ListeningStatus.LISTENING;
    }

    private void groupToServer(MessageEvent e) {
        ConfigSection setting = WCBot.getInstance().getSetting();
        if (!setting.getBoolean("qqGroupToServer.enable")) {
            return;
        }
        String msg = e.getMessage().contentToString();
        String checkString = setting.getString("qqGroupToServer.checkMessage");
        if (!checkString.isEmpty() && !msg.startsWith(checkString)) {
            return;
        }

        if (setting.getBoolean("qqGroupToServer.sendSuccessTip.enable")) {
            WCBot.getBotManager().sendMessageToEnableQQGroup(setting.getString("qqGroupToServer.sendSuccessTip.message"));
        }
        Server.getInstance().broadcastMessage(StringUtil.replaceString(setting.getString("qqGroupToServer.sendQQGroupMessage"), "qq:" + e.getSender().getId(), "qq_nick:" + e.getSender().getNick(), "chat:" + msg));
    }
    private void customReply(MessageEvent e){
        String reply = WCBot.getInstance().getSetting().getString("customReply."+e.getMessage().contentToString());
        if(reply==null||reply.isEmpty()){
            return;
        }
        if(e instanceof GroupMessageEvent){
            GroupMessageEvent event = (GroupMessageEvent) e;
            event.getGroup().sendMessage(reply);
            return;
        }
        e.getSender().sendMessage(reply);
    }
}
