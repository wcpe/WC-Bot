package top.wcpe.wcbot.entity;

import cn.nukkit.utils.Config;
import lombok.Data;

import java.util.List;

/**
 * 服务器数据存储
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 21:43
 */
@Data
public class ServerData {
    private List<Long> enableQQGroup;

    public ServerData(Config cfg) {
        this.enableQQGroup = cfg.getLongList("enableQQGroup");
    }

    public Config saveToConfig() {
        Config cfg = new Config();
        cfg.set("enableQQGroup", this.enableQQGroup);
        return cfg;
    }
}
