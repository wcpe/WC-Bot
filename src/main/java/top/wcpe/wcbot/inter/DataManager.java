package top.wcpe.wcbot.inter;

import top.wcpe.wcbot.entity.PlayerData;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcbot.entity.ServerData;

import java.util.List;

/**
 * 数据管理类接口
 */
public interface DataManager {

    PlayerData getPlayerData(String name);

    boolean savePlayerData(PlayerData playerData);

    List<PlayerData> listPlayerData();


    QQMemberData getQQMemberData(Long qq);

    boolean saveQQMemberData(QQMemberData qqMemberData);

    List<QQMemberData> listQQMemberData();

    ServerData getServerData();

    boolean saveServerData(ServerData serverData);


}
