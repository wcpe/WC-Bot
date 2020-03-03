package com.wcpe.MyKuQ;

import cc.moecraft.icq.PicqBotX;
import cc.moecraft.icq.PicqConfig;
import cc.moecraft.icq.accounts.BotAccount;
import cc.moecraft.icq.sender.IcqHttpApi;
import cc.moecraft.icq.sender.returndata.returnpojo.get.RFriend;
import cc.moecraft.icq.sender.returndata.returnpojo.get.RGroup;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main extends PluginBase implements Listener {

    private long MainAdminQQGroup;
    private long MainAdminQQ;

    private String PlayerJoinMessage;
    private boolean isPlayerJoinMessage;
    private String PlayerQuitMessage;
    private boolean isPlayerQuitMessage;

    private String GameCheck;
    private String SendQQGroupMessage;
    private boolean Game_to_QQ_isRemoveColor;
    private boolean Game_to_QQ_SendSuccessTipEnable;
    private String Game_to_QQ_SendSuccessTip;

    private String QQGroupCheck;
    private String SendGameMessage;
    private boolean QQ_to_Game_isRemoveColor;
    private boolean isIDorName;
    private boolean QQ_to_Game_SendSuccessTipEnable;
    private String QQ_to_Game_SendSuccessTip;

    private String ListCommands;
    private String ListMessage;

    private int System_ListenPORT;
    private String System_IP;
    private int System_PORT;

    private boolean Rcon_Enable;
    private String Rcon_IP;
    private int Rcon_PORT;
    private String Rcon_PASSWORD;
    private boolean isPrivate;

    private boolean WhiteList_Enable;
    private String WhiteList_Check;
    private String WhiteList_Message;

    private List<Long> Admin;
    private List<Long> QQGroup;

    private HashMap<String,String> WhiteListPlayer = new HashMap<String,String>();


    @Override
    public void onLoad() {
        getLogger().info(TextFormat.GREEN + "MyKuQ 正在加载");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkConfig();
        saveResource("data.yml");
        reload();
        loadKuQ();
        if (isRcon_Enable()) {
            loadRcon();
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(TextFormat.GREEN + "MyKuQ 加载完成");
    }

    @Override
    public void onDisable() {
        savedata();
        getLogger().info(TextFormat.RED + "MyKuQ 卸载完成");
    }

    private void reload() {
        reloadConfig();
        MainAdminQQGroup = this.getConfig().getLong("MainAdminQQGroup");
        MainAdminQQ = this.getConfig().getLong("MainAdminQQ");

        PlayerJoinMessage = this.getConfig().getString("PlayerJoinMessage.Message", "%player%加入了服务器");
        isPlayerJoinMessage = this.getConfig().getBoolean("PlayerJoinMessage.Enable", false);

        PlayerQuitMessage = this.getConfig().getString("PlayerQuitMessage.Message", "%player%退出了服务器");
        isPlayerQuitMessage = this.getConfig().getBoolean("PlayerQuitMessage.Enable", false);

        GameCheck = this.getConfig().getString("Check.GameCheck.Message", "群:");
        SendQQGroupMessage = this.getConfig().getString("Check.GameCheck.SendQQGroupMessage", "%player%>>>%chat%");
        Game_to_QQ_isRemoveColor = this.getConfig().getBoolean("Check.GameCheck.isRemoveColor", true);
        QQ_to_Game_SendSuccessTipEnable = this.getConfig().getBoolean("Check.GameCheck.Tip.Enable", true);
        QQ_to_Game_SendSuccessTip = this.getConfig().getString("Check.GameCheck.Tip.Message", "消息发送成功~");

        QQGroupCheck = this.getConfig().getString("Check.QQGroupCheck.Message", "服:");
        SendGameMessage = this.getConfig().getString("Check.QQGroupCheck.SendGameMessage", "[人工智障]%player%>>>%chat%");
        isIDorName = this.getConfig().getBoolean("Check.QQGroupCheck.isIDorName", true);
        QQ_to_Game_isRemoveColor = this.getConfig().getBoolean("Check.QQGroupCheck.isRemoveColor", true);
        Game_to_QQ_SendSuccessTipEnable = this.getConfig().getBoolean("Check.QQGroupCheck.Tip.Enable", true);
        Game_to_QQ_SendSuccessTip = this.getConfig().getString("Check.QQGroupCheck.Tip.Message", "消息发送成功~");

        ListCommands = this.getConfig().getString("ListCommands", "/list");
        ListMessage = this.getConfig().getString("ListMessage", "当前在线人数:%online_number%\n玩家列表:%online_player%");

        System_ListenPORT = this.getConfig().getInt("System.ListenPort", 12138);
        System_IP = this.getConfig().getString("System.Ip", "127.0.0.1");
        System_PORT = this.getConfig().getInt("System.Port", 5700);

        Rcon_Enable = this.getConfig().getBoolean("Rcon.Enable", false);
        Rcon_IP = this.getConfig().getString("Rcon.Ip", "0.0.0.0");
        Rcon_PORT = this.getConfig().getInt("Rcon.Port", 19132);
        Rcon_PASSWORD = this.getConfig().getString("Rcon.Password", "abcde");
        isPrivate = this.getConfig().getBoolean("Rcon.Private", true);

        WhiteList_Enable = this.getConfig().getBoolean("WhiteList.Enable");
        WhiteList_Check = this.getConfig().getString("WhiteList.Check");
        WhiteList_Message = this.getConfig().getString("WhiteList.Message");


        Config data = new Config(new File(this.getDataFolder(), "data.yml"));
        Admin = data.getLongList("AdminQQ");
        QQGroup = data.getLongList("QQGroup");
        if(data.get("WhiteList")!=null){
            WhiteListPlayer.clear();
            WhiteListPlayer.putAll((Map<String,String>) data.get("WhiteList"));
        }

    }

    private PicqConfig config;
    private PicqBotX bot;
    private BotAccount myMcBot;
    private IcqHttpApi a;

    private void loadKuQ() {
        try {
            config = new PicqConfig(System_ListenPORT);
            bot = new PicqBotX(config);
            bot.addAccount("MyMcBot", System_IP, System_PORT);
            bot.getEventManager().registerListeners(new KuQ(this));
            bot.startBot();

            myMcBot = new BotAccount("MyMcBot", bot, System_IP, System_PORT);
            a = new IcqHttpApi(bot, myMcBot, System_IP, System_PORT);
            StringBuilder msg = new StringBuilder("[MyKuQ]加载成功!");
            msg.append("\n插件监听端口:" + System_ListenPORT);
            msg.append("\nHTTPIp:" + System_IP);
            msg.append("\nHTTP端口:" + System_PORT);
            a.sendGroupMsg(MainAdminQQGroup, msg.toString());
            a.sendPrivateMsg(MainAdminQQ, msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Rcon rcon;

    private void loadRcon() {
        try {
            rcon = new Rcon(Rcon_IP, Rcon_PORT, Rcon_PASSWORD.getBytes());
        } catch (IOException | AuthenticationException e) {
            e.printStackTrace();
        }
    }

    private void savedata() {
        File file = new File(this.getDataFolder(), "data.yml");
        Config data = new Config(file);
        data.set("AdminQQ", Admin);
        data.set("QQGroup", QQGroup);
        data.set("WhiteList", WhiteListPlayer);
        data.save(file);
    }

    private void checkConfig()  {
        Config a = new Config();
        a.load(this.getResource("config.yml"));
        Set<String> keys = this.getConfig().getKeys(true);
        for(String b:a.getKeys(true)){
            if(!keys.contains(b)){
                this.getConfig().set(b,a.get(b));
            }
        }
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mykuq.use")) {
            sender.sendMessage("§c您没得权限!");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                reload();
                loadKuQ();
                savedata();
                if (isRcon_Enable()) {
                    loadRcon();
                }
            }
            return true;
        } else if (args.length == 3) {
            if (args[0].equals("sendQQ") && args[1] != null && args[2] != null) {
                for (RFriend g : a.getFriendList().getData()) {
                    if (String.valueOf(g.getUserId()).equals(args[1])) {
                        a.sendPrivateMsg(Long.valueOf(args[1]), args[2]);
                        return true;
                    }
                }
                sender.sendMessage("§c未找到该QQ!");
                return true;
            }
            if (args[0].equals("sendGroup") && args[1] != null && args[2] != null) {
                for (RGroup g : a.getGroupList().getData()) {
                    if (String.valueOf(g.getGroupId()).equals(args[1])) {
                        a.sendGroupMsg(Long.valueOf(args[1]), args[2]);
                        return true;
                    }
                }
                sender.sendMessage("§c未找到该群聊!");
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (isPlayerJoinMessage) {
            a.sendGroupMsg(MainAdminQQGroup, PlayerJoinMessage.replaceAll("%player%", e.getPlayer().getName()));
            for (long lon : QQGroup) {
                a.sendGroupMsg(lon, PlayerJoinMessage.replaceAll("%player%", e.getPlayer().getName()));
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        if (isPlayerQuitMessage) {
            a.sendGroupMsg(MainAdminQQGroup, PlayerQuitMessage.replaceAll("%player%", e.getPlayer().getName()));
            for (long lon : QQGroup) {
                a.sendGroupMsg(lon, PlayerQuitMessage.replaceAll("%player%", e.getPlayer().getName()));
            }
        }
    }

    @EventHandler
    public void chat(PlayerChatEvent e) {
        int i = e.getMessage().indexOf(GameCheck);
        if (getGameCheck().equals("")) {
            String s = SendQQGroupMessage.replaceAll("%player%", e.getPlayer().getName()).replaceAll("%chat%", e.getMessage());
            a.sendGroupMsg(MainAdminQQGroup, s);
            for (long lon : QQGroup) {
                a.sendGroupMsg(lon, s);
            }
            if(isGame_to_QQ_SendSuccessTipEnable())
            e.getPlayer().sendMessage(getGame_to_QQ_SendSuccessTip());
        } else if (i != -1) {
            String s = SendQQGroupMessage.replaceAll("%player%", e.getPlayer().getName()).replaceAll("%chat%", e.getMessage().substring(i + GameCheck.length(), e.getMessage().length()));
            if (Game_to_QQ_isRemoveColor) {
                if (s.contains("&"))
                    s = s.replaceAll("&", "");
            } else {
                s = s.replaceAll("&", "§");
            }
            a.sendGroupMsg(MainAdminQQGroup, s);
            for (long lon : QQGroup) {
                a.sendGroupMsg(lon, s);
            }
            if(isGame_to_QQ_SendSuccessTipEnable())
                e.getPlayer().sendMessage(getGame_to_QQ_SendSuccessTip());
        }
    }


    public boolean isWhiteList_Enable() {
        return WhiteList_Enable;
    }

    public String getWhiteList_Check() {
        return WhiteList_Check;
    }

    public String getWhiteList_Message() {
        return WhiteList_Message;
    }

    public HashMap<String,String> getWhiteListPlayer() {
        return WhiteListPlayer;
    }
    public long getMainAdminQQGroup() {
        return MainAdminQQGroup;
    }

    public long getMainAdminQQ() {
        return MainAdminQQ;
    }

    public String getPlayerJoinMessage() {
        return PlayerJoinMessage;
    }

    public boolean isPlayerJoinMessage() {
        return isPlayerJoinMessage;
    }

    public String getPlayerQuitMessage() {
        return PlayerQuitMessage;
    }

    public boolean isPlayerQuitMessage() {
        return isPlayerQuitMessage;
    }

    public String getGameCheck() {
        return GameCheck;
    }

    public String getSendQQGroupMessage() {
        return SendQQGroupMessage;
    }

    public boolean isGame_to_QQ_isRemoveColor() {
        return Game_to_QQ_isRemoveColor;
    }

    public boolean isGame_to_QQ_SendSuccessTipEnable() {
        return Game_to_QQ_SendSuccessTipEnable;
    }

    public String getGame_to_QQ_SendSuccessTip() {
        return Game_to_QQ_SendSuccessTip;
    }

    public String getQQGroupCheck() {
        return QQGroupCheck;
    }

    public String getSendGameMessage() {
        return SendGameMessage;
    }

    public boolean isQQ_to_Game_isRemoveColor() {
        return QQ_to_Game_isRemoveColor;
    }

    public boolean isIDorName() {
        return isIDorName;
    }

    public boolean isQQ_to_Game_SendSuccessTipEnable() {
        return QQ_to_Game_SendSuccessTipEnable;
    }

    public String getQQ_to_Game_SendSuccessTip() {
        return QQ_to_Game_SendSuccessTip;
    }

    public String getListCommands() {
        return ListCommands;
    }

    public String getListMessage() {
        return ListMessage;
    }

    public int getSystem_ListenPORT() {
        return System_ListenPORT;
    }

    public String getSystem_IP() {
        return System_IP;
    }

    public int getSystem_PORT() {
        return System_PORT;
    }

    public boolean isRcon_Enable() {
        return Rcon_Enable;
    }

    public String getRcon_IP() {
        return Rcon_IP;
    }

    public int getRcon_PORT() {
        return Rcon_PORT;
    }

    public String getRcon_PASSWORD() {
        return Rcon_PASSWORD;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public List<Long> getAdmin() {
        return Admin;
    }

    public List<Long> getQQGroup() {
        return QQGroup;
    }

    public PicqBotX getBot() {
        return bot;
    }

    public BotAccount getMyMcBot() {
        return myMcBot;
    }

    public IcqHttpApi getA() {
        return a;
    }

    public Rcon getRcon() {
        return rcon;
    }
}