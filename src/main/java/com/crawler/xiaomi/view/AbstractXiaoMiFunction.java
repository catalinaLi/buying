package com.crawler.xiaomi.view;

import com.crawler.xiaomi.controller.XiaoMiController;
import com.crawler.xiaomi.manage.Config;
import com.crawler.xiaomi.manage.ServiceFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 14:59 2020/4/15
 * @Modefied by:
 */
public abstract class AbstractXiaoMiFunction {

    //小米主服务
    private static  XiaoMiController xiaoMiController = ServiceFactory.getService(XiaoMiController.class);

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

    /**
     * 查询商品按钮
     * @return
     */
    public ActionListener getParseFunction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = getNameText().getText().trim();
                if(name.length()==0){
                    return;
                }
                String errmsg = xiaoMiController.searchGoods(name);
                if(errmsg!=null){
                    getNameText().setText(name+errmsg);
                    return;
                }
                getJframe().setTitle(Config.goodsConfig.getName());
                getNameText().setText(Config.goodsConfig.getName());
                List<String> version = Config.goodsConfig.getVersion();
                getOption1().removeAll();
                getOption1().addItem("默认");
                for(String s:version){
                    getOption1().addItem(s);
                }
                getOption1().setSelectedIndex(0);

                List<String> color = Config.goodsConfig.getColor();
                getOption2().removeAll();
                getOption2().addItem("默认");
                for(String s:color){
                    getOption2().addItem(s);
                }
                getOption2().setSelectedIndex(0);
            }
        };
    }

}
