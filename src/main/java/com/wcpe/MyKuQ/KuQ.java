package com.wcpe.MyKuQ;

import cc.moecraft.icq.event.EventHandler;
import cc.moecraft.icq.event.IcqListener;
import cc.moecraft.icq.event.events.message.EventGroupMessage;
import cc.moecraft.icq.event.events.message.EventPrivateMessage;
import cc.moecraft.icq.event.events.notice.groupmember.decrease.EventNoticeGroupMemberDecrease;
import cc.moecraft.icq.sender.returndata.returnpojo.get.RGroup;
import cn.nukkit.Player;
import cn.nukkit.Server;
import com.wcpe.MyKuQ.Obj.KuPlayer;
import com.wcpe.MyKuQ.Utils.Confirm;
import com.wcpe.MyKuQ.Utils.WxysUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class KuQ extends IcqListener {
    public KuQ(Main a) {
        this.a = a;
    }

    private Main a;

    @EventHandler
    public void QuitGroup(EventNoticeGroupMemberDecrease e) {
        long group = e.getGroupId();
        if (isGroupList(group)) {
            String s = a.getWhiteListPlayer().get(String.valueOf(e.getUserId()));
            if (s != null) {
                a.getServer().removeWhitelist(s);
                a.getWhiteListPlayer().remove(String.valueOf(e.getUserId()));
                try {
                    a.getServer().getPlayer(s).kick();
                    a.getA().sendGroupMsg(e.getGroupId(), a.getMessage_PlayerQuitGroup().replaceAll("%player%", s));
                } catch (NullPointerException ee) {
                    return;
                }
            }
        }
    }

    @EventHandler
    public void PrivateMessage(EventPrivateMessage e) {
        long user = e.getSender().getId();
        String message = e.getMessage();
        if (user == a.getMainAdminQQ() || a.getAdmin().contains(user)) {
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
            }
            if (user == a.getMainAdminQQ()) {
                String Admin_add = a.getAdmins_Admin() + a.getAdmins_Admin_add();
                int admin_add = message.indexOf(Admin_add);
                if (admin_add != -1) {
                    String s = message.substring(admin_add + Admin_add.length());
                    a.getAdmin().add(Long.valueOf(s));
                    e.respond("添加" + s + "为管理员成功!");
                    return;
                }
                String Admin_del = a.getAdmins_Admin() + a.getAdmins_Admin_del();
                int admin_del = message.indexOf(Admin_del);
                if (admin_del != -1) {
                    String s = message.substring(admin_del + Admin_del.length());
                    if (a.getAdmin().contains(s)) {
                        a.getAdmin().remove(Long.valueOf(s));
                        e.respond("将" + s + "从管理员列表删除成功!");
                        return;
                    } else {
                        e.respond(s + "不在管理员列表内!");
                        return;
                    }
                }
                String Admin_list = a.getAdmins_Admin() + a.getAdmins_Admin_list();
                int admin_list = message.indexOf(Admin_list);
                if (admin_list != -1) {
                    e.respond("管理员列表:\n" + a.getAdmin().toString());
                    return;
                }
                String Group_add = a.getAdmins_Group() + a.getAdmins_Group_add();
                int group_add = message.indexOf(Group_add);
                if (group_add != -1) {
                    String s = message.substring(group_add + Group_add.length(), message.length());
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
                String Group_del = a.getAdmins_Group() + a.getAdmins_Group_del();
                int group_del = message.indexOf(Group_del);
                if (group_del != -1) {
                    String s = message.substring(group_del + Group_del.length(), message.length());
                    if (a.getQQGroup().contains(Long.valueOf(s))) {
                        a.getQQGroup().remove(Long.valueOf(s));
                        e.respond("将" + s + "从管理员群列表删除成功!");
                        return;
                    } else {
                        e.respond("删除" + s + "到服务器群列表失败!\n该群不在管理群聊列表中");
                        return;
                    }

                }
                String Group_list = a.getAdmins_Group() + a.getAdmins_Group_list();
                int group_list = message.indexOf(Group_list);
                if (group_list != -1) {
                    e.respond("群列表:\n" + a.getQQGroup().toString());
                    return;
                }
            }
            String White_add = a.getAdmins_White() + a.getAdmins_White_add();
            int white_add = message.indexOf(White_add);
            if (white_add != -1) {
                try {
                    String s = message.substring(white_add + White_add.length());
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
            String White_del = a.getAdmins_White() + a.getAdmins_White_del();
            int white_del = message.indexOf(White_del);
            if (white_del != -1) {
                try {
                    String s = message.substring(white_del + White_del.length());
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
            String White_list = a.getAdmins_White() + a.getAdmins_White_list();
            int white_list = message.indexOf(White_list);
            if (white_list != -1) {
                e.respond("白名单列表:\n" + a.getWhiteListPlayer().toString());
                return;
            }
            String Bind_add = a.getAdmins_Bind() + a.getAdmins_Bind_add();
            int bind_add = message.indexOf(Bind_add);
            if (bind_add != -1) {
                try {
                    String s = message.substring(bind_add + Bind_add.length());
                    String[] s1 = s.split(" ");

                    a.getKuPlayer().put(s1[0], new KuPlayer(a.getServer().getOfflinePlayer(s1[1]), Long.valueOf(s1[0])));
                    e.respond("添加玩家:" + s1[1] + " QQ:" + s1[0] + "成功");
                    return;
                } catch (Exception ee) {
                    e.respond("使用方法:/Bind add QQ号 玩家id");
                }
            }
            String Bind_del = a.getAdmins_Bind() + a.getAdmins_Bind_del();
            int bind_del = message.indexOf(Bind_del);
            if (bind_del != -1) {
                try {
                    String s = message.substring(bind_del + Bind_del.length());
                    if (a.getKuPlayer().containsKey(s)) {
                        a.getKuPlayer().remove(s);
                        e.respond("将" + s + "从白名单删除成功!");
                        return;
                    } else {
                        e.respond(s + "玩家未绑定!");
                    }
                } catch (Exception ee) {
                    e.respond("使用方法:/Bind del QQ号");
                }
            }
            String Bind_list = a.getAdmins_Bind() + a.getAdmins_Bind_list();
            int bind_list = message.indexOf(Bind_list);
            if (bind_list != -1) {
                StringBuilder sb = new StringBuilder();
                a.getKuPlayer().entrySet().forEach((s) -> {
                    sb.append("QQ:"+s.getKey() +" 绑定:"+s.getValue().getOfflinePlayer().getName()+"\n");
                });
                sb.setCharAt(sb.length() - 1, '\0');
                e.respond("玩家绑定列表:\n" + sb.toString());
                return;
            }

        }
    }

    /***
     * @param group qq群号
     * @return 返回qq群是否为主副群
     */
    private boolean isGroupList(long group) {
        return (a.getQQGroup().contains(group) || a.getMainAdminQQGroup() == group);
    }

    @EventHandler
    public void AdminGroupMessage(EventGroupMessage e) {
        long group = e.getGroup().getId();
        if (isGroupList(group)) {
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
        }

    }

    @EventHandler
    public void PlayerGroupMessage(EventGroupMessage e) {
        long group = e.getGroup().getId();
        if (isGroupList(group)) {
            long user = e.getSender().getId();
            String message = e.getMessage();

            int i = message.indexOf(a.getQQGroupCheck());
            if (a.getQQGroupCheck().equals("")) {
                String b = a.isIDorName() ? user + "" : e.getSender().getInfo().getNickname();
                String s = a.getSendGameMessage().replaceAll("%player%", b).replaceAll("%chat%", message);
                Server.getInstance().broadcastMessage(s);
                if (a.isQQ_to_Game_SendSuccessTipEnable()) {
                    e.respond(a.getQQ_to_Game_SendSuccessTip());
                }
                return;
            } else if (i != -1) {
                message = message.substring(i + a.getQQGroupCheck().length());
                String b = a.isIDorName() ? user + "" : e.getSender().getInfo().getNickname();
                String s = a.getSendGameMessage().replaceAll("%player%", b).replaceAll("%chat%", message);
                if (a.isQQ_to_Game_isRemoveColor()) {
                    if (s.contains("&")) {
                        s = s.replaceAll("&", "");
                    }
                } else {
                    s = s.replaceAll("&", "§");
                }
                Server.getInstance().broadcastMessage(s);
                if (a.isQQ_to_Game_SendSuccessTipEnable()) {
                    e.respond(a.getQQ_to_Game_SendSuccessTip());
                }
                return;
            }
            //list
            if (message.equals(a.getListCommands())) {
                StringBuilder s = new StringBuilder("");
                Map<UUID, Player> onlinePlayers = a.getServer().getOnlinePlayers();
                for (Map.Entry<UUID, Player> p : a.getServer().getOnlinePlayers().entrySet()) {
                    s.append(p.getValue().getName());
                    s.append(" ");
                }
                e.respond(a.getListMessage().replaceAll("%online_number%", "" + onlinePlayers.size()).replaceAll("%online_player%", s.toString()));
                return;
            }
            //申请白名单
            int i1 = message.indexOf(a.getWhiteList_Check());
            if (i1 != -1) {
                if (a.isWhiteList_Enable()) {
                    String last = a.getWhiteListPlayer().get(String.valueOf(user));
                    String substring = message.substring(i1 + a.getWhiteList_Check().length());
                    a.getWhiteListPlayer().put(String.valueOf(user), substring);
                    a.getServer().addWhitelist(substring);
                    e.respond(a.getWhiteList_Message().replaceAll("%player%", substring));
                    if (last != null) {
                        try {
                            a.getServer().getPlayer(last).kick();
                            e.respond(a.getMessage_KickPlayer().replaceAll("%player%", last));
                        } catch (NullPointerException ee) {
                            return;
                        }
                    }
                    return;
                } else {
                    e.respond(a.getMessage_NoEnable());
                }
            }
            //绑定玩家
            int i2 = message.indexOf(a.getPlayerBind_Check());
            if (i2 != -1) {
                if (a.isPlayerBind_Enable()) {
                    String name = message.substring(i2 + a.getPlayerBind_Check().length());
                    Player player = null;
                    try {
                        player = a.getServer().getPlayer(name);
                    } catch (NullPointerException e1) {
                        e.respond(a.getMessage_PlayerOffline());
                        return;
                    }
                    e.respond(a.getMessage_GroupConfirm());
                    Confirm.start(player, user);
                    return;
                } else {
                    e.respond(a.getMessage_NoEnable());
                }
            }
            //我的信息
            int i3 = message.indexOf(a.getPlayerInfo_Check());
            if (i3 != -1) {
                if (a.isPlayerInfo_Enable()) {
                    KuPlayer kuPlayer = a.getKuPlayer().get(String.valueOf(user));
                    if (kuPlayer == null) {
                        e.respond(a.getMessage_NoBind());
                        return;
                    }

                    String online = kuPlayer.getOfflinePlayer().isOnline() ? "在线" : "离线";
                    String money = a.getEconomy().hasAccount(kuPlayer.getOfflinePlayer()) ? String.valueOf(a.getEconomy().myMoney(kuPlayer.getOfflinePlayer())) : "无";
                    String points = a.getPlayerPoints().containsKey(kuPlayer.getOfflinePlayer().getName())?String.valueOf(a.getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName())) : "无";
                    StringBuilder s = new StringBuilder("");
                    for (String ss : a.getPlayerInfo_Message()) {
                        s.append(ss
                                .replaceAll("%player%", kuPlayer.getOfflinePlayer().getName())
                                .replaceAll("%money%", money)
                                .replaceAll("%points%",points)
                                .replaceAll("%online%", online)
                                .replaceAll("%offline_time%", WxysUtil.toTime(kuPlayer.getOfflinePlayer().getLastPlayed() * 1000))
                                .replaceAll("%first_time%", WxysUtil.toTime(kuPlayer.getOfflinePlayer().getFirstPlayed() * 1000)));
                        s.append("\n");
                    }

                    e.respond(s.toString());
                    return;
                } else {
                    e.respond(a.getMessage_NoEnable());
                }
            }
            //签到
            int i4 = message.indexOf(a.getPlayerSign_Check());
            if (i4 != -1) {
                if (a.isPlayerSign_Enable()) {
                    KuPlayer kuPlayer = a.getKuPlayer().get(String.valueOf(user));
                    if (kuPlayer == null) {
                        e.respond(a.getMessage_NoBind());
                        return;
                    }
                    Long aLong = a.getPlayerSign_SignTime().get(kuPlayer.getOfflinePlayer().getName());
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 5);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    long today = calendar.getTime().getTime();
                    if (aLong == null) {
                       sign(kuPlayer,today,e);
                    } else {
                        if (System.currentTimeMillis() >= aLong) {
                            sign(kuPlayer,today,e);
                        } else {
                            e.respond(a.getPlayerSign_AlreadySign());
                        }
                    }
                    return;
                } else {
                    e.respond(a.getMessage_NoEnable());
                }
            }
        }
    }
    private void sign(KuPlayer kuPlayer,long today,EventGroupMessage e){
        a.getPlayerSign_SignTime().put(kuPlayer.getOfflinePlayer().getName(), today);
        int points = 0;
        int i5 = a.getPlayerSing_SignReward().indexOf("~");
        if (i5 != -1) {
            int min = Integer.valueOf(a.getPlayerSing_SignReward().split("~")[0]);
            int max = Integer.valueOf(a.getPlayerSing_SignReward().split("~")[1]);
            points = (int) (Math.floor(Math.random() * (max - min + 1)) + min);
        } else {
            try {
                points = Integer.valueOf(a.getPlayerSing_SignReward());
            } catch (NumberFormatException event) {
                a.log("§c检查SignReward 签到奖励积分 必须为 数字~数字 或者纯数字");
                event.printStackTrace();
            }
        }
        e.respond(a.getPlayerSign_Message().replaceAll("%reward%",""+points));
        points += a.getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName()) == null?0:a.getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName());
        a.getPlayerPoints().put(kuPlayer.getOfflinePlayer().getName(), points);
    }
}
