package com.wcpe.MyKuQ;

import cn.nukkit.IPlayer;

public class KuPlayer {
    public KuPlayer(IPlayer offlinePlayer, long qq) {
        this.offlinePlayer = offlinePlayer;
        this.qq = qq;
    }

    private IPlayer offlinePlayer;
    private long qq;

    public IPlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public long getQq() {
        return qq;
    }
}
