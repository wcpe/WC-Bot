package com.wcpe.MyKuQ;

import cn.nukkit.Server;
import com.wcpe.MyKuQ.Entity.ConsoleSender;
import com.wcpe.MyKuQ.Obj.KuPlayer;

public class MyKuQApi {
    /**
     * 给玩家添加积分
     *
     * @param kuPlayer 玩家
     * @param points   积分
     * @return boolean
     */
    public static boolean addPoints(KuPlayer kuPlayer, int points) {
        try {
            Integer point = Main.getInstance().getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName());
            points += (point == null ? 0 : point);
            Main.getInstance().getPlayerPoints().put(kuPlayer.getOfflinePlayer().getName(), points);
            return true;
        } catch (Exception s) {
            return false;
        }
    }

    /**
     * 给玩家减少积分
     *
     * @param kuPlayer 玩家
     * @param points   积分
     * @return boolean
     */
    public static boolean delPoints(KuPlayer kuPlayer, int points) {
        try {
            Integer point = Main.getInstance().getPlayerPoints().get(kuPlayer.getOfflinePlayer().getName());
            if (point > points) {
                point -= points;
                Main.getInstance().getPlayerPoints().put(kuPlayer.getOfflinePlayer().getName(), point);
            } else {
                Main.getInstance().getPlayerPoints().put(kuPlayer.getOfflinePlayer().getName(), 0);
            }
            return true;
        } catch (Exception s) {
            return false;
        }
    }

    /**
     * 给群发送消息
     *
     * @param msg 消息
     * @return boolean
     */
    public static boolean sendQQGroup(String msg) {
        try {
            Main.getInstance().getBot().getGroup(Main.getInstance().getMainAdminQQGroup()).sendMessage(msg);
            return true;
        } catch (Exception s) {
            return false;
        }
    }

    private static ConsoleSender con = new ConsoleSender(Server.getInstance());

    public static ConsoleSender getConsoleSender() {
        return con;
    }
}
