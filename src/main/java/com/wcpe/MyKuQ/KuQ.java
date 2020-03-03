package com.wcpe.MyKuQ;

import cc.moecraft.icq.event.EventHandler;
import cc.moecraft.icq.event.IcqListener;
import cc.moecraft.icq.event.events.message.EventGroupMessage;
import cc.moecraft.icq.event.events.message.EventPrivateMessage;
import cc.moecraft.icq.event.events.notice.groupmember.decrease.EventNoticeGroupMemberDecrease;
import cc.moecraft.icq.sender.returndata.returnpojo.get.RGroup;
import cn.nukkit.Player;
import cn.nukkit.Server;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class KuQ extends IcqListener {
    public KuQ(Main a) {
        this.a = a;
    }
    private Main a;
    @EventHandler
    public void QuitGroup(EventNoticeGroupMemberDecrease e) {
        String s = a.getWhiteListPlayer().get(String.valueOf(e.getUserId()));
        if(s != null){
            a.getServer().removeWhitelist(s);
            a.getWhiteListPlayer().remove(String.valueOf(e.getUserId()));
            try {
                a.getServer().getPlayer(s).kick();
                a.getA().sendGroupMsg(e.getGroupId(),"发现该退群玩家绑定的游戏id" + s + "在线 踢出玩家成功~");
            } catch (NullPointerException ee) {
                return;
            }
        }
    }

    @EventHandler
    public void PrivateMessage(EventPrivateMessage e) {
        long user = e.getSender().getId();
        String message = e.getMessage();
        if (user == a.getMainAdminQQ()||a.getAdmin().contains(user)) {
            if (a.isRcon_Enable()) {
                if (message.contains("cmd:")) {
                    message = message.substring(4);
                    String result = null;
                    try {
                        result = a.getRcon().command(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.respond(result);
                }
                return;
            }
            if (user == a.getMainAdminQQ()) {
                int admin_add = message.indexOf("/Admin add ");
                if (admin_add != -1) {
                    String s = message.substring(admin_add + 11);
                    a.getAdmin().add(Long.valueOf(s));
                    e.respond("添加" + s + "为管理员成功!");
                    return;
                }
                int admin_del = message.indexOf("/Admin del ");
                if (admin_del != -1) {
                    String s = message.substring(admin_del + 11);
                    if (a.getAdmin().contains(s)) {
                        a.getAdmin().remove(Long.valueOf(s));
                        e.respond("将" + s + "从管理员列表删除成功!");
                        return;
                    } else {
                        e.respond(s + "不在管理员列表内!");
                        return;
                    }
                }
                int admin_list = message.indexOf("/Admin list");
                if (admin_list != -1) {
                    e.respond("管理员列表:\n" + a.getAdmin().toString());
                    return;
                }
                int group_add = message.indexOf("/Group add ");
                if (group_add != -1) {
                    String s = message.substring(group_add + 11, message.length());
                    for (RGroup b : a.getA().getGroupList().getData()) {
                        if (String.valueOf(b.getGroupId()).equals(s)) {
                            a.getQQGroup().add(Long.valueOf(s));
                            e.respond("添加" + s + "到服务器群列表成功!");
                            return;
                        }
                    }
                    e.respond("添加" + s + "到服务器群列表失败!\n该群不在QQ群聊列表中");
                    return;
                }
                int group_del = message.indexOf("/Group del ");
                if (group_del != -1) {
                    String s = message.substring(group_del + 11, message.length());
                    if (a.getQQGroup().contains(Long.valueOf(s))) {
                        a.getQQGroup().remove(Long.valueOf(s));
                        e.respond("将" + s + "从管理员群列表删除成功!");
                        return;
                    } else {
                        e.respond("删除" + s + "到服务器群列表失败!\n该群不在管理群聊列表中");
                        return;
                    }

                }
                int group_list = message.indexOf("/Group list");
                if (group_list != -1) {
                    e.respond("群列表:\n" + a.getQQGroup().toString());
                    return;
                }
            }
            int white_add = message.indexOf("/White add ");
            if (white_add != -1) {
                try {
                    String s = message.substring(white_add + 11);
                    String[] s1 = s.split(" ");

                    String s2 = a.getWhiteListPlayer().get(s1[0]);

                    a.getWhiteListPlayer().put(s1[0], s1[1]);
                    a.getServer().addWhitelist(s1[1]);
                    e.respond("将" + s + "加入白名单成功!");
                    if (a.getWhiteListPlayer().containsKey(s1[0])) {
                        a.getServer().removeWhitelist(s2);
                        a.getWhiteListPlayer().remove(s1[0]);
                        try {
                            a.getServer().getPlayer(s2).kick();
                            e.respond("发现玩家" + s2 + "在线 踢出玩家成功~");
                        } catch (NullPointerException ee) {
                            return;
                        }
                    }
                    return;
                } catch (Exception ee) {
                    e.respond("使用方法:/White add QQ号 玩家id");
                }
            }
            int white_del = message.indexOf("/White del ");
            if (white_del != -1) {
                try {
                    String s = message.substring(white_del + 11);
                    if (a.getWhiteListPlayer().containsKey(s)) {
                        String s1 = a.getWhiteListPlayer().get(s);
                        a.getServer().removeWhitelist(s1);
                        a.getWhiteListPlayer().remove(s);
                        e.respond("将" + s + "从白名单删除成功!");
                        try {
                            a.getServer().getPlayer(s1).kick();
                            e.respond("发现玩家" + s1 + "在线 踢出玩家成功~");
                        } catch (NullPointerException ee) {
                            return;
                        }
                        return;
                    } else {
                        e.respond(s + "不存在于白名单!");
                    }
                } catch (Exception ee) {
                    e.respond("使用方法:/White del QQ号");
                }
            }
            int white_list = message.indexOf("/White list");
            if (white_list != -1) {
                e.respond("白名单列表:\n" + a.getWhiteListPlayer().toString());
                return;
            }
        }
    }

    @EventHandler
    public void GroupMessage(EventGroupMessage e) {
        long group = e.getGroup().getId();
        if (a.getQQGroup().contains(group) || a.getMainAdminQQGroup() == group) {
            long user = e.getSender().getId();
            String message = e.getMessage();
            if (a.isRcon_Enable() && a.isPrivate()) {
                if (user == a.getMainAdminQQ() && message.contains("cmd:")) {
                    message = message.substring(4);
                    String result = null;
                    try {
                        result = a.getRcon().command(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.respond(result);
                    return;
                }
            }
            int i = message.indexOf(a.getQQGroupCheck());
            if (a.getQQGroupCheck().equals("")) {
                String b = a.isIDorName() ? user + "" : e.getSender().getInfo().getNickname();
                String s = a.getSendGameMessage().replaceAll("%player%", b).replaceAll("%chat%", message);
                Server.getInstance().broadcastMessage(s);
                if (a.isQQ_to_Game_SendSuccessTipEnable())
                    e.respond(a.getQQ_to_Game_SendSuccessTip());

            } else if (i != -1) {
                message = message.substring(i + a.getQQGroupCheck().length());
                String b = a.isIDorName() ? user + "" : e.getSender().getInfo().getNickname();
                String s = a.getSendGameMessage().replaceAll("%player%", b).replaceAll("%chat%", message);
                if (a.isQQ_to_Game_isRemoveColor()) {
                    if (s.contains("&"))
                        s = s.replaceAll("&", "");
                } else {
                    s = s.replaceAll("&", "§");
                }
                Server.getInstance().broadcastMessage(s);
                if (a.isQQ_to_Game_SendSuccessTipEnable())
                    e.respond(a.getQQ_to_Game_SendSuccessTip());

            }
            if (message.equals(a.getListCommands())) {
                StringBuilder s = new StringBuilder("");
                Map<UUID, Player> onlinePlayers = a.getServer().getOnlinePlayers();
                for (Map.Entry<UUID, Player> p : a.getServer().getOnlinePlayers().entrySet()) {
                    s.append(p.getValue().getName());
                    s.append(" ");
                }
                e.respond(a.getListMessage().replaceAll("%online_number%", "" + onlinePlayers.size()).replaceAll("%online_player%", s.toString()));
            }

            int i1 = message.indexOf(a.getWhiteList_Check());
            if (i1 != -1) {
                if (a.isWhiteList_Enable()) {
                    String last = a.getWhiteListPlayer().get(String.valueOf(user));
                    String substring = message.substring(i1 + a.getWhiteList_Check().length());
                    a.getWhiteListPlayer().put(String.valueOf(user), substring);
                    a.getServer().addWhitelist(substring);
                    e.respond(a.getWhiteList_Message().replaceAll("%player%",substring));
                    if (last != null) {
                        try {
                            a.getServer().getPlayer(last).kick();
                            e.respond("发现该QQ上次申请的玩家白名单" + last + "在线 踢出玩家成功~");
                        } catch (NullPointerException ee) {
                            return;
                        }
                    }
                    return;
                }
            }
        }
    }
}
