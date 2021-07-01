package top.wcpe.wcbot.entity;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * QQ 数据
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 23:01
 */
@Data
public class QQMemberData {
    private Long qq;
    private String bindPlayerName;
    private boolean isAdmin;
    private Long wcBotPoint;
    private HashMap<String, Long> signDayTimeStamp = new HashMap<>();


    public QQMemberData(Config cfg) {
        this.qq = cfg.getLong("qq");
        this.bindPlayerName = cfg.getString("bindPlayerName");
        this.isAdmin = cfg.getBoolean("isAdmin");
        this.wcBotPoint = cfg.getLong("wcBotPoint");
        ConfigSection signDayTimeStamp = cfg.getSection("signDayTimeStamp");
        if (signDayTimeStamp != null) {
            for (String key : signDayTimeStamp.getKeys(false)) {
                this.signDayTimeStamp.put(key, signDayTimeStamp.getLong(key));
            }
        }
    }

    public Config saveToConfig() {
        Config cfg = new Config();
        cfg.set("qq", this.qq);
        cfg.set("bindPlayerName", this.bindPlayerName);
        cfg.set("isAdmin", this.isAdmin);
        cfg.set("wcBotPoint", this.wcBotPoint);
        for (Map.Entry<String, Long> stringLongEntry : this.signDayTimeStamp.entrySet()) {
            cfg.set("signDayTimeStamp." + stringLongEntry.getKey(), stringLongEntry.getValue());
        }
        return cfg;
    }
}
