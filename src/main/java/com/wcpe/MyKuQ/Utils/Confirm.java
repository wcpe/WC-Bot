package com.wcpe.MyKuQ.Utils;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import com.wcpe.MyKuQ.Obj.KuPlayer;
import com.wcpe.MyKuQ.Main;

import java.util.HashMap;
import java.util.UUID;

public class Confirm implements Listener {
    public Confirm(Main a) {
        Confirm.a = a;
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
            p.sendMessage(a.getMessage_GameConfirmFinish());
            e.setCancelled(true);
        }
    }

    public static void start(Player p, long qq) {
        codes.put(p.getUniqueId(), qq);
        a.getServer().getScheduler().scheduleDelayedTask(a,()->{
            codes.remove(p.getUniqueId());
        },10*20);
        p.sendMessage(a.getMessage_GameConfirm().replaceAll("%qq%",""+qq));
    }

}
