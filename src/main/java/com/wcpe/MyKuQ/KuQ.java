package com.wcpe.MyKuQ;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMemberEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;

import com.wcpe.MyKuQ.Obj.KuPlayer;
import com.wcpe.MyKuQ.Utils.Confirm;
import com.wcpe.MyKuQ.Utils.WxysUtil;


import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class KuQ implements ListenerHost {
    public KuQ(Main a) {
        this.a = a;
    }

    private Main a;

    @EventHandler
    public ListeningStatus QuitGroup(MemberLeaveEvent e) {
        long group = e.getGroup().getId();
        if (isGroupList(group)) {
            String s = a.getWhiteListPlayer().get(String.valueOf(e.getMember().getId()));
            if (s != null) {
                a.getServer().removeWhitelist(s);
                a.getWhiteListPlayer().remove(String.valueOf(e.getMember().getId()));
                try {
                    a.getServer().getPlayer(s).kick();
                    a.getBot().getGroup(group).sendMessage(a.getMessage_PlayerQuitGroup().replaceAll("%player%", s));
                } catch (NullPointerException ee) {
                    return ListeningStatus.LISTENING;
                }
            }
        }
        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus PrivateMessage(FriendMessageEvent e) {

        long user = e.getSender().getId();
        String message = e.getMessage().contentToString();
        if (user == a.getMainAdminQQ() || a.getAdmin().contains(user)) {
            if (a.isSudo_Enable()) {
                if (message.contains(a.getAdmins_Sudo())) {
                    String finalMessage = message.substring(a.getAdmins_Sudo().length());
                    Server.getInstance().getScheduler().scheduleTask(Main.getInstance(), () -> {
                        Server.getInstance().dispatchCommand(MyKuQApi.getConsoleSender(), finalMessage);
                    });
                    return ListeningStatus.LISTENING;
                }
            }

            if (user == a.getMainAdminQQ()) {
                String Admin_add = a.getAdmins_Admin() + a.getAdmins_Admin_add();
                int admin_add = message.indexOf(Admin_add);
                if (admin_add != -1) {
                    try {
                        String s = message.substring(admin_add + Admin_add.length());
                        a.getAdmin().add(Long.valueOf(s));
                        e.getFriend().sendMessage("添加" + s + "为管理员成功!");
                        return ListeningStatus.LISTENING;
                    } catch (Exception ee) {
                        e.getFriend().sendMessage("使用方法:/" + a.getAdmins_Admin() + " " + a.getAdmins_Admin_add() + " QQ号");
                    }
                }
                String Admin_del = a.getAdmins_Admin() + a.getAdmins_Admin_del();
                int admin_del = message.indexOf(Admin_del);
                if (admin_del != -1) {
                    try {
                        String s = message.substring(admin_del + Admin_del.length());
                        if (a.getAdmin().contains(Long.valueOf(s))) {
                            a.getAdmin().remove(Long.valueOf(s));
                            e.getFriend().sendMessage("将" + s + "从管理员列表删除成功!");
                            return ListeningStatus.LISTENING;
                        } else {
                            e.getFriend().sendMessage(s + "不在管理员列表内!");
                            return ListeningStatus.LISTENING;
                        }
                    } catch (Exception ee) {
                        e.getFriend().sendMessage("使用方法:/" + a.getAdmins_Admin() + " " + a.getAdmins_Admin_del() + " QQ号");
                    }
                }
                String Admin_list = a.getAdmins_Admin() + a.getAdmins_Admin_list();
                int admin_list = message.indexOf(Admin_list);
                if (admin_list != -1) {
                    e.getFriend().sendMessage("管理员列表:\n" + a.getAdmin().toString());
                    return ListeningStatus.LISTENING;
                }
                String Group_add = a.getAdmins_Group() + a.getAdmins_Group_add();
                int group_add = message.indexOf(Group_add);
                if (group_add != -1) {
                    try {
                        String s = message.substring(group_add + Group_add.length(), message.length());
                        for (Group b : a.getBot().getGroups()) {
                            if (String.valueOf(b.getId()).equals(s)) {
                                a.getQQGroup().add(Long.valueOf(s));
                                e.getFriend().sendMessage("添加" + s + "到服务器群列表成功!");
                                return ListeningStatus.LISTENING;
                            }
                        }
                        e.getFriend().sendMessage("添加" + s + "到服务器群列表失败!\n该群不在QQ群聊列表中");
                        return ListeningStatus.LISTENING;
                    } catch (Exception ee) {
                        e.getFriend().sendMessage("使用方法:/" + a.getAdmins_Group() + " " + a.getAdmins_Group_add() + " 群号");
                    }
                }
                String Group_del = a.getAdmins_Group() + a.getAdmins_Group_del();
                int group_del = message.indexOf(Group_del);
                if (group_del != -1) {
                    try {
                        String s = message.substring(group_del + Group_del.length(), message.length());
                        if (a.getQQGroup().contains(Long.valueOf(s))) {
                            a.getQQGroup().remove(Long.valueOf(s));
                            e.getFriend().sendMessage("将" + s + "从管理员群列表删除成功!");
                            return ListeningStatus.LISTENING;
                        } else {
                            e.getFriend().sendMessage("删除" + s + "到服务器群列表失败!\n该群不在管理群聊列表中");
                            return ListeningStatus.LISTENING;
                        }
                    } catch (Exception ee) {
                        e.getFriend().sendMessage("使用方法:/" + a.getAdmins_Group() + " " + a.getAdmins_Group_del() + "群号");
                    }
                }
                String Group_list = a.getAdmins_Group() + a.getAdmins_Group_list();
                int group_list = message.indexOf(Group_list);
                if (group_list != -1) {
                    e.getFriend().sendMessage("群列表:\n" + a.getQQGroup().toString());
                    return ListeningStatus.LISTENING;
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
                    e.getFriend().sendMessage("将" + s + "加入白名单成功!");
                    if (a.getWhiteListPlayer().containsKey(s1[0])) {
                        a.getServer().removeWhitelist(s2);
                        a.getWhiteListPlayer().remove(s1[0]);
                        try {
                            a.getServer().getPlayer(s2).kick();
                            e.getFriend().sendMessage("发现玩家" + s2 + "在线 踢出玩家成功~");
                        } catch (NullPointerException ee) {
                            return ListeningStatus.LISTENING;
                        }
                    }
                    return ListeningStatus.LISTENING;
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/White add QQ号 玩家id");
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
                        e.getFriend().sendMessage("将" + s + "从白名单删除成功!");
                        try {
                            a.getServer().getPlayer(s1).kick();
                            e.getFriend().sendMessage("发现玩家" + s1 + "在线 踢出玩家成功~");
                        } catch (NullPointerException ee) {
                            return ListeningStatus.LISTENING;
                        }
                        return ListeningStatus.LISTENING;
                    } else {
                        e.getFriend().sendMessage(s + "不存在于白名单!");
                    }
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/White del QQ号");
                }
            }
            String White_list = a.getAdmins_White() + a.getAdmins_White_list();
            int white_list = message.indexOf(White_list);
            if (white_list != -1) {
                e.getFriend().sendMessage("白名单列表:\n" + a.getWhiteListPlayer().toString());
                return ListeningStatus.LISTENING;
            }
            String Bind_add = a.getAdmins_Bind() + a.getAdmins_Bind_add();
            int bind_add = message.indexOf(Bind_add);
            if (bind_add != -1) {
                try {
                    String s = message.substring(bind_add + Bind_add.length());
                    String[] s1 = s.split(" ");

                    a.getKuPlayer().put(s1[0], new KuPlayer(a.getServer().getOfflinePlayer(s1[1]), Long.valueOf(s1[0])));
                    e.getFriend().sendMessage("添加玩家:" + s1[1] + " QQ:" + s1[0] + "成功");
                    return ListeningStatus.LISTENING;
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/Bind add QQ号 玩家id");
                }
            }
            String Bind_del = a.getAdmins_Bind() + a.getAdmins_Bind_del();
            int bind_del = message.indexOf(Bind_del);
            if (bind_del != -1) {
                try {
                    String s = message.substring(bind_del + Bind_del.length());
                    if (a.getKuPlayer().containsKey(s)) {
                        a.getKuPlayer().remove(s);
                        e.getFriend().sendMessage("将" + s + "删除成功!");
                        return ListeningStatus.LISTENING;
                    } else {
                        e.getFriend().sendMessage(s + "玩家未绑定!");
                    }
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/Bind del QQ号");
                }
            }
            String Bind_list = a.getAdmins_Bind() + a.getAdmins_Bind_list();
            int bind_list = message.indexOf(Bind_list);
            if (bind_list != -1) {
                StringBuilder sb = new StringBuilder();
                a.getKuPlayer().entrySet().forEach((s) -> {
                    sb.append("QQ:" + s.getKey() + " 绑定:" + s.getValue().getOfflinePlayer().getName() + "\n");
                });
                sb.setCharAt(sb.length() - 1, '\0');
                e.getFriend().sendMessage("玩家绑定列表:\n" + sb.toString());
                return ListeningStatus.LISTENING;
            }

            String Point_add = a.getAdmins_Point() + a.getAdmins_Point_add();
            int point_add = message.indexOf(Point_add);
            if (point_add != -1) {
                try {
                    String s = message.substring(point_add + Point_add.length());
                    String[] s1 = s.split(" ");
                    KuPlayer kuPlayer = a.getKuPlayer().get(s1[0]);
                    MyKuQApi.addPoints(kuPlayer, Integer.valueOf(s1[1]));
                    e.getFriend().sendMessage("添加玩家:" + kuPlayer.getOfflinePlayer().getName() + " 积分:" + s1[1] + "成功");
                    return ListeningStatus.LISTENING;
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/Point add QQ号 积分");
                }
            }

            String Point_del = a.getAdmins_Point() + a.getAdmins_Point_del();
            int point_del = message.indexOf(Point_del);
            if (point_del != -1) {
                try {
                    String s = message.substring(point_del + Point_del.length());
                    String[] s1 = s.split(" ");
                    KuPlayer kuPlayer = a.getKuPlayer().get(s1[0]);
                    MyKuQApi.delPoints(kuPlayer, Integer.valueOf(s1[1]));
                    e.getFriend().sendMessage("减少玩家:" + kuPlayer.getOfflinePlayer().getName() + " 积分:" + s1[1] + "成功");
                    return ListeningStatus.LISTENING;
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/Point del QQ号 积分");
                }
            }

            String Point_check = a.getAdmins_Point() + a.getAdmins_Point_check();
            int point_check = message.indexOf(Point_check);
            if (point_check != -1) {
                try {
                    String s = message.substring(point_check + Point_check.length());
                    String[] s1 = s.split(" ");
                    KuPlayer kuPlayer = a.getKuPlayer().get(s1[0]);
                    e.getFriend().sendMessage("玩家:" + kuPlayer.getOfflinePlayer().getName() + " 积分:" + a.getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName()));
                    return ListeningStatus.LISTENING;
                } catch (Exception ee) {
                    e.getFriend().sendMessage("使用方法:/Point check QQ号");
                }
            }
        }
        return ListeningStatus.LISTENING;
    }


    /***
     * @param group qq群号
     * @return 返回qq群是否为主副群
     */
    private boolean isGroupList(long group) {
        return (a.getQQGroup().contains(group) || a.getMainAdminQQGroup() == group);
    }

    @EventHandler
    public ListeningStatus adminGroupMessage(GroupMessageEvent e) {
        long group = e.getGroup().getId();
        if (isGroupList(group)) {
            long user = e.getSender().getId();
            String message = e.getMessage().contentToString();
            if (user == a.getMainAdminQQ() && a.isSudo_Enable() && message.contains(a.getAdmins_Sudo())) {
                String finalMessage = message.substring(a.getAdmins_Sudo().length());
                Server.getInstance().getScheduler().scheduleTask(Main.getInstance(), () -> {
                    Server.getInstance().dispatchCommand(MyKuQApi.getConsoleSender(), finalMessage);
                });
                return ListeningStatus.LISTENING;
            }
        }
        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus playerGroupMessage(GroupMessageEvent e) {
        long group = e.getGroup().getId();
        if (isGroupList(group)) {
            long user = e.getSender().getId();
            String message = e.getMessage().contentToString();


            //list
            if (message.equals(a.getListCommands())) {
                StringBuilder s = new StringBuilder("");
                Map<UUID, Player> onlinePlayers = a.getServer().getOnlinePlayers();
                for (Map.Entry<UUID, Player> p : a.getServer().getOnlinePlayers().entrySet()) {
                    s.append(p.getValue().getName());
                    s.append(" ");
                }
                e.getGroup().sendMessage(a.getListMessage().replaceAll("%online_number%", "" + onlinePlayers.size()).replaceAll("%online_player%", s.toString()));
                return ListeningStatus.LISTENING;
            }
            //申请白名单
            int i1 = message.indexOf(a.getWhiteList_Check());
            if (i1 != -1) {
                if (a.isWhiteList_Enable()) {
                    String last = a.getWhiteListPlayer().get(String.valueOf(user));
                    String substring = message.substring(i1 + a.getWhiteList_Check().length());
                    a.getWhiteListPlayer().put(String.valueOf(user), substring);
                    a.getServer().addWhitelist(substring);
                    e.getGroup().sendMessage(a.getWhiteList_Message().replaceAll("%player%", substring));
                    if (last != null) {
                        try {
                            a.getServer().getPlayer(last).kick();
                            e.getGroup().sendMessage(a.getMessage_KickPlayer().replaceAll("%player%", last));
                        } catch (NullPointerException ee) {
                            return ListeningStatus.LISTENING;
                        }
                    }
                    return ListeningStatus.LISTENING;
                } else {
                    e.getGroup().sendMessage(a.getMessage_NoEnable());
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
                        e.getGroup().sendMessage(a.getMessage_PlayerOffline());
                        return ListeningStatus.LISTENING;
                    }
                    e.getGroup().sendMessage(a.getMessage_GroupConfirm());
                    Confirm.start(player, user);
                    return ListeningStatus.LISTENING;
                } else {
                    e.getGroup().sendMessage(a.getMessage_NoEnable());
                }
            }
            //我的信息
            int i3 = message.indexOf(a.getPlayerInfo_Check());
            if (i3 != -1) {
                if (a.isPlayerInfo_Enable()) {
                    KuPlayer kuPlayer = a.getKuPlayer().get(String.valueOf(user));
                    if (kuPlayer == null) {
                        e.getGroup().sendMessage(a.getMessage_NoBind());
                        return ListeningStatus.LISTENING;
                    }

                    String online = kuPlayer.getOfflinePlayer().isOnline() ? "在线" : "离线";
                    String money = a.getEconomy().hasAccount(kuPlayer.getOfflinePlayer()) ? String.valueOf(a.getEconomy().myMoney(kuPlayer.getOfflinePlayer())) : "无";
                    String points = a.getPlayerPoints().containsKey(kuPlayer.getOfflinePlayer().getName()) ? String.valueOf(a.getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName())) : "无";
                    StringBuilder s = new StringBuilder("");
                    for (String ss : a.getPlayerInfo_Message()) {
                        s.append(ss
                                .replaceAll("%player%", kuPlayer.getOfflinePlayer().getName())
                                .replaceAll("%money%", money)
                                .replaceAll("%points%", points)
                                .replaceAll("%online%", online)
                                .replaceAll("%offline_time%", WxysUtil.toTime(kuPlayer.getOfflinePlayer().getLastPlayed() * 1000))
                                .replaceAll("%first_time%", WxysUtil.toTime(kuPlayer.getOfflinePlayer().getFirstPlayed() * 1000))
                                .replaceAll("%sign_timer%", a.getPlayerSign_SignTimer().get(kuPlayer.getOfflinePlayer().getName()) == null ?""+ 0 : ""+a.getPlayerSign_SignTimer().get(kuPlayer.getOfflinePlayer().getName())));
                        s.append("\n");
                    }

                    e.getGroup().sendMessage(s.toString());
                    return ListeningStatus.LISTENING;
                } else {
                    e.getGroup().sendMessage(a.getMessage_NoEnable());
                    return ListeningStatus.LISTENING;
                }
            }
            //签到
            int i4 = message.indexOf(a.getPlayerSign_Check());
            if (i4 != -1) {
                if (a.isPlayerSign_Enable()) {
                    KuPlayer kuPlayer = a.getKuPlayer().get(String.valueOf(user));
                    if (kuPlayer == null) {
                        e.getGroup().sendMessage(a.getMessage_NoBind());
                        return ListeningStatus.LISTENING;
                    }
                    Long aLong = a.getPlayerSign_SignData().get(kuPlayer.getOfflinePlayer().getName());
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 5);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    long today = calendar.getTime().getTime();
                    if (aLong == null) {
                        sign(kuPlayer, today, e);
                    } else {
                        if (System.currentTimeMillis() >= aLong) {
                            sign(kuPlayer, today, e);
                        } else {
                            e.getGroup().sendMessage(a.getPlayerSign_AlreadySign());
                        }
                    }
                    return ListeningStatus.LISTENING;
                } else {
                    e.getGroup().sendMessage(a.getMessage_NoEnable());
                }
            }
            //群 - 服 对话
            int i = message.indexOf(a.getQQGroupCheck());
            if (a.getQQGroupCheck().equals("")) {
                String s = a.getSendGameMessage().replaceAll("%player%", user + "").replaceAll("%chat%", message).replaceAll("%player_name%", e.getSender().getNameCard());
                Server.getInstance().broadcastMessage(s);
                if (a.isQQ_to_Game_SendSuccessTipEnable()) {
                    e.getGroup().sendMessage(a.getQQ_to_Game_SendSuccessTip());
                }
            } else if (i != -1) {
                message = message.substring(i + a.getQQGroupCheck().length());
                String s = a.getSendGameMessage().replaceAll("%player%", user + "").replaceAll("%chat%", message).replaceAll("%player_name%", e.getSender().getNameCard());
                if (a.isQQ_to_Game_isRemoveColor()) {
                    if (s.contains("&")) {
                        s = s.replaceAll("&", "");
                    }
                } else {
                    s = s.replaceAll("&", "§");
                }
                Server.getInstance().broadcastMessage(s);
                if (a.isQQ_to_Game_SendSuccessTipEnable()) {
                    e.getGroup().sendMessage(a.getQQ_to_Game_SendSuccessTip());
                }
            }

        }
        return ListeningStatus.LISTENING;
    }

    private void sign(KuPlayer kuPlayer, long today, GroupMessageEvent e) {
        a.getPlayerSign_SignData().put(kuPlayer.getOfflinePlayer().getName(), today);
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
        e.getGroup().sendMessage(a.getPlayerSign_Message().replaceAll("%reward%", "" + points));
        MyKuQApi.addPoints(kuPlayer, points);
        int timer = a.getPlayerSign_SignTimer().get(kuPlayer.getOfflinePlayer().getName()) == null ? 0 : a.getPlayerSign_SignTimer().get(kuPlayer.getOfflinePlayer().getName());
        a.getPlayerSign_SignTimer().put(kuPlayer.getOfflinePlayer().getName(), timer + 1);
    }

}
