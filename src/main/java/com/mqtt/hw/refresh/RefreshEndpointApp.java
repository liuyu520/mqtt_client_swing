package com.mqtt.hw.refresh;

import com.string.widget.util.ValueWidget;
import com.swing.dialog.DialogUtil;
import com.swing.messagebox.GUIUtil23;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class RefreshEndpointApp extends JPanel {
    private static final long serialVersionUID = -7824289195741882247L;
    /***
     * endpoint的ip
     */
    private JTextField serverIpTextField;
    /***
     * endpoint的socket 端口号
     */
    private JTextField portTextField;
    /***
     * 刷新endpoint
     */
    private JButton refreshButton;
    /***
     * 与endpoint socket的连接
     */
    private Socket server;
    private PrintWriter writer;
    private JComboBox typeComboBox;

    /**
     * Create the panel.
     */
    public RefreshEndpointApp() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0,
                Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0,
                Double.MIN_VALUE};
        setLayout(gridBagLayout);

        JLabel lblip = new JLabel("服务器ip");
        GridBagConstraints gbc_lblip = new GridBagConstraints();
        gbc_lblip.insets = new Insets(0, 0, 5, 5);
        gbc_lblip.gridx = 0;
        gbc_lblip.gridy = 0;
        add(lblip, gbc_lblip);

        serverIpTextField = new JTextField();
        serverIpTextField.setText("localhost");
        GridBagConstraints gbc_serverIpTextField = new GridBagConstraints();
        gbc_serverIpTextField.insets = new Insets(0, 0, 5, 0);
        gbc_serverIpTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_serverIpTextField.gridx = 2;
        gbc_serverIpTextField.gridy = 0;
        add(serverIpTextField, gbc_serverIpTextField);
        serverIpTextField.setColumns(10);

        JLabel label = new JLabel("端口");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 1;
        add(label, gbc_label);

        portTextField = new JTextField();
        portTextField.setText("5050");
        GridBagConstraints gbc_portTextField = new GridBagConstraints();
        gbc_portTextField.insets = new Insets(0, 0, 5, 0);
        gbc_portTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_portTextField.gridx = 2;
        gbc_portTextField.gridy = 1;
        add(portTextField, gbc_portTextField);
        portTextField.setColumns(10);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 3;
        gbc_panel.insets = new Insets(0, 0, 0, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 2;
        add(panel, gbc_panel);

        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!DialogUtil
                        .verifyTFEmpty(serverIpTextField, "endpoint ip ")) {
                    return;
                }
                String endpointIp = serverIpTextField.getText();
                if (!ValueWidget.isValidIP(endpointIp)) {
                    GUIUtil23.warningDialog("非法的ip");
                    return;
                }
                if (!DialogUtil.verifyTFEmpty(portTextField, "socket port ")) {
                    return;
                }
                final String socketPort = portTextField.getText();
                if (!ValueWidget.isInteger(socketPort)) {
                    GUIUtil23.warningDialog("端口号不合法");
                    return;
                }
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        refreshButton.setEnabled(false);
                        if (server == null || writer == null) {
                            try {
                                Socket server = new Socket("localhost", Integer
                                        .parseInt(socketPort));
                                OutputStream out = server.getOutputStream();
                                writer = new PrintWriter(out);
                            } catch (NumberFormatException e1) {
                                e1.printStackTrace();
                                GUIUtil23.errorDialog(e1.getMessage());
                            } catch (UnknownHostException e1) {
                                e1.printStackTrace();
                                GUIUtil23.errorDialog(e1.getMessage());
                            } catch (IOException e1) {
//								e1.printStackTrace();
                                GUIUtil23.errorDialog(e1.getMessage());
                            }
                        }
                        String sendMsg = "refresh";
                        if (typeComboBox.getSelectedIndex() == 0) {

                        } else if (typeComboBox.getSelectedIndex() == 1) {
                            sendMsg += " plugin";
                        } else if (typeComboBox.getSelectedIndex() == 2) {
                            sendMsg += " clientVersion";
                        } else if (typeComboBox.getSelectedIndex() == 3) {
                            sendMsg += " osConf";
                        } else if (typeComboBox.getSelectedIndex() == 4) {
                            sendMsg += " business";
                        }
                        if (writer != null) {
                            writer.write(sendMsg);
                            writer.flush();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        refreshButton.setEnabled(true);
                    }
                }).start();
            }
        });

        typeComboBox = new JComboBox();

        typeComboBox.addItem("全部");
        typeComboBox.addItem("插件")/* plugin */;
        typeComboBox.addItem("客户端")/* clientVersion */;
        typeComboBox.addItem("客户端操作系统配置")/* osConf */;
        typeComboBox.addItem("业务资源")/* business */;
        typeComboBox.setSelectedIndex(0);//全部
        panel.add(typeComboBox);
        panel.add(refreshButton);

    }

}
