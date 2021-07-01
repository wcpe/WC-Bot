package top.wcpe.wcbot;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 本插件控制台对象
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-23 11:10
 */
public class WCBotConsoleSender implements CommandSender {
    @Getter
    @Setter
    private MessageEvent messageEvent;


    @Override
    public void sendMessage(String s) {
        if (messageEvent == null||s.isEmpty()) return;
        if (messageEvent instanceof GroupMessageEvent) {
            ((GroupMessageEvent) messageEvent).getGroup().sendMessage(s);
        }
        if (messageEvent instanceof FriendMessageEvent) {
            ((FriendMessageEvent) messageEvent).getFriend().sendMessage(s);
        }
    }

    @Override
    public void sendMessage(TextContainer textContainer) {
        sendMessage(this.getServer().getLanguage().translate(textContainer));
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    @Override
    public String getName() {
        return "WC-Bot Console";
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isPermissionSet(String s) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return true;
    }

    @Override
    public boolean hasPermission(String s) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, Boolean aBoolean) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean b) {

    }
}
