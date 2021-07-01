package top.wcpe.wcbot.window;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Location;
import cn.nukkit.utils.ConfigSection;
import top.wcpe.wcbot.WCBot;
import top.wcpe.wcpelib.common.utils.string.StringUtil;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author WCPE
 */
public class EventWindow implements Listener {
    private DefaultListModel<String> players = new DefaultListModel<>();

    private JList<String> onlinePlayerList = new JList<>(players);

    private JTextArea eventInfoLogs = new JTextArea();
    private JScrollPane jScrollPaneInfo = new JScrollPane(eventInfoLogs);
    private JCheckBox updateButton = new JCheckBox("实时更新");

    public EventWindow() {
        Server.getInstance().getPluginManager().registerEvents(this, WCBot.getInstance());
        EventQueue.invokeLater(() -> {
            try {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        JFrame mainWindow = new JFrame("事件窗口");
        mainWindow.setSize(748, 740);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setResizable(false);
        mainWindow.setLayout(null);

        eventInfoLogs.setText("");
        eventInfoLogs.setLineWrap(true);
        eventInfoLogs.setWrapStyleWord(true);
        eventInfoLogs.setEditable(false);

        jScrollPaneInfo.setBounds(20, 20, 400, 672);
        mainWindow.add(jScrollPaneInfo);

        JScrollPane onlinePlayer = new JScrollPane(onlinePlayerList);
        onlinePlayer.setBounds(420, 20, 300, 500);
        onlinePlayerList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 鼠标点击（按下并抬起）
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // 鼠标按下
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (onlinePlayerList.getSelectedIndex() != -1) {
                    if (e.isMetaDown()) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem kickPlayerItem = new JMenuItem("踢出玩家");
                        kickPlayerItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Player playerExact = Server.getInstance().getPlayerExact(players.getElementAt(onlinePlayerList.getSelectedIndex()));
                                if (playerExact == null)
                                    return;
                                playerExact.kick();
                                updatePlayer();
                                putEventLog(WCBot.getMessageManager().getMessage("eventWindowKickPlayer", "player:" + playerExact.getName()));
                            }
                        });

                        popupMenu.add(kickPlayerItem);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // 鼠标进入组件区域
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 鼠标离开组件区域
            }
        });
        mainWindow.add(onlinePlayer);

        JButton clearEventInfoLogsButton = new JButton("清屏");
        clearEventInfoLogsButton.setBounds(445, 545, 64, 24);
        clearEventInfoLogsButton.addActionListener((s) -> {
            eventInfoLogs.setText("");
        });
        mainWindow.add(clearEventInfoLogsButton);

        updateButton.setBounds(529, 545, 125, 24);
        mainWindow.add(updateButton);

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
        playerMoveTask();
    }


    public synchronized void updatePlayer() {
        players.removeAllElements();
        for (Map.Entry<UUID, Player> uuidPlayerEntry : Server.getInstance().getOnlinePlayers().entrySet()) {
            players.addElement(uuidPlayerEntry.getValue().getName());
        }
    }

    public synchronized void putEventLog(String msg) {
        if (msg == null) {
            return;
        }
        msg = "[" + LocalDateTime.now() + "] " + msg + System.lineSeparator();
        eventInfoLogs.append(msg);
        if (updateButton.isSelected()) {
            eventInfoLogs.setSelectionStart(eventInfoLogs.getText().length());
        } else {
            DefaultCaret caret = (DefaultCaret) eventInfoLogs.getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }

        Path eventWindowLogsDirPath = WCBot.getInstance().getDataFolder().toPath().resolve("eventWindowLogs");
        if (Files.notExists(eventWindowLogsDirPath)) {
            try {
                Files.createDirectory(eventWindowLogsDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path logFilePath = eventWindowLogsDirPath.resolve(LocalDate.now().toString() + ".logs");
        if (Files.notExists(logFilePath)) {
            try {
                Files.createFile(logFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.write(logFilePath, msg.getBytes(Charset.forName("utf-8")), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @EventHandler
    public void playerJoinServer(PlayerJoinEvent e) {
        updatePlayer();
    }

    @EventHandler
    public void playerJoinServer(PlayerQuitEvent e) {
        updatePlayer();
    }

    private final HashMap<Player, Location> onlinePlayerMoveLocationMap = new HashMap<>();

    void playerMoveTask() {
        ConfigSection cfg = WCBot.getInstance().getConfig().getSection("EventWindow.playerMoveEvent");
        if (!cfg.getBoolean("enable")) return;
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(WCBot.getInstance(), () -> {
            for (Map.Entry<UUID, Player> e : Server.getInstance().getOnlinePlayers().entrySet()) {
                Player p = e.getValue();
                Location location = onlinePlayerMoveLocationMap.get(p);
                if (location == null) {
                    onlinePlayerMoveLocationMap.put(p, p.getLocation());
                    continue;
                }
                if (location.getX() != p.getLocation().getX() && location.getY() != p.getLocation().getY()
                        && location.getZ() != p.getLocation().getZ()) {
                    putEventLog(StringUtil.replaceString(cfg.getString("format"), "player:" + p.getName(), "x:" + location.getFloorX(), "y:" + location.getFloorY(), "z:" + location.getFloorZ(), "tx:" + p.getLocation().getFloorX(), "ty:" + p.getLocation().getFloorY(), "tz:" + p.getLocation().getFloorZ()));
                    onlinePlayerMoveLocationMap.put(p, p.getLocation());
                }
            }
        }, cfg.getInt("checkTime") * 20, cfg.getInt("checkTime") * 20);


    }


    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        ConfigSection cfg = WCBot.getInstance().getConfig().getSection("EventWindow.playerBreakBlockEvent");
        if (!cfg.getBoolean("enable")) return;
        putEventLog(StringUtil.replaceString(cfg.getString("format"), "player:" + e.getPlayer().getName(), "x:" + e.getBlock().getLocation().getFloorX(), "y:" + e.getBlock().getLocation().getFloorY(), "z:" + e.getBlock().getLocation().getFloorZ(), "block:" + e.getBlock().getName()));
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        ConfigSection cfg = WCBot.getInstance().getConfig().getSection("EventWindow.playerDeathEvent");
        if (!cfg.getBoolean("enable")) return;
        putEventLog(StringUtil.replaceString(cfg.getString("format"), "player:" + e.getEntity().getName(), "x:" + e.getEntity().getLocation().getFloorX(), "y:" + e.getEntity().getLocation().getFloorY(), "z:" + e.getEntity().getLocation().getFloorZ()));
    }


}
