package com.wcpe.MyKuQ.Utils;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.utils.Config;
import com.wcpe.MyKuQ.KuPlayer;
import com.wcpe.MyKuQ.Main;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class Confirm implements Listener {
    public Confirm(Main a) {
        this.a = a;
    }

    private static Main a;
    private static HashMap<UUID, Long> codes = new HashMap<>();

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        if (!codes.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        Player p = e.getPlayer();
        if (e.getMessage().startsWith("confirm ") && e.getMessage().replace("confirm ", "")
                .equalsIgnoreCase(String.valueOf(codes.get(p.getUniqueId())))) {
            a.getKuPlayer().put(String.valueOf(codes.get(p.getUniqueId())), new KuPlayer(a.getServer().getOfflinePlayer(p.getUniqueId()), codes.get(p.getUniqueId())));
            codes.remove(p.getUniqueId());
            p.sendMessage("§a验证成功!");
            e.setCancelled(true);
        }
    }

    public static void start(Player p, long qq) {
        codes.put(p.getUniqueId(), qq);
        a.getServer().getScheduler().scheduleDelayedTask(a,()->{
            codes.remove(p.getUniqueId());
        },10*20);
        p.sendMessage("§6Q群内的: " + qq + " 用户正在绑定此账号\n§6请在聊天栏输入 confirm " + qq + " 以完成绑定\n§6如果非本人操作请忽略此消息\n§7十秒有效时间~!");
    }

}
