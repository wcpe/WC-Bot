package com.wcpe.MyKuQ;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.wcpe.MyKuQ.Gui.ExChangeGui;
import com.wcpe.MyKuQ.Gui.InterFace.Gui;
import com.wcpe.MyKuQ.Jframe.EventJframe;
import com.wcpe.MyKuQ.Obj.KuPlayer;
import com.wcpe.MyKuQ.Obj.SignReward;
import com.wcpe.MyKuQ.Utils.Confirm;
import com.wcpe.MyKuQ.Utils.UpCheck;
import com.wcpe.MyKuQ.Utils.WxysUtil;
import me.onebone.economyapi.EconomyAPI;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.network.WrongPasswordException;
import net.mamoe.mirai.utils.BotConfiguration;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends PluginBase implements Listener {
    public static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private EconomyAPI economy;

    private boolean MainQQGroupTip_Enable;
    private List<String> MainQQGroupTip_Message;
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

    private long System_User;
    private String System_Password;

    private boolean WhiteList_Enable;
    private String WhiteList_Check;
    private String WhiteList_Message;

    private boolean PlayerBind_Enable;
    private String PlayerBind_Check;
    private String PlayerBind_Message;
    private int PlayerBind_ConfirmTime;

    private boolean PlayerInfo_Enable;
    private String PlayerInfo_Check;
    private int PlayerInfo_ConfirmTime;
    private List<String> PlayerInfo_Message;

    private List<Long> Admin;
    private List<Long> QQGroup;

    private HashMap<String, String> WhiteListPlayer = new HashMap<String, String>();

    private HashMap<String, KuPlayer> KuPlayer = new HashMap<>();

    private boolean PlayerSign_Enable;
    private String PlayerSign_Check;
    private String PlayerSign_Message;
    private String PlayerSign_AlreadySign;
    private List<SignReward> PlayerSign_ExChangeReward;
    private String PlayerSign_ExChangeRewardSetting_Title;
    private String PlayerSign_ExChangeRewardSetting_PointsNotEnough;
    private String PlayerSign_ExChangeRewardSetting_Success;
    private String PlayerSign_ExChangeRewardSetting_Item;
    private String PlayerSign_ExChangeRewardSetting_Confirm;
    private String PlayerSing_SignReward;
    private HashMap<String, Long> PlayerSign_SignData = new HashMap<>();
    private HashMap<String, Integer> PlayerSign_SignTimer = new HashMap<>();
    private HashMap<String, Integer> PlayerPoints = new HashMap<>();

    private String Admins_White;
    private String Admins_White_add;
    private String Admins_White_del;
    private String Admins_White_list;

    private String Admins_Group;
    private String Admins_Group_add;
    private String Admins_Group_del;
    private String Admins_Group_list;

    private String Admins_Admin;
    private String Admins_Admin_add;
    private String Admins_Admin_del;
    private String Admins_Admin_list;

    private String Admins_Bind;
    private String Admins_Bind_add;
    private String Admins_Bind_del;
    private String Admins_Bind_list;

    private String Admins_Point;
    private String Admins_Point_add;
    private String Admins_Point_del;
    private String Admins_Point_check;

    private boolean Sudo_Enable;
    private String Admins_Sudo;

    private boolean EventJframe_Enable;
    private boolean PlayerMove_Enable;
    private String PlayerMove_Format;
    private int PlayerMove_CheckTime;
    private boolean PlayerBreakBlock_Enable;
    private String PlayerBreakBlock_Format;
    private boolean PlayerDeath_Enable;
    private String PlayerDeath_Format;

    private boolean PlayerGameTime_Enable;
    private String PlayerGameTime_Type;
    private List<String> PlayerGameTime_Command;
    private List<String> PlayerGameTime_Message;
    private HashMap<String, Integer> PlayerGameTime = new HashMap<>();

    //

    private String Message_PlayerQuitGroup;
    private String Message_KickPlayer;
    private String Message_PlayerOffline;
    private String Message_GroupConfirm;
    private String Message_GameConfirm;
    private String Message_GameConfirmFinish;
    private String Message_NoBind;
    private String Message_NoEnable;
    public final static double Version = 2.0;
    public final Runnable updata = () -> {
        try {
            Config conf = UpCheck.upCheckVersion();
            String version = conf.getString("NewVersion");
            if (UpCheck.isLatestVersion(conf)) {
                log("§4当前不是最新版本 " + "§a最新版本:§8§l" + version);
                for (String s : conf.getSection("Version." + version).getStringList("Updata")) {
                    log(s.replaceAll("%version%", "" + Version));
                }
            } else {
                log("§a§lb§r§e(§0●§e'§8◡§e'§0●§e)§a§ld§r§e当前为最新版本 " + version + " §a§lb§r§e(￣▽￣)§a§ld§r§8§l");
            }
        } catch (IllegalThreadStateException e) {
            log("§4检查更新失败！！！");
        }
    };

    EventJframe ejf;

    public void log(String log) {
        getServer().getConsoleSender().sendMessage("§a[§1M§2y§3K§4u§5Q§a]§r" + log);
    }

    private boolean CheckVersion_Enable;
    private int CheckVersion_Timer;

    private void timeUpCheck() {
        if (CheckVersion_Enable) {
            log("开始检测更新~");
            getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
                new Thread(this.updata).start();
            }, 0, CheckVersion_Timer);
        } else {
            log("关闭了自动检测更新~");
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        log("§a开始加载bStats");
        new bStats(this, 6812);
        log("§a开始加载EconomyApi!");
        loadEconomyAPI();
        saveDefaultConfig();
        saveResource("Message.yml");
        saveResource("data.yml");
        reload();
        log("§a开始加载玩家事件窗口");
        if (EventJframe_Enable) {
            ejf = new EventJframe();
            ejf.updataPlayer();
        }
        log("§a开始加载酷Q!");
        loadKuQ();
        timeUpCheck();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Confirm(this), this);
        log("§1	   __  ___     __ __     ____ ");
        log("§2	  /  |/  /_ __/ //_/_ __/ __ \\");
        log("§3	 / /|_/ / // / ,< / // / /_/ /");
        log("§4	/_/  /_/\\_, /_/|_|\\_,_/\\___\\_\\");
        log("§5	       /___/                  ");
        log("§6              §aVersion: "+getServer().getVersion()+"      ");
        log("§aMyKuQ 加载完成");
        log("§a如果您喜欢这个插件 请在Mcbbs帖子:https://www.mcbbs.net/thread-971000-1-1.html 下方给我评个分~");
        enablePlayerMove();
    }

    @Override
    public void onDisable() {
        savedata();
        log("MyKuQ 卸载完成");
    }

    @SuppressWarnings({"unchecked", "serial"})
    private void reload() {
        log("检查配置文件!");
        checkConfig();
        log("检查消息文件!");
        checkMessage();
        reloadConfig();
        this.MainQQGroupTip_Enable = this.getConfig().getBoolean("MainQQGroupTip.Enable");
        this.MainQQGroupTip_Message = this.getConfig().getStringList("MainQQGroupTip.Message");
        this.MainAdminQQGroup = this.getConfig().getLong("MainAdminQQGroup");
        this.MainAdminQQ = this.getConfig().getLong("MainAdminQQ");

        this.PlayerJoinMessage = this.getConfig().getString("PlayerJoinMessage.Message", "%player%加入了服务器");
        this.isPlayerJoinMessage = this.getConfig().getBoolean("PlayerJoinMessage.Enable", false);

        this.PlayerQuitMessage = this.getConfig().getString("PlayerQuitMessage.Message", "%player%退出了服务器");
        this.isPlayerQuitMessage = this.getConfig().getBoolean("PlayerQuitMessage.Enable", false);

        this.GameCheck = this.getConfig().getString("Check.GameCheck.Message", "群:");
        this.SendQQGroupMessage = this.getConfig().getString("Check.GameCheck.SendQQGroupMessage", "%player%>>>%chat%");
        this.Game_to_QQ_isRemoveColor = this.getConfig().getBoolean("Check.GameCheck.isRemoveColor", true);
        this.QQ_to_Game_SendSuccessTipEnable = this.getConfig().getBoolean("Check.GameCheck.Tip.Enable", true);
        this.QQ_to_Game_SendSuccessTip = this.getConfig().getString("Check.GameCheck.Tip.Message", "消息发送成功~");

        this.QQGroupCheck = this.getConfig().getString("Check.QQGroupCheck.Message", "服:");
        this.SendGameMessage = this.getConfig().getString("Check.QQGroupCheck.SendGameMessage",
                "[人工智障]%player%>>>%chat%");
        this.isIDorName = this.getConfig().getBoolean("Check.QQGroupCheck.isIDorName", true);
        this.QQ_to_Game_isRemoveColor = this.getConfig().getBoolean("Check.QQGroupCheck.isRemoveColor", true);
        this.Game_to_QQ_SendSuccessTipEnable = this.getConfig().getBoolean("Check.QQGroupCheck.Tip.Enable", true);
        this.Game_to_QQ_SendSuccessTip = this.getConfig().getString("Check.QQGroupCheck.Tip.Message", "消息发送成功~");

        this.ListCommands = this.getConfig().getString("ListCommands", "/list");
        this.ListMessage = this.getConfig().getString("ListMessage", "当前在线人数:%online_number%\n玩家列表:%online_player%");

        this.System_User = this.getConfig().getLong("System.User", 100000);
        this.System_Password = this.getConfig().getString("System.Password", "MyKuQYes");

        this.WhiteList_Enable = this.getConfig().getBoolean("WhiteList.Enable", true);
        this.WhiteList_Check = this.getConfig().getString("WhiteList.Check", "申请白名单:");
        this.WhiteList_Message = this.getConfig().getString("WhiteList.Message", "%player%成功申请了白名单~");

        this.PlayerBind_Enable = this.getConfig().getBoolean("PlayerBind.Enable", true);
        this.PlayerBind_Check = this.getConfig().getString("PlayerBind.Check", "绑定:");
        this.PlayerBind_Message = this.getConfig().getString("PlayerBind.Message", "绑定成功~");
        this.PlayerBind_ConfirmTime = this.getConfig().getInt("PlayerBind.ConfirmTime", 10);

        this.PlayerInfo_Enable = this.getConfig().getBoolean("PlayerInfo.Enable", true);
        this.PlayerInfo_Check = this.getConfig().getString("PlayerInfo.Check", "我的信息");
        this.PlayerInfo_ConfirmTime = this.getConfig().getInt("PlayerInfo.ConfirmTime", 20);
        this.PlayerInfo_Message = this.getConfig().getStringList("PlayerInfo.Message");

        this.PlayerSign_Enable = this.getConfig().getBoolean("PlayerSign.Enable", true);
        this.PlayerSign_Check = this.getConfig().getString("PlayerSign.Check", "签到");
        this.PlayerSign_Message = this.getConfig().getString("PlayerSign.Message");
        this.PlayerSign_AlreadySign = this.getConfig().getString("PlayerSign.AlreadySign");
        this.PlayerSing_SignReward = this.getConfig().getString("PlayerSign.SignReward");
        this.PlayerSign_ExChangeReward = new ArrayList<SignReward>() {
            {
                for (String s : getConfig().getSection("PlayerSign.ExChangeReward").getKeys(false)) {
                    add(new SignReward(s, getConfig().getInt("PlayerSign.ExChangeReward." + s + ".Points"),
                            getConfig().getStringList("PlayerSign.ExChangeReward." + s + ".Commands")));
                }
            }
        };
        this.PlayerSign_ExChangeRewardSetting_Title = this.getConfig()
                .getString("PlayerSign.ExChangeRewardSetting.Title");
        this.PlayerSign_ExChangeRewardSetting_PointsNotEnough = this.getConfig()
                .getString("PlayerSign.ExChangeRewardSetting.PointsNotEnough");
        this.PlayerSign_ExChangeRewardSetting_Success = this.getConfig()
                .getString("PlayerSign.ExChangeRewardSetting.Success");
        this.PlayerSign_ExChangeRewardSetting_Item = this.getConfig()
                .getString("PlayerSign.ExChangeRewardSetting.Item");
        this.PlayerSign_ExChangeRewardSetting_Confirm = this.getConfig()
                .getString("PlayerSign.ExChangeRewardSetting.Confirm");

        this.PlayerGameTime_Enable = this.getConfig().getBoolean("PlayerGameTime.Enable");
        this.PlayerGameTime_Type = this.getConfig().getString("PlayerGameTime.Type");
        this.PlayerGameTime_Command = this.getConfig().getStringList("PlayerGameTime.Command");
        this.PlayerGameTime_Message = this.getConfig().getStringList("PlayerGameTime.Message");

        Config data = new Config(new File(this.getDataFolder(), "data.yml"));
        this.Admin = data.getLongList("AdminQQ");
        this.QQGroup = data.getLongList("QQGroup");
        if (data.get("WhiteList") != null) {
            WhiteListPlayer.clear();
            WhiteListPlayer.putAll((Map<String, String>) data.get("WhiteList"));
        }
        if (data.get("KuPlayer") != null) {
            KuPlayer.clear();
            HashMap<String, String> kuplayer = (HashMap<String, String>) data.get("KuPlayer");
            for (Map.Entry<String, String> stringUUIDEntry : kuplayer.entrySet()) {
                KuPlayer.put(stringUUIDEntry.getKey(),
                        new KuPlayer(getServer().getOfflinePlayer(UUID.fromString(stringUUIDEntry.getValue())),
                                Long.valueOf(stringUUIDEntry.getKey())));
            }
        }
        if (data.get("PlayerSignData") != null) {
            PlayerSign_SignData = (HashMap<String, Long>) data.get("PlayerSignData");
        }
        if (data.get("PlayerSignTimer") != null) {
            PlayerSign_SignTimer = (HashMap<String, Integer>) data.get("PlayerSignTimer");
        }
        if (data.get("PlayerGameTime") != null) {
            PlayerGameTime = (HashMap<String, Integer>) data.get("PlayerGameTime");
        }
        if (data.get("PlayerPoints") != null) {
            PlayerPoints = (HashMap<String, Integer>) data.get("PlayerPoints");
        }

        this.Admins_White = this.getConfig().getString("AdminCommands.White.MainCommand");
        this.Admins_White_add = this.getConfig().getString("AdminCommands.White.Add");
        this.Admins_White_del = this.getConfig().getString("AdminCommands.White.Del");
        this.Admins_White_list = this.getConfig().getString("AdminCommands.White.List");

        this.Admins_Group = this.getConfig().getString("AdminCommands.Group.MainCommand");
        this.Admins_Group_add = this.getConfig().getString("AdminCommands.Group.Add");
        this.Admins_Group_del = this.getConfig().getString("AdminCommands.Group.Del");
        this.Admins_Group_list = this.getConfig().getString("AdminCommands.Group.List");

        this.Admins_Admin = this.getConfig().getString("AdminCommands.Admin.MainCommand");
        this.Admins_Admin_add = this.getConfig().getString("AdminCommands.Admin.Add");
        this.Admins_Admin_del = this.getConfig().getString("AdminCommands.Admin.Del");
        this.Admins_Admin_list = this.getConfig().getString("AdminCommands.Admin.List");

        this.Admins_Bind = this.getConfig().getString("AdminCommands.Bind.MainCommand");
        this.Admins_Bind_add = this.getConfig().getString("AdminCommands.Bind.Add");
        this.Admins_Bind_del = this.getConfig().getString("AdminCommands.Bind.Del");
        this.Admins_Bind_list = this.getConfig().getString("AdminCommands.Bind.List");

        this.Admins_Point = this.getConfig().getString("AdminCommands.Point.MainCommand");
        this.Admins_Point_add = this.getConfig().getString("AdminCommands.Point.Add");
        this.Admins_Point_del = this.getConfig().getString("AdminCommands.Point.Del");
        this.Admins_Point_check = this.getConfig().getString("AdminCommands.Point.Check");

        this.Sudo_Enable = this.getConfig().getBoolean("Sudo.Enable", true);
        this.Admins_Sudo = this.getConfig().getString("Sudo.MainCommand", "sudo:");

        this.EventJframe_Enable = this.getConfig().getBoolean("EventJframe.Enable", true);
        this.PlayerMove_Enable = this.getConfig().getBoolean("EventJframe.PlayerMove.Enable", true);
        this.PlayerMove_Format = this.getConfig().getString("EventJframe.PlayerMove.Format",
                "%player%从 X:%x% Y:%y% Z:%z% 移动至 X:%tx% Y:%ty% Z:%tz%");
        this.PlayerMove_CheckTime = this.getConfig().getInt("EventJframe.PlayerMove.CheckTime", 5);
        this.PlayerBreakBlock_Enable = this.getConfig().getBoolean("EventJframe.PlayerBreakBlock.Enable", true);
        this.PlayerBreakBlock_Format = this.getConfig().getString("EventJframe.PlayerBreakBlock.Format",
                "%player%打破了 X:%x% Y:%y% Z:%z% 的 %block%");
        this.PlayerDeath_Enable = this.getConfig().getBoolean("EventJframe.PlayerDeath.Enable", true);
        this.PlayerDeath_Format = this.getConfig().getString("EventJframe.PlayerDeath.Format",
                "%player%死亡在 X:%x% Y:%y% Z:%z%");

        this.CheckVersion_Enable = this.getConfig().getBoolean("CheckVersion.Enable");
        this.CheckVersion_Timer = this.getConfig().getInt("CheckVersion.Time");
        reloadMessage();
        loadPlayerGameTime();
    }

    private void reloadMessage() {
        Config mess = new Config(new File(this.getDataFolder(), "Message.yml"));
        Message_PlayerQuitGroup = mess.getString("PlayerQuitGroup");
        Message_KickPlayer = mess.getString("KickPlayer");
        Message_PlayerOffline = mess.getString("PlayerOffline");
        Message_GroupConfirm = mess.getString("GroupConfirm");
        Message_GameConfirm = mess.getString("GameConfirm");
        Message_GameConfirmFinish = mess.getString("GameConfirmFinish");
        Message_NoBind = mess.getString("NoBind");
        Message_NoEnable = mess.getString("NoEnable");
    }

    private Bot bot;

    private void loadKuQ() {

        new Thread(() -> {
            try {
                bot = BotFactory.INSTANCE.newBot(System_User, System_Password, new BotConfiguration() {{
                    // 配置，例如：
                    fileBasedDeviceInfo(new File(getDataFolder(), "/deviceInfo.json").getPath());
                }});

                bot.login();
                bot.getEventChannel().registerListenerHost(new KuQ(this));

                StringBuilder msg = new StringBuilder();
                MainQQGroupTip_Message.forEach((s) -> {
                    msg.append(s + "\n");
                });
                msg.setCharAt(msg.length() - 1, '\0');
                if (MainQQGroupTip_Enable) {
                    bot.getGroup(MainAdminQQGroup).sendMessage(msg.toString());
                }
                bot.getFriend(MainAdminQQ).sendMessage(msg.toString());
                bot.join();
            } catch (WrongPasswordException e) {
                e.printStackTrace();
                log("§4如果您是第一次使用本插件 请按照帖子 https://www.mcbbs.net/thread-971000-1-1.html 更改配置文件中System.User和System.Password为您机器人的QQ以及密码");
                log("§4如果您已经更改还报错请检查密码 如果确认密码正确无其他异常请 在帖子下方评论或者联系作者QQ");
            }catch (NoSuchElementException e){
                e.printStackTrace();
                log("§4如果您是第一次使用本插件 请按照帖子 https://www.mcbbs.net/thread-971000-1-1.html 更改配置文件中MainAdminQQGroup和MainAdminQQ");
                log("§4如果您已经更改还报错请在帖子下方评论或者联系作者QQ");
            }
        }).start();


    }

    private void savedata() {
        File file = new File(this.getDataFolder(), "data.yml");
        Config data = new Config(file);
        data.set("AdminQQ", Admin);
        data.set("QQGroup", QQGroup);
        data.set("WhiteList", WhiteListPlayer);
        HashMap<String, String> kuplayer = new HashMap<>();
        for (Map.Entry<String, KuPlayer> stringKuPlayerEntry : KuPlayer.entrySet()) {
            kuplayer.put(stringKuPlayerEntry.getKey(),
                    stringKuPlayerEntry.getValue().getOfflinePlayer().getUniqueId().toString());
        }
        data.set("KuPlayer", kuplayer);
        data.set("PlayerSignData", PlayerSign_SignData);
        data.set("PlayerSignTimer", PlayerSign_SignTimer);
        data.set("PlayerGameTime", PlayerGameTime);
        data.set("PlayerPoints", PlayerPoints);
        data.save(file);
    }

    private void checkConfig() {
        int c = 0;
        Config a = new Config();
        a.load(this.getResource("config.yml"));
        Set<String> keys = this.getConfig().getKeys(true);
        for (String b : a.getKeys(true)) {
            if (b.contains("ExChangeReward")) {
                continue;
            }
            if (!keys.contains(b)) {
                c++;
                this.getConfig().set(b, a.get(b));
            }
        }
        log("共更新了" + c + "个插件配置");
        saveConfig();
    }

    private void checkMessage() {
        int c = 0;
        Config a = new Config();
        a.load(this.getResource("Message.yml"));
        Config mess = new Config(new File(this.getDataFolder(), "Message.yml"));
        Set<String> keys = mess.getKeys(true);
        for (String b : a.getKeys(true)) {
            if (!keys.contains(b)) {
                c++;
                mess.set(b, a.get(b));
            }
        }
        log("共更新了" + c + "个消息配置");
        mess.save();
    }

    private void loadEconomyAPI() {
        try {
            if (getServer().getPluginManager().getPlugin("EconomyAPI") != null) {
                log("§aEconomyAPI已安装！");
                economy = EconomyAPI.getInstance();
            } else {
                log("§4您未安装EconomyAPI!");
            }
        } catch (java.lang.NullPointerException e) {
        }
    }

    private void loadPlayerGameTime() {
        if (PlayerGameTime_Enable) {
            int a = 0;
            String pmtype = PlayerGameTime_Type;
            if (pmtype.equalsIgnoreCase("D")) {
                a = 20 * 60 * 60 * 24;
            } else if (pmtype.equalsIgnoreCase("H")) {
                a = 20 * 60 * 60;
            } else if (pmtype.equalsIgnoreCase("M")) {
                a = 20 * 60;
            } else if (pmtype.equalsIgnoreCase("S")) {
                a = 20;
            }
            if (a != 0) {
                Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
                    for (Map.Entry<UUID, Player> p : getServer().getOnlinePlayers().entrySet()) {
                        PlayerGameTime.put(p.getValue().getName(),
                                (PlayerGameTime.get(p.getValue().getName()) != null
                                        ? (PlayerGameTime.get(p.getValue().getName()) + 1)
                                        : 0));
                    }
                }, 0, a);
            } else {
                log("§c在线奖励关闭 请检查插件配置文件中在线时间Type");
            }
        }
    }

    @SuppressWarnings("serial")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (!sender.hasPermission("mykuq.use")) {
                    sender.sendMessage("§c您没得权限!");
                    return true;
                }
                reload();
                loadKuQ();
                savedata();
                return true;
            } else if (args[0].equals("exchange")) {
                if (PlayerSign_Enable) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§c这个指令只能玩家使用");
                        return true;
                    }
                    Player p = (Player) sender;
                    int points = PlayerPoints.get(p.getName()) == null ? 0 : PlayerPoints.get(p.getName());
                    ExChangeGui exChangeGui = new ExChangeGui(
                            PlayerSign_ExChangeRewardSetting_Title.replaceAll("%points%", "" + points),
                            new ArrayList<Element>() {
                                {
                                    for (SignReward s : PlayerSign_ExChangeReward) {
                                        add(new ElementLabel(
                                                PlayerSign_ExChangeRewardSetting_Item.replaceAll("%item%", s.getName())
                                                        .replaceAll("%points%", "" + s.getPoint())));
                                        add(new ElementToggle(PlayerSign_ExChangeRewardSetting_Confirm, false));
                                    }
                                }
                            });
                    p.showFormWindow(exChangeGui);
                } else {
                    sender.sendMessage(Message_NoEnable);
                }
                return true;
            } else if ("gametime".equals(args[0])) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c这个指令只能玩家使用");
                    return true;
                }
                Player p = (Player) sender;
                p.sendMessage("&a您当前剩余可兑换在线时间为:&e&l%time%".replaceAll("%time%",
                        PlayerGameTime.get(p.getName()) != null ? "" + PlayerGameTime.get(p.getName()) : "" + 0));
                return true;
            }
            return false;
        } else if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c这个指令只能玩家使用");
                return true;
            }
            Player p = (Player) sender;
            int s = PlayerGameTime.get(p.getName()) != null ? PlayerGameTime.get(p.getName()) : 0;
            if (args[0].equals("gametime" + "") && args[1] != null) {
                int ss;
                try {
                    ss = Integer.valueOf(args[1]);
                    if (ss > s || ss < 0) {
                        p.sendMessage(
                                "&c输入错误,&a您当前剩余可兑换在线时间为:&e&l%time%".replaceAll("%time%", s + PlayerGameTime_Type));
                        return true;
                    }
                } catch (Exception e1) {
                    p.sendMessage("&4使用错误 &a格式:\n/mykuq gametime &e查询剩余可兑换在线时间~\n/mykuq gametime <兑换的在线时间>&e兑换在线时间奖励~");
                    return true;
                }
                int cha = s - ss;
                PlayerGameTime.put(p.getName(), cha);
                List<String> listcommand = PlayerGameTime_Command;
                List<String> listmessage = PlayerGameTime_Message;
                for (String cd : listcommand) {
                    cd = cd.replaceAll("%player%", p.getName());
                    cd = cd.replaceAll("%pmdh%", String.valueOf(ss));
                    cd = cd.replaceAll("%pmsy%", String.valueOf(cha));
                    try {
                        String sourceArray = cd.substring(cd.indexOf("<") + 1, cd.indexOf(">"));
                        int returns = 0;
                        try {
                            returns = (int) new ScriptEngineManager().getEngineByName("JavaScript").eval(sourceArray);
                        } catch (NullPointerException e) {
                            returns = Integer.valueOf(sourceArray);
                        }
                        sourceArray = "<" + sourceArray + ">";
                        cd = cd.replace(sourceArray, String.valueOf(returns));
                        getServer().dispatchCommand(getServer().getConsoleSender(), cd);
                    } catch (ScriptException e) {
                        p.sendMessage("§4请检查配置中表达式是否正确");
                    }
                }
                for (String mg : listmessage) {
                    mg = mg.replaceAll("%player%", p.getName());
                    mg = mg.replaceAll("%pmdh%", String.valueOf(ss));
                    mg = mg.replaceAll("%pmsy%", String.valueOf(cha));
                    p.sendMessage(mg);
                }

            } else {
                p.sendMessage("&4使用错误 &a格式:\n/mykuq gametime &e查询剩余可兑换在线时间~\n/mykuq gametime <兑换的在线时间>&e兑换在线时间奖励~");

            }
        } else if (args.length == 3) {
            if (args[0].equals("sendQQ") && args[1] != null && args[2] != null) {
                if (!sender.hasPermission("mykuq.use")) {
                    sender.sendMessage("§c您没得权限!");
                    return true;
                }
                for (Friend g : bot.getFriends()) {
                    if (String.valueOf(g.getId()).equals(args[1])) {
                        g.sendMessage(args[2]);
                        return true;
                    }
                }
                sender.sendMessage("§c未找到该QQ!");
                return true;
            }
            if (args[0].equals("sendGroup") && args[1] != null && args[2] != null) {
                if (!sender.hasPermission("mykuq.use")) {
                    sender.sendMessage("§c您没得权限!");
                    return true;
                }
                for (Group g : bot.getGroups()) {
                    if (String.valueOf(g.getId()).equals(args[1])) {
                        g.sendMessage(args[2]);
                        return true;
                    }
                }
                sender.sendMessage("§c未找到该群聊!");
                return true;
            }
            return false;
        }
        return false;
    }

    @EventHandler
    public void clickGui(PlayerFormRespondedEvent event) {
        if (event.getWindow() instanceof Gui) {
            JSONObject j1 = new JSONObject(event.getWindow().getJSONData());
            if (!j1.getBoolean("closed")) {
                JSONObject j2 = j1.getJSONObject("response");
                JSONObject j3 = j2.getJSONObject("toggleResponses");
                int c = 1;
                for (SignReward s : PlayerSign_ExChangeReward) {
                    if (j3.getBoolean(String.valueOf(c))) {
                        int points = PlayerPoints.get(event.getPlayer().getName()) == null ? 0
                                : PlayerPoints.get(event.getPlayer().getName());
                        if (points >= s.getPoint()) {
                            PlayerPoints.put(event.getPlayer().getName(), points - s.getPoint());
                            WxysUtil.executionCommands(s.getCommands(), event.getPlayer());
                            event.getPlayer().sendMessage(PlayerSign_ExChangeRewardSetting_Success
                                    .replaceAll("%item%", s.getName()).replaceAll("%points%", "" + s.getPoint()));
                        } else {
                            event.getPlayer().sendMessage(PlayerSign_ExChangeRewardSetting_PointsNotEnough);
                        }
                    }
                    c += 2;
                }
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (EventJframe_Enable) {
            ejf.updataPlayer();
        }
        if (isPlayerJoinMessage) {
            bot.getGroup(MainAdminQQGroup)
                    .sendMessage(PlayerJoinMessage.replaceAll("%player%", e.getPlayer().getName()));
            for (long lon : QQGroup) {
                bot.getGroup(lon).sendMessage(PlayerJoinMessage.replaceAll("%player%", e.getPlayer().getName()));
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        if (EventJframe_Enable) {
            ejf.updataPlayer();
        }
        if (isPlayerQuitMessage) {
            bot.getGroup(MainAdminQQGroup)
                    .sendMessage(PlayerQuitMessage.replaceAll("%player%", e.getPlayer().getName()));
            for (long lon : QQGroup) {
                bot.getGroup(lon).sendMessage(PlayerQuitMessage.replaceAll("%player%", e.getPlayer().getName()));
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        if (EventJframe_Enable) {
            if (PlayerBreakBlock_Enable) {
                ejf.updataEventArea(PlayerBreakBlock_Format.replaceAll("%player%", e.getPlayer().getName())
                        .replaceAll("%x%", e.getBlock().getLocation().getFloorX() + "")
                        .replaceAll("%y%", e.getBlock().getLocation().getFloorY() + "")
                        .replaceAll("%z%", e.getBlock().getLocation().getFloorZ() + "")
                        .replaceAll("%block%", e.getBlock().getName()));
            }
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        if (EventJframe_Enable) {
            if (PlayerDeath_Enable) {
                ejf.updataEventArea(PlayerDeath_Format.replaceAll("%player%", e.getEntity().getName())
                        .replaceAll("%x%", e.getEntity().getLocation().getFloorX() + "")
                        .replaceAll("%y%", e.getEntity().getLocation().getFloorY() + "")
                        .replaceAll("%z%", e.getEntity().getLocation().getFloorZ() + ""));
            }
        }
    }

    HashMap<Player, Location> playersLocation = new HashMap<>();

    void enablePlayerMove() {
        if (EventJframe_Enable) {
            if (PlayerMove_Enable) {
                getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
                    for (Map.Entry<UUID, Player> e : getServer().getOnlinePlayers().entrySet()) {
                        Player p = e.getValue();
                        Location location = playersLocation.get(p);
                        if (location != null) {
                            if (location.getX() != p.getLocation().getX() && location.getY() != p.getLocation().getY()
                                    && location.getZ() != p.getLocation().getZ()) {
                                ejf.updataEventArea(PlayerMove_Format.replaceAll("%player%", p.getName())
                                        .replaceAll("%x%", location.getFloorX() + "")
                                        .replaceAll("%y%", location.getFloorY() + "")
                                        .replaceAll("%z%", location.getFloorZ() + "")
                                        .replaceAll("%tx%", p.getLocation().getFloorX() + "")
                                        .replaceAll("%ty%", p.getLocation().getFloorY() + "")
                                        .replaceAll("%tz%", p.getLocation().getFloorZ() + ""));
                            }
                        }
                        playersLocation.put(p, p.getLocation());
                    }
                }, PlayerMove_CheckTime * 20, PlayerMove_CheckTime * 20);

            }
        }
    }

    @EventHandler
    public void chat(PlayerChatEvent e) {
        int i = e.getMessage().indexOf(GameCheck);
        if (getGameCheck().equals("")) {
            String s = SendQQGroupMessage.replaceAll("%player%", e.getPlayer().getName()).replaceAll("%chat%",
                    e.getMessage());
            bot.getGroup(MainAdminQQGroup).sendMessage(s);
            for (long lon : QQGroup) {
                bot.getGroup(lon).sendMessage(s);
            }
            if (isGame_to_QQ_SendSuccessTipEnable()) {
                e.getPlayer().sendMessage(getGame_to_QQ_SendSuccessTip());
            }
        } else if (i != -1) {
            String s = SendQQGroupMessage.replaceAll("%player%", e.getPlayer().getName()).replaceAll("%chat%",
                    e.getMessage().substring(i + GameCheck.length(), e.getMessage().length()));
            if (Game_to_QQ_isRemoveColor) {
                if (s.contains("&")) {
                    s = s.replaceAll("&", "");
                }
            } else {
                s = s.replaceAll("&", "§");
            }
            bot.getGroup(MainAdminQQGroup).sendMessage(s);
            for (long lon : QQGroup) {
                bot.getGroup(lon).sendMessage(s);
            }
            if (isGame_to_QQ_SendSuccessTipEnable()) {
                e.getPlayer().sendMessage(getGame_to_QQ_SendSuccessTip());
            }
        }
    }

    public int getPlayerBind_ConfirmTime() {
        return PlayerBind_ConfirmTime;
    }

    public int getPlayerInfo_ConfirmTime() {
        return PlayerInfo_ConfirmTime;
    }

    public HashMap<String, Long> getPlayerSign_SignData() {
        return PlayerSign_SignData;
    }

    public String getPlayerSign_ExChangeRewardSetting_Title() {
        return PlayerSign_ExChangeRewardSetting_Title;
    }

    public String getPlayerSign_ExChangeRewardSetting_PointsNotEnough() {
        return PlayerSign_ExChangeRewardSetting_PointsNotEnough;
    }

    public String getPlayerSign_ExChangeRewardSetting_Success() {
        return PlayerSign_ExChangeRewardSetting_Success;
    }

    public String getPlayerSign_ExChangeRewardSetting_Item() {
        return PlayerSign_ExChangeRewardSetting_Item;
    }

    public HashMap<String, Integer> getPlayerSign_SignTimer() {
        return PlayerSign_SignTimer;
    }

    public String getAdmins_Point() {
        return Admins_Point;
    }

    public String getAdmins_Point_add() {
        return Admins_Point_add;
    }

    public String getAdmins_Point_del() {
        return Admins_Point_del;
    }

    public String getAdmins_Point_check() {
        return Admins_Point_check;
    }

    public String getPlayerSign_ExChangeRewardSetting_Confirm() {
        return PlayerSign_ExChangeRewardSetting_Confirm;
    }

    public boolean isMainQQGroupTip_Enable() {
        return MainQQGroupTip_Enable;
    }

    public List<String> getMainQQGroupTip_Message() {
        return MainQQGroupTip_Message;
    }

    public List<SignReward> getPlayerSign_ExChangeReward() {
        return PlayerSign_ExChangeReward;
    }

    public String getPlayerSing_SignReward() {
        return PlayerSing_SignReward;
    }

    public HashMap<String, Integer> getPlayerPoints() {
        return PlayerPoints;
    }

    public String getMessage_NoEnable() {
        return Message_NoEnable;
    }

    public static double getVersion() {
        return Version;
    }

    public Runnable getUpdata() {
        return updata;
    }

    public boolean isCheckVersion_Enable() {
        return CheckVersion_Enable;
    }

    public int getCheckVersion_Timer() {
        return CheckVersion_Timer;
    }

    public boolean isMainQQGroupTipEnable() {
        return MainQQGroupTip_Enable;
    }

    public boolean isPlayerSign_Enable() {
        return PlayerSign_Enable;
    }

    public String getPlayerSign_Check() {
        return PlayerSign_Check;
    }

    public String getPlayerSign_Message() {
        return PlayerSign_Message;
    }

    public String getMessage_PlayerQuitGroup() {
        return Message_PlayerQuitGroup;
    }

    public String getMessage_KickPlayer() {
        return Message_KickPlayer;
    }

    public String getMessage_PlayerOffline() {
        return Message_PlayerOffline;
    }

    public String getMessage_GroupConfirm() {
        return Message_GroupConfirm;
    }

    public String getMessage_GameConfirm() {
        return Message_GameConfirm;
    }

    public String getMessage_GameConfirmFinish() {
        return Message_GameConfirmFinish;
    }

    public String getMessage_NoBind() {
        return Message_NoBind;
    }

    public EconomyAPI getEconomy() {
        return economy;
    }

    public boolean isPlayerBind_Enable() {
        return PlayerBind_Enable;
    }

    public String getPlayerSign_AlreadySign() {
        return PlayerSign_AlreadySign;
    }

    public String getPlayerBind_Check() {
        return PlayerBind_Check;
    }

    public String getPlayerBind_Message() {
        return PlayerBind_Message;
    }

    public boolean isPlayerInfo_Enable() {
        return PlayerInfo_Enable;
    }

    public String getPlayerInfo_Check() {
        return PlayerInfo_Check;
    }

    public List<String> getPlayerInfo_Message() {
        return PlayerInfo_Message;
    }

    public HashMap<String, KuPlayer> getKuPlayer() {
        return KuPlayer;
    }

    public String getAdmins_Bind() {
        return Admins_Bind;
    }

    public String getAdmins_Bind_add() {
        return Admins_Bind_add;
    }

    public String getAdmins_Bind_del() {
        return Admins_Bind_del;
    }

    public String getAdmins_Bind_list() {
        return Admins_Bind_list;
    }

    public String getAdmins_White() {
        return Admins_White;
    }

    public String getAdmins_White_add() {
        return Admins_White_add;
    }

    public String getAdmins_White_del() {
        return Admins_White_del;
    }

    public String getAdmins_White_list() {
        return Admins_White_list;
    }

    public String getAdmins_Group() {
        return Admins_Group;
    }

    public String getAdmins_Group_add() {
        return Admins_Group_add;
    }

    public String getAdmins_Group_del() {
        return Admins_Group_del;
    }

    public String getAdmins_Group_list() {
        return Admins_Group_list;
    }

    public String getAdmins_Admin() {
        return Admins_Admin;
    }

    public String getAdmins_Admin_add() {
        return Admins_Admin_add;
    }

    public String getAdmins_Admin_del() {
        return Admins_Admin_del;
    }

    public String getAdmins_Admin_list() {
        return Admins_Admin_list;
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

    public HashMap<String, String> getWhiteListPlayer() {
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

    public boolean isPlayerGameTime_Enable() {
        return PlayerGameTime_Enable;
    }

    public String getPlayerGameTime_Type() {
        return PlayerGameTime_Type;
    }

    public List<String> getPlayerGameTime_Command() {
        return PlayerGameTime_Command;
    }

    public List<String> getPlayerGameTime_Message() {
        return PlayerGameTime_Message;
    }

    public HashMap<String, Integer> getPlayerGameTime() {
        return PlayerGameTime;
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

    public List<Long> getAdmin() {
        return Admin;
    }

    public List<Long> getQQGroup() {
        return QQGroup;
    }

    public Bot getBot() {
        return bot;
    }

    public boolean isSudo_Enable() {
        return Sudo_Enable;
    }

    public String getAdmins_Sudo() {
        return Admins_Sudo;
    }
}