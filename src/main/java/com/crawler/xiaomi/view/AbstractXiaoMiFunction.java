package com.crawler.xiaomi.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 14:59 2020/4/15
 * @Modefied by:
 */
public abstract class AbstractXiaoMiFunction {
    /**
     * 窗体控件
     */
    public abstract JFrame getJframe();//窗体面板
    public abstract JPanel getPanel();//内容面板
    public abstract JLabel getName();//商品名称/链接
    public abstract JTextField getNameText();//商品链接
    public abstract JButton getParseButton();//搜索按钮
    public abstract JLabel getOptionLable();//自定义选项
    public abstract JComboBox<String> getOption1();//下拉框1
    public abstract JComboBox<String> getOption2();//下拉框2
    public abstract JLabel getBuyTime();//抢购时间
    public abstract JTextField getBuyTimeText();//抢购时间框
    public abstract JLabel getDuration();//抢购时长
    public abstract JTextField getDurationText();//抢购时长框
    public abstract JLabel getUsername();//账号
    public abstract JTextField getUserText();//账号框
    public abstract JLabel getPassword();//密码
    public abstract JTextField getPasswordText();//密码框
    public abstract JButton getHideButton();//显示日志按钮
    public abstract JButton getStartButton();//开始按钮
    public abstract JButton getPauseButton();//停止按钮
    public abstract JButton getQuitButton();//退出按钮



}
