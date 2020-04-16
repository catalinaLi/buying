package com.crawler.xiaomi.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.util.Enumeration;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 17:13 2020/4/9
 * @Modefied by:
 */
public class XiaoMiView extends AbstractXiaoMiFunction{

    private JFrame jframe;//窗体面板
    private JPanel panel;//内容面板
    private JLabel name;//商品名称/链接
    private JTextField nameText;//商品链接
    private JButton parseButton;//搜索按钮
    private JLabel optionLable;//自定义选项
    private JComboBox<String> option1;//下拉框1版本
    private JComboBox<String> option2;//下拉框2颜色
    private JLabel buyTime;//抢购时间
    private JTextField buyTimeText;//抢购时间框
    private JLabel duration;//抢购时长
    private JTextField durationText;//抢购时长框
    private JLabel username;//账号
    private JTextField userText;//账号框
    private JLabel password;//密码
    private JTextField passwordText;//密码框
    private JButton hideButton;//显示日志按钮
    private JButton startButton;//开始按钮
    private JButton pauseButton;//停止按钮
    private JButton quitButton;//退出按钮

    public XiaoMiView() {
        FontUIResource fontRes = new FontUIResource(new Font(null, Font.PLAIN, 13));
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
        //窗体面板
        jframe = new JFrame("测试窗口");
        jframe.setSize(650, 375);
        jframe.setLocationRelativeTo(null);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 创建内容面板，使用绝对值布局
        panel = new JPanel(null);
        name = new JLabel();
        name.setText("商品名称/链接:");
        name.setBounds(20, 30,100, 20);
        panel.add(name);

        //商品链接
        nameText = new JTextField();
        nameText.setLocation(120, 30);
        nameText.setSize(420, 20);
        panel.add(nameText);

        //搜索按钮
        parseButton = new JButton("搜索-->");
        parseButton.setLocation(545, 30);
        parseButton.setSize(75, 22);
        panel.add(parseButton);

        //自定义选项
        optionLable = new JLabel();
        optionLable.setText("自定义选项:");
        optionLable.setLocation(20, 69);
        optionLable.setSize(100, 20);
        panel.add(optionLable);

        //下拉框1
        option1 = new JComboBox<>();
        option1.addItem("默认");
        option1.addItem("苹果");
        option1.setSelectedIndex(0);
        option1.setLocation(120,66);
        option1.setSize(235, 20);
        panel.add(option1);

        //下拉框2
        option2 = new JComboBox<>();
        option2.addItem("默认");
        option2.addItem("香蕉");
        option2.setLocation(385,66);
        option2.setSize(235, 20);
        panel.add(option2);

        //抢购时间
        buyTime = new JLabel();
        buyTime.setText("抢购时间:");
        buyTime.setLocation(20, 112);
        buyTime.setSize(100, 20);
        panel.add(buyTime);

        //抢购时间框
        buyTimeText = new JTextField();
        buyTimeText.setLocation(120, 112);
        buyTimeText.setSize(500, 20);
        panel.add(buyTimeText);

        //抢购时长
        duration = new JLabel();
        duration.setText("抢购时长(分钟):");
        duration.setLocation(20, 158);
        duration.setSize(100, 20);
        panel.add(duration);

        //抢购时长框
        durationText = new JTextField();
        duration.setText("抢购时长(分钟):");
        durationText.setLocation(120, 158);
        durationText.setSize(500, 20);
        panel.add(durationText);

        //小米账号
        username = new JLabel();
        username.setText("账号:");
        username.setLocation(20, 204);
        username.setSize(100, 20);
        panel.add(username);

        //账号框
        userText = new JTextField();
        userText.setLocation(120, 204);
        userText.setSize(500, 20);
        panel.add(userText);

        //密码
        password = new JLabel();
        password.setText("密码:");
        password.setLocation(20, 250);
        password.setSize(100, 20);
        panel.add(password);

        //密码框
        passwordText = new JTextField();
        passwordText.setLocation(120, 250);
        passwordText.setSize(500, 20);
        panel.add(passwordText);

        //显示日志
        hideButton = new JButton();
        hideButton.setLocation(20, 300);
        hideButton.setSize(90, 30);
        hideButton.setText("显示日志");
        panel.add(hideButton);

        //开始按钮
        startButton = new JButton();
        startButton.setLocation(430, 300);
        startButton.setSize(80, 30);
        startButton.setText("开始");
        panel.add(startButton);

        //停止按钮
        pauseButton = new JButton();
        pauseButton.setLocation(430, 300);
        pauseButton.setSize(80, 30);
        pauseButton.setText("停止");
        pauseButton.setVisible(false);
        panel.add(pauseButton);

        //退出按钮
        quitButton = new JButton();
        quitButton.setLocation(540, 300);
        quitButton.setSize(80, 30);
        quitButton.setText("退出");
        panel.add(quitButton);
        jframe.add(panel);

        parseButton.addActionListener(getParseFunction());

        jframe.setVisible(true);
    }

    public static void main(String[] args) {
        new XiaoMiView();
    }

    @Override
    public JFrame getJframe() {
        return jframe;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public JLabel getName() {
        return name;
    }

    @Override
    public JTextField getNameText() {
        return nameText;
    }

    @Override
    public JButton getParseButton() {
        return parseButton;
    }

    @Override
    public JLabel getOptionLable() {
        return optionLable;
    }

    @Override
    public JComboBox<String> getOption1() {
        return option1;
    }

    @Override
    public JComboBox<String> getOption2() {
        return option2;
    }

    @Override
    public JLabel getBuyTime() {
        return buyTime;
    }

    @Override
    public JTextField getBuyTimeText() {
        return buyTimeText;
    }

    @Override
    public JLabel getDuration() {
        return duration;
    }

    @Override
    public JTextField getDurationText() {
        return durationText;
    }

    @Override
    public JLabel getUsername() {
        return username;
    }

    @Override
    public JTextField getUserText() {
        return userText;
    }

    @Override
    public JLabel getPassword() {
        return password;
    }

    @Override
    public JTextField getPasswordText() {
        return passwordText;
    }

    @Override
    public JButton getHideButton() {
        return hideButton;
    }

    @Override
    public JButton getStartButton() {
        return startButton;
    }

    @Override
    public JButton getPauseButton() {
        return pauseButton;
    }

    @Override
    public JButton getQuitButton() {
        return quitButton;
    }
}
