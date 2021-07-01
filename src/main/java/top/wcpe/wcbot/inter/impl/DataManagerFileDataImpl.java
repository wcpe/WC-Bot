package top.wcpe.wcbot.inter.impl;

import cn.nukkit.utils.Config;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcbot.entity.PlayerData;
import top.wcpe.wcbot.entity.QQMemberData;
import top.wcpe.wcbot.entity.ServerData;
import top.wcpe.wcbot.inter.DataManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * 数据管理类 本地文件实现
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 15:09
 */
public class DataManagerFileDataImpl implements DataManager {

    private Path getPlayerDataDirPath() {
        Path playerDataDirPath = WCBot.getInstance().getDataFolder().toPath().resolve("playerData");
        if (Files.notExists(playerDataDirPath)) {
            try {
                Files.createDirectory(playerDataDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return playerDataDirPath;
    }

    private Path getPlayerDataFilePath(String name) {
        Path playerDataFilePath = getPlayerDataDirPath().resolve(name + ".yml");
        if (Files.notExists(playerDataFilePath)) {
            try {
                Files.createFile(playerDataFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return playerDataFilePath;
    }

    private final HashMap<String, PlayerData> playerDataMap = new HashMap<>();

    @Override
    public PlayerData getPlayerData(String name) {
        PlayerData playerData = this.playerDataMap.get(name);
        if (playerData == null) {
            playerData = new PlayerData(new Config(getPlayerDataFilePath(name).toFile()));
            playerData.setName(name);
            this.playerDataMap.put(name, playerData);
        }
        return playerData;
    }

    @Override
    public boolean savePlayerData(PlayerData playerData) {
        return playerData.saveToConfig().save(getPlayerDataFilePath(playerData.getName()).toFile());
    }

    @Override
    public List<PlayerData> listPlayerData() {
        return null;
    }

    private Path getQQMemberDataDirPath() {
        Path qqMemberDataDirPath = WCBot.getInstance().getDataFolder().toPath().resolve("qqMemberData");
        if (Files.notExists(qqMemberDataDirPath)) {
            try {
                Files.createDirectory(qqMemberDataDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return qqMemberDataDirPath;
    }

    private Path getQQMemberDataFilePath(Long qq) {
        Path qqMemberDataFilePath = getQQMemberDataDirPath().resolve(qq + ".yml");
        if (Files.notExists(qqMemberDataFilePath)) {
            try {
                Files.createFile(qqMemberDataFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return qqMemberDataFilePath;
    }

    private final HashMap<Long, QQMemberData> qqMemberDataMap = new HashMap<>();


    @Override
    public QQMemberData getQQMemberData(Long qq) {
        QQMemberData qqMemberData = this.qqMemberDataMap.get(qq);
        if (qqMemberData == null) {
            qqMemberData = new QQMemberData(new Config(getQQMemberDataFilePath(qq).toFile()));
            qqMemberData.setQq(qq);
            this.qqMemberDataMap.put(qq, qqMemberData);
        }
        return qqMemberData;
    }

    @Override
    public boolean saveQQMemberData(QQMemberData qqMemberData) {
        return qqMemberData.saveToConfig().save(getQQMemberDataFilePath(qqMemberData.getQq()).toFile());
    }

    @Override
    public List<QQMemberData> listQQMemberData() {
        return null;
    }

    private Path getServerDataFilePath() {
        Path serverDataFilePath = WCBot.getInstance().getDataFolder().toPath().resolve("serverData.yml");
        if (Files.notExists(serverDataFilePath)) {
            try {
                Files.createFile(serverDataFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return serverDataFilePath;
    }

    private ServerData serverData;

    @Override
    public ServerData getServerData() {
        if (serverData == null) {
            serverData = new ServerData(new Config(getServerDataFilePath().toFile()));
        }
        return serverData;
    }

    @Override
    public boolean saveServerData(ServerData serverData) {
        return serverData.saveToConfig().save(getServerDataFilePath().toFile());
    }
}
