package com.mqtt.hw.listener;

import com.mqtt.hw.MqttClientSwing;
import com.swing.menu.MenuUtil2;
import com.swing.messagebox.GUIUtil23;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyActionListener implements ActionListener {
    private MqttClientSwing pusherApp;

    public MyActionListener(MqttClientSwing pusherApp) {
        super();
        this.pusherApp = pusherApp;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        // System.out.println(command);
        if (command.equals(MenuUtil2.ACTION_STR_EXIT)) {
            this.pusherApp.dispose();
            System.exit(0);
        } else if (command.equals("help")) {
            GUIUtil23.infoDialog("作者:黄威(1287789687@qq.com)");
        }
    }

}
