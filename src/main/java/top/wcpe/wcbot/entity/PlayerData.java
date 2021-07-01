package top.wcpe.wcbot.entity;

import cn.nukkit.utils.Config;
import lombok.Data;

/**
 * 玩家数据实体
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 15:15
 */
@Data
public class PlayerData {

    private String name;
    private Long qqNumber;
    private Integer playerOnlineTime;

    public PlayerData(Config cfg) {
        this.name = cfg.getString("name");
        this.qqNumber = cfg.getLong("qqNumber");
        this.playerOnlineTime = cfg.getInt("playerOnlineTime");
    }

    public Config saveToConfig() {
        Config cfg = new Config();
        cfg.set("name", this.name);
        cfg.set("qqNumber", this.qqNumber);
        cfg.set("playerOnlineTime", this.playerOnlineTime);
        return cfg;
    }
}
