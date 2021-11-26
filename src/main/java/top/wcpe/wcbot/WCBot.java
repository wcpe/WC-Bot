package top.wcpe.wcbot;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import top.wcpe.wcbot.botcommand.command.*;
import top.wcpe.wcbot.inter.DataManager;
import top.wcpe.wcbot.inter.impl.DataManagerFileDataImpl;
import top.wcpe.wcbot.window.EventWindow;
import top.wcpe.wcpelib.nukkit.command.CommandPlus;
import top.wcpe.wcpelib.nukkit.command.entity.Command;
import top.wcpe.wcpelib.nukkit.command.entity.CommandArgument;
import top.wcpe.wcpelib.nukkit.manager.MessageManager;

/**
 * 插件主类
 *
 * @author: WCPE 1837019522@qq.com
 * @create: 2021-06-22 14:09
 */
public class WCBot extends PluginBase {


    @Getter
    private static WCBot instance;

    @Getter
    private static MessageManager messageManager;
    @Getter
    private static DataManager dataManager;
    @Getter
    private static BotManager botManager;

    @Getter
    private static WcBotBasicFunctions wcBotBasicFunctions;


    public ConfigSection getSetting() {
        return getConfig().getSection("Setting");
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        saveDefaultConfig();
        messageManager = new MessageManager(this, "CN");

        dataManager = new DataManagerFileDataImpl();
        botManager = new BotManager();

        wcBotBasicFunctions = new WcBotBasicFunctions();

        getServer().getPluginManager().registerEvents(wcBotBasicFunctions, this);

        CommandPlus cp = new CommandPlus.Builder("WC-Bot", this).aliases("wb").build();
        cp.registerSubCommand(new Command.Builder("sendGroup", "§a发送消息至QQ群").executeComponent((sender, args) -> {
            Long qqGroupId = null;
            try {
                qqGroupId = Long.parseLong(args[0]);
            } catch (Exception e) {
                sender.sendMessage("请输入正确的QQ群号码!");
                return;
            }
            Group group = getBotManager().getDefaultBot().getGroup(qqGroupId);
            if (group == null) {
                sender.sendMessage("请输入正确的QQ群号码!");
                return;
            }
            group.sendMessage(args[1]);
            sender.sendMessage("发送成功!");
        }).args(new CommandArgument.Builder("QQ群号").build(), new CommandArgument.Builder("消息").build()).build());
        cp.registerSubCommand(new Command.Builder("sendQQ", "§a发送消息至QQ").executeComponent((sender, args) -> {
            Long qqFirendId = null;
            try {
                qqFirendId = Long.parseLong(args[0]);
            } catch (Exception e) {
                sender.sendMessage("请输入正确的QQ号码!");
                return;
            }
            Friend friend = getBotManager().getDefaultBot().getFriend(qqFirendId);
            if (friend == null) {
                sender.sendMessage("请输入正确的QQ号码!");
                return;
            }
            friend.sendMessage(args[1]);
            sender.sendMessage("发送成功!");
        }).args(new CommandArgument.Builder("QQ号").build(), new CommandArgument.Builder("消息").build()).build());


        cp.registerThis();
        new AdminManagerCommand().registerCommand();
        new BindManagerCommand().registerCommand();
        new BotPointManagerCommand().registerCommand();
        new EnableGroupManagerCommand().registerCommand();
        new ListCommand().registerCommand();
        new PlayerBindCommand().registerCommand();
        new PlayerGameTimeCommand().registerCommand();
        new PlayerInfoCommand().registerCommand();
        new PlayerSignCommand().registerCommand();
        new SudoCommand().registerCommand();
        new WhiteListCommand().registerCommand();
        new WhiteListManagerCommand().registerCommand();

        getLogger().info("load time: " + (System.currentTimeMillis() - start) + " ms");
        getServer().getConsoleSender().sendMessage("§1  _       __   ______           ____           __ ");
        getServer().getConsoleSender().sendMessage("§2 | |     / /  / ____/          / __ )  ____   / /_");
        getServer().getConsoleSender().sendMessage("§3 | | /| / /  / /      ______  / __  | / __ \\ / __/");
        getServer().getConsoleSender().sendMessage("§4 | |/ |/ /  / /___   /_____/ / /_/ / / /_/ // /_  ");
        getServer().getConsoleSender().sendMessage("§5 |__/|__/   \\____/          /_____/  \\____/ \\__/  ");
        getServer().getConsoleSender().sendMessage("§6                                                  ");
        getServer().getConsoleSender().sendMessage("§6              §aServerVersion: " + getServer().getVersion() + "      ");
        getServer().getConsoleSender().sendMessage("§6              §aPluginVersion: 3.0.0      ");
        getServer().getConsoleSender().sendMessage("§a如果您喜欢这个插件 请在 Mcbbs 帖子: https://www.mcbbs.net/thread-971000-1-1.html 下方给我评个分~");
        new bStats(this, 6812);
        getLogger().info("onEnable...");

        if (getConfig().getBoolean("EventWindow.enable"))
            new EventWindow();
    }


    @Override
    public void onDisable() {
        getLogger().info("onDisable...");
    }
}
