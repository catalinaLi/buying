package com.crawler.xiaomi.view;

import com.crawler.xiaomi.controller.XiaoMiController;
import com.crawler.xiaomi.manage.Config;
import com.crawler.xiaomi.manage.ServiceFactory;
import com.crawler.xiaomi.manage.StatusManage;
import com.crawler.xiaomi.pojo.CustomRule;
import com.crawler.xiaomi.pojo.GoodsInfo;
import com.crawler.xiaomi.pojo.User;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public abstract JTextArea getLogText();//退出按钮

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
                getOption1().removeAllItems();
                getOption1().addItem("默认");
                for(String s:version){
                    getOption1().addItem(s);
                }
                getOption1().setSelectedIndex(0);

                List<String> color = Config.goodsConfig.getColor();
                getOption2().removeAllItems();
                getOption2().addItem("默认");
                for(String s:color){
                    getOption2().addItem(s);
                }
                getOption2().setSelectedIndex(0);
            }
        };
    }


    /**
     * 日志框显示事件
     * @return
     */
    public ActionListener getHideFunction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getLogText().isVisible()){
                    getJframe().setSize(650, 375);
                    getLogText().setVisible(false);
                    getHideButton().setText("显示日志");
                }else{
                    getJframe().setSize(650, 720);
                    getLogText().setVisible(true);
                    getHideButton().setText("隐藏日志");
                }
            }
        };
    }

    //打开视图
    public void openView(){
        initView();
        getPanel();
        while (getJframe().isDisplayable()) {
            //日志输出
            loadLog();
            //任务完成
            finishMsg();
        }
    }
    //日志输出
    private void loadLog() {
        String loadLog = xiaoMiController.loadLog();
        if(loadLog.length()==0){
            return;
        }
        if(getLogText().getLineCount()>3000){
            getLogText().setText(loadLog);
            return;
        }
        getLogText().append(loadLog + "\r\n");
    }

    //任务完成后的处理逻辑
    private void finishMsg() {
        if(StatusManage.isEnd){
            //修改状态为完成
            modifyStatus(true);
            //发送消息
            sendMsg(StatusManage.endMsg);
            //恢复初始状态
            StatusManage.isEnd = false;
        }
    }

    //提示弹框
    public void sendMsg(String msg) {
        JOptionPane.showMessageDialog(
                getJframe(),
                msg == null ? "" : msg,
                "提示",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    //初始化视图
    private void initView() {
        xiaoMiController.init();
        //设置界面参数
        setViewSetting();
    }

    private void setViewSetting() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = System.currentTimeMillis()+2*60*1000;
        getBuyTimeText().setText(format.format(new Date(time)));
    }

    /**
     * 停止按钮
     * @return
     */
    public ActionListener getPauseFunction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xiaoMiController.stop("停止");
            }
        };
    }

    /**
     * 开始按钮事件
     * @return
     */
    public ActionListener getStartFunction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //读取用户界面配置
                    readParameter();
                    //修改状态为爬取开始
                    modifyStatus(false);
                    //开始抢购
                    xiaoMiController.start();

                } catch (Exception ex) {
                    sendErrMsg(ex.getMessage());
                }
            }
        };
    }

    //错误弹框
    public void sendErrMsg(String msg) {
        JOptionPane.showMessageDialog(
                getJframe(),
                msg == null ? "未知错误,请检查参数设置是否正确" : msg,
                "警告",
                JOptionPane.WARNING_MESSAGE
        );
    }

    //修改状态后控件的变化效果
    private void modifyStatus(boolean isFinish) {
        getStartButton().setVisible(isFinish);
        getPauseButton().setVisible(!isFinish);
        getParseButton().setVisible(isFinish);
        getNameText().setEditable(isFinish);
        getBuyTimeText().setEditable(isFinish);
        getDurationText().setEditable(isFinish);
        getUserText().setEditable(isFinish);
        getPasswordText().setEditable(isFinish);

        getOption1().setEnabled(isFinish);
        getOption2().setEnabled(isFinish);
    }

    public void readParameter() throws Exception {

        String name = getNameText().getText();
        if(Config.goodsConfig==null||!name.equals(Config.goodsConfig.getName())){
            throw new Exception("请先搜索商品");
        }
        String version = (String)getOption1().getSelectedItem();
        String color = (String)getOption2().getSelectedItem();
        Config.goodsInfo = new GoodsInfo(Config.goodsConfig.getUrl(),version,color);
        String buyTime = getBuyTimeText().getText().trim();
        String duration = getDurationText().getText().trim();
        Config.customRule = new CustomRule(buyTime,duration);

        String user = getUserText().getText().trim();
        String password = getPasswordText().getText().trim();
        Config.user = new User(user,password);
    }

    public ActionListener getQuitFunction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(getJframe(), "确定要退出吗？");
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        };
    }

}
