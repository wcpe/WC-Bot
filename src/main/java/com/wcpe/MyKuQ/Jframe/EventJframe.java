package com.wcpe.MyKuQ.Jframe;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.wcpe.MyKuQ.Utils.WxysUtil;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author WCPE
 */
public class EventJframe {
    private DefaultListModel<String> players = new DefaultListModel<String>();
    private JList<String> playerList = new JList<>(players);

    private JTextArea jTextAreaInfo = new JTextArea();
    private JScrollPane jScrollPaneInfo;
    private JCheckBox checkbox = new JCheckBox("实时更新");

    public EventJframe() {
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

        JFrame jf = new JFrame("玩家事件");

        jf.setTitle("玩家事件");
        jf.setSize(748, 740);
        jf.setLocationRelativeTo(null);
        jf.setResizable(false);
        jf.setLayout(null);
        jTextAreaInfo.setText("");
        jTextAreaInfo.setLineWrap(true); // 设置自动换行
        jTextAreaInfo.setWrapStyleWord(true);
        jTextAreaInfo.setEditable(false);
        jScrollPaneInfo = new JScrollPane(jTextAreaInfo);
        jScrollPaneInfo.setBounds(20, 20, 400, 672);
        jf.add(jScrollPaneInfo);
        JScrollPane playersList = new JScrollPane(playerList);
        playersList.setBounds(420, 20, 300, 500);

        playerList.addMouseListener(new MouseListener() {
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
                if (playerList.getSelectedIndex() != -1) {
                    if (e.isMetaDown()) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY());
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
        jf.add(playersList);
        JButton button = new JButton("清屏");
        button.setBounds(445, 545, 64, 24);
        button.addActionListener((s) -> {
            jTextAreaInfo.setText("");
        });
        jf.add(button);
        checkbox.setBounds(529, 545, 125, 24);
        jf.add(checkbox);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    void showPopupMenu(Component invoker, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem("踢出玩家");
        copyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Server.getInstance().getPlayer(players.getElementAt(playerList.getSelectedIndex())).kick();
                updataPlayer();
            }
        });
        popupMenu.add(copyMenuItem);
        popupMenu.show(invoker, x, y);
    }

    public synchronized void updataPlayer() {
        players.removeAllElements();
        for (Map.Entry<UUID, Player> uuidPlayerEntry : Server.getInstance().getOnlinePlayers().entrySet()) {
            players.addElement(uuidPlayerEntry.getValue().getName());
        }
    }
    public synchronized void updataPlayer(List<String> s) {
        players.removeAllElements();
        for (String ss : s) {
            players.addElement(ss);
        }
    }

    public synchronized void updataEventArea(String s) {
        jTextAreaInfo.append("["+ WxysUtil.toTime(System.currentTimeMillis())+"] "+s + "\n\n");
        if(checkbox.isSelected()){
            jTextAreaInfo.setSelectionStart(jTextAreaInfo.getText().length());
        }else{
            DefaultCaret caret = (DefaultCaret)jTextAreaInfo.getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }
}
