package com.mqtt.hw;

import com.cmd.dos.hw.util.CMDUtil;
import com.common.util.JarFileHWUtil;
import com.common.util.SystemHWUtil;
import com.common.util.WindowUtil;
import com.file.hw.props.GenericReadPropsUtil;
import com.mqtt.hw.callback.MyCallBack;
import com.mqtt.hw.component.MyTab;
import com.mqtt.hw.component.TabbedPane;
import com.mqtt.hw.component.TabbedPaneListener;
import com.mqtt.hw.listener.MyActionListener;
import com.mqtt.hw.refresh.RefreshEndpointApp;
import com.mqtt.hw.util.MacSignature;
import com.string.widget.util.RandomUtils;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextField;
import com.swing.component.AssistPopupTextPane;
import com.swing.component.ComponentUtil;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericFrame;
import com.swing.menu.MenuUtil2;
import com.swing.messagebox.GUIUtil23;
import com.time.util.TimeHWUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class MqttClientSwing extends GenericFrame {

    /***
     * 阿里云消息队列的Consumer ID
     */
    public static final String Consumer_ID_tv = "CID_tv_mobile";
    public static final String CONF_PATH = "conf_client.properties";
    public static final String KEY_PROP_ACTIVEMQ_IP = "active.ip";
    public static final String KEY_PROP_ACTIVEMQ_PUBLISHER_PORT = "active.publisher.port";
    public static final String KEY_PROP_ACTIVEMQ_MQTT_PORT = "active.mqtt.port";
    public static final String KEY_PROP_ACTIVEMQ_TOPIC = "active.topic";
    public static final String KEY_PROP_ACTIVEMQ_CLIENT_ID = "activemq.client.id";
    /***
     * windows 中,在C盘用户home目录下面;<br>
     * linux 中,在/opt 下面,内容如下:<br>
     * ip=182.92.80.1<br>
     * port=1881<br>
     * topic=yuanjian<br>
     */
    public static final String configFilePath = System.getProperty("user.home") + File.separator + ".mqtt_client.properties";
    /***
     * 推送服务器的ip
     */
    public static final String PROP_KEY_IP = "server_ip";
    /***
     * 推送服务器的端口号
     */
    public static final String PROP_KEY_PORT_TF = "port_tf";
    public static final String PROP_KEY_PORT_COMBOBOX = "port_comboBox";
//	public static final String TOPIC_DEFAULT="topic2222";
//	private int client_id_index=1;
    /***
     * 订阅的主题
     */
    public static final String PROP_KEY_TOPIC = "topic";
    /***
     * 是否是阿里云消息队列MQ
     */
    public static final String PROP_KEY_IS_aliyun_mq_ONS = "is_aliyun_mq_ONS";
    public static final String PROP_KEY_CLIENT_ID = "client_id";
    public static final String PROP_KEY_USERNAME = "mqtt_username";
    public static final String PROP_KEY_PASSWORD = "mqtt_password";
    public static final String PROP_KEY_CLIENT_ID_COMBOBOX = "client_id_comboBox";
    public static final String PROP_KEY_TOPIC_COMBOBOX = "topic_comboBox";
    public static final String PROP_KEY_IP_COMBOBOX = "server_ip_comboBox";
    private static final long serialVersionUID = 3841840037643273433L;
    private static SimpleAttributeSet HTML_RED = new SimpleAttributeSet();
    private static SimpleAttributeSet HTML_GREEN = new SimpleAttributeSet();

    static {
        StyleConstants.setForeground(HTML_RED, Color.red);
        StyleConstants.setBold(HTML_RED, true);
//		    StyleConstants.setItalic(HTML_RED, true);//斜体
//		    StyleConstants.setFontFamily(HTML_RED, "Helvetica");
        StyleConstants.setFontSize(HTML_RED, 24);

        StyleConstants.setForeground(HTML_GREEN, Color.GREEN);
        StyleConstants.setBold(HTML_GREEN, true);
        StyleConstants.setFontSize(HTML_GREEN, 24);
    }

    private JPanel contentPane;
    /***
     * activeMQ 的 ip
     */
    private AssistPopupTextField ipTextField;
    /***
     * activeMQ的端口
     */
    private AssistPopupTextField portTextField;
    /**
     * 客户端的clientId
     */
    private AssistPopupTextField clientIdTextField;
    /***
     * 订阅的主题
     */
    private AssistPopupTextField topicTextField;
    /***
     * 是否清空activeMQ的session
     */
    private AssistPopupTextField cleanSessionTextField;
    /***
     * 和activeMQ建立连接
     */
    private JButton startButton;
    private MqttClient mqttClient;
    private AssistPopupTextPane resultTextPane;
    /***
     * 断开与activeMQ的连接
     */
    private JButton stopButton;
    private String title = "刷新endpoint";
    private int selectedIndex;
    private boolean isInjar = JarFileHWUtil.isInJar(getClass());
    /***
     * 会把以下字符串存储到配置文件中
     */
//	public static final String SAVE_CONFIG_TEMP="ip=%s"+SystemHWUtil.CRLF+"port=%s"+SystemHWUtil.CRLF+"topic=%s"+SystemHWUtil.CRLF;
    private boolean isPrintException = true;
    private JPanel panel_1;
    private AssistPopupTextField delayTextField;
    private JButton timeButton;
    private JLabel label_2;
    private JScrollPane scrollPane;
    private TabbedPane tabbedPane;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private AssistPopupTextField usernameTextField;
    private AssistPopupTextField passwordTextField;
    private JLabel label_3;
    private JComboBox<String> portComboBox;
    private JLabel label_4;
    private AssistPopupTextField textField;
    private JLabel lblClientId;
    private JComboBox<String> clientIdComboBox;
    private JLabel lblTopic_1;
    private JComboBox<String> topicComboBox;
    private JLabel label_ip;
    private JComboBox<String> ipComboBox;
    private JLabel lblNewLabel;
    /***
     * 服务器订阅的topic,通过该topic向服务器发送消息
     */
    private JTextField targetTopicTField_1;
    private JScrollPane sendScrollPane_1;
    private JTextArea sendTextArea;
    private int pushHeight = 780;
    private JPanel panel_2;
    private JCheckBox isAliyuncheckBox;
    private MqttConnectOptions options;
    /***
     * 客户端订阅的topic
     */
    private String[] topicFilters = null;

    /**
     * Create the frame.
     */
    public MqttClientSwing() {
        /***
         * 设置window 样式
         */
        DialogUtil.lookAndFeel2();
        setIcon();
        menu3();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(100, 100, );
//		repaintSize(550, 200);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        contentPane.setLayout(new BorderLayout(0, 0));
        tabbedPane = new TabbedPane(JTabbedPane.TOP);
        tabbedPane.setCloseButtonEnabled(false);//是否允许关闭标签页
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        JPanel panelMqtt = new JPanel();
        tabbedPane.addTab("android 推送客户端", null, panelMqtt, null);

        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 170, 0, 0};
        gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        panelMqtt.setLayout(gbl_contentPane);

        JButton lblip = new JButton("服务器ip");
        GridBagConstraints gbc_lblip = new GridBagConstraints();
        gbc_lblip.insets = new Insets(0, 0, 5, 5);
        gbc_lblip.gridx = 0;
        gbc_lblip.gridy = 0;
        panelMqtt.add(lblip, gbc_lblip);

        ipTextField = new AssistPopupTextField();
        ipTextField.setText("localhost");
        GridBagConstraints gbc_ipTextField = new GridBagConstraints();
        gbc_ipTextField.insets = new Insets(0, 0, 5, 0);
        gbc_ipTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_ipTextField.gridx = 2;
        gbc_ipTextField.gridy = 0;
        panelMqtt.add(ipTextField, gbc_ipTextField);
        ipTextField.setColumns(10);
        lblip.addActionListener(new ActionListener() {//必须在ipTextField 的new之后

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = WindowUtil.getSysClipboardText();
                if (!ValueWidget.isNullOrEmpty(text)) {
                    ipTextField.setText(text);
                }
            }
        });

        panel_2 = new JPanel();
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.anchor = GridBagConstraints.WEST;
        gbc_panel_2.gridwidth = 3;
        gbc_panel_2.insets = new Insets(0, 0, 5, 5);
        gbc_panel_2.fill = GridBagConstraints.VERTICAL;
        gbc_panel_2.gridx = 0;
        gbc_panel_2.gridy = 1;
        panelMqtt.add(panel_2, gbc_panel_2);

        isAliyuncheckBox = new JCheckBox("是否是阿里云");
//        isAliyuncheckBox.setSelected(true);
        panel_2.add(isAliyuncheckBox);


        label_ip = new JLabel("服务器ip");
        GridBagConstraints gbc_label_3ip = new GridBagConstraints();
        gbc_label_3ip.insets = new Insets(0, 0, 5, 5);
        gbc_label_3ip.gridx = 0;
        gbc_label_3ip.gridy = 2;
        panelMqtt.add(label_ip, gbc_label_3ip);

        ipComboBox = new JComboBox<String>();
        ipComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedPort = (String) ipComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    ipTextField.setText(selectedPort);
                }
//				System.out.println(e.getSource());
            }
        });
//		portComboBox.addItem(String.valueOf(1881));
//		portComboBox.addItem(String.valueOf(1882));
//		portComboBox.addItem(String.valueOf(1883));
        //设置默认选中的项
//		portComboBox.setSelectedIndex(2);
        ipComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String selectedPort = (String) ipComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    ipTextField.setText(selectedPort);
                }
            }
        });
        GridBagConstraints gbc_ipComboBox2 = new GridBagConstraints();
        gbc_ipComboBox2.insets = new Insets(0, 0, 5, 0);
        gbc_ipComboBox2.fill = GridBagConstraints.HORIZONTAL;
        gbc_ipComboBox2.gridx = 2;
        gbc_ipComboBox2.gridy = 2;
        panelMqtt.add(ipComboBox, gbc_ipComboBox2);


        JLabel label = new JLabel("端口号");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 3;
        panelMqtt.add(label, gbc_label);

        portTextField = new AssistPopupTextField();
        portTextField.setText("1883");
        portTextField.setEditable(false);

        //双击变为可以编辑
        portTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (!portTextField.isEditable()) {
                        portTextField.setEditable(true);
                        DialogUtil.focusSelectAllTF(portTextField);
                    }
//					System.out.println("Double Click!");
                }
                super.mouseClicked(e);
            }

        });
        DialogUtil.addKeyListener22(portTextField);
        GridBagConstraints gbc_portTextField = new GridBagConstraints();
        gbc_portTextField.insets = new Insets(0, 0, 5, 0);
        gbc_portTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_portTextField.gridx = 2;
        gbc_portTextField.gridy = 3;
        panelMqtt.add(portTextField, gbc_portTextField);
        portTextField.setColumns(10);

        label_3 = new JLabel("端口号");
        GridBagConstraints gbc_label_3 = new GridBagConstraints();
        gbc_label_3.insets = new Insets(0, 0, 5, 5);
        gbc_label_3.gridx = 0;
        gbc_label_3.gridy = 4;
        panelMqtt.add(label_3, gbc_label_3);

        portComboBox = new JComboBox<String>();
        portComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedPort = (String) portComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    portTextField.setText(selectedPort);
                }
//				System.out.println(e.getSource());
            }
        });
//		portComboBox.addItem(String.valueOf(1881));
//		portComboBox.addItem(String.valueOf(1882));
//		portComboBox.addItem(String.valueOf(1883));
        //设置默认选中的项
//		portComboBox.setSelectedIndex(2);
        portComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String selectedPort = (String) portComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    portTextField.setText(selectedPort);
                }
            }
        });
        GridBagConstraints gbc_topicComboBox = new GridBagConstraints();
        gbc_topicComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_topicComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_topicComboBox.gridx = 2;
        gbc_topicComboBox.gridy = 4;
        panelMqtt.add(portComboBox, gbc_topicComboBox);

        lblUsername = new JLabel("username");
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.gridx = 0;
        gbc_lblUsername.gridy = 5;
        panelMqtt.add(lblUsername, gbc_lblUsername);

        usernameTextField = new AssistPopupTextField();
        GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
        gbc_usernameTextField.insets = new Insets(0, 0, 5, 0);
        gbc_usernameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_usernameTextField.gridx = 2;
        gbc_usernameTextField.gridy = 5;
        panelMqtt.add(usernameTextField, gbc_usernameTextField);
        usernameTextField.setColumns(10);

        lblPassword = new JLabel("password");
        GridBagConstraints gbc_lblPassword = new GridBagConstraints();
        gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
        gbc_lblPassword.gridx = 0;
        gbc_lblPassword.gridy = 6;
        panelMqtt.add(lblPassword, gbc_lblPassword);

        passwordTextField = new AssistPopupTextField();
        GridBagConstraints gbc_passwordTextField = new GridBagConstraints();
        gbc_passwordTextField.insets = new Insets(0, 0, 5, 0);
        gbc_passwordTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordTextField.gridx = 2;
        gbc_passwordTextField.gridy = 6;
        panelMqtt.add(passwordTextField, gbc_passwordTextField);
        passwordTextField.setColumns(10);

        label_4 = new JLabel("预留");
        GridBagConstraints gbc_label_4 = new GridBagConstraints();
        gbc_label_4.insets = new Insets(0, 0, 5, 5);
        gbc_label_4.gridx = 0;
        gbc_label_4.gridy = 7;
        panelMqtt.add(label_4, gbc_label_4);

        textField = new AssistPopupTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 0);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 2;
        gbc_textField.gridy = 7;
        panelMqtt.add(textField, gbc_textField);
        textField.setColumns(10);

        JLabel lblclientId = new JLabel("客户端id");
        GridBagConstraints gbc_lblid = new GridBagConstraints();
        gbc_lblid.insets = new Insets(0, 0, 5, 5);
        gbc_lblid.gridx = 0;
        gbc_lblid.gridy = 8;
        panelMqtt.add(lblclientId, gbc_lblid);

        clientIdTextField = new AssistPopupTextField();
        clientIdTextField.setText("android-client5_" + RandomUtils.getRandomStr(5));
        GridBagConstraints gbc_clientIdTextField = new GridBagConstraints();
        gbc_clientIdTextField.insets = new Insets(0, 0, 5, 0);
        gbc_clientIdTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_clientIdTextField.gridx = 2;
        gbc_clientIdTextField.gridy = 8;
        panelMqtt.add(clientIdTextField, gbc_clientIdTextField);
        clientIdTextField.setColumns(10);

        lblClientId = new JLabel("client id");
        GridBagConstraints gbc_lblClientId = new GridBagConstraints();
        gbc_lblClientId.insets = new Insets(0, 0, 5, 5);
        gbc_lblClientId.gridx = 0;
        gbc_lblClientId.gridy = 9;
        panelMqtt.add(lblClientId, gbc_lblClientId);

        clientIdComboBox = new JComboBox<String>();
        GridBagConstraints gbc_comboBox_clientId = new GridBagConstraints();
        gbc_comboBox_clientId.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox_clientId.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_clientId.gridx = 2;
        gbc_comboBox_clientId.gridy = 9;
        panelMqtt.add(clientIdComboBox, gbc_comboBox_clientId);
        clientIdComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedPort = (String) clientIdComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    clientIdTextField.setText(selectedPort);
                }
//				System.out.println(e.getSource());
            }
        });
        clientIdComboBox.addItem("864587025867988_898600");
        //设置默认选中的项
//		clientIdComboBox.setSelectedIndex(2);
        clientIdComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String selectedPort = (String) clientIdComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    clientIdTextField.setText(selectedPort);
                }
            }
        });

        JLabel lblTopic = new JLabel("topic");
        GridBagConstraints gbc_lblTopic = new GridBagConstraints();
        gbc_lblTopic.insets = new Insets(0, 0, 5, 5);
        gbc_lblTopic.gridx = 0;
        gbc_lblTopic.gridy = 10;
        panelMqtt.add(lblTopic, gbc_lblTopic);

        topicTextField = new AssistPopupTextField();
//		topicTextField.setText(TOPIC_DEFAULT);
        GridBagConstraints gbc_topicTextField = new GridBagConstraints();
        gbc_topicTextField.insets = new Insets(0, 0, 5, 0);
        gbc_topicTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_topicTextField.gridx = 2;
        gbc_topicTextField.gridy = 10;
        panelMqtt.add(topicTextField, gbc_topicTextField);
        topicTextField.setColumns(10);

        lblTopic_1 = new JLabel("topic");
        GridBagConstraints gbc_lblTopic_1 = new GridBagConstraints();
        gbc_lblTopic_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblTopic_1.gridx = 0;
        gbc_lblTopic_1.gridy = 11;
        panelMqtt.add(lblTopic_1, gbc_lblTopic_1);

        topicComboBox = new JComboBox<String>();
        GridBagConstraints gbc_topicComboBox2 = new GridBagConstraints();
        gbc_topicComboBox2.insets = new Insets(0, 0, 5, 0);
        gbc_topicComboBox2.fill = GridBagConstraints.HORIZONTAL;
        gbc_topicComboBox2.gridx = 2;
        gbc_topicComboBox2.gridy = 11;
        panelMqtt.add(topicComboBox, gbc_topicComboBox2);
        topicComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedPort = (String) topicComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    topicTextField.setText(selectedPort);
                }
//				System.out.println(e.getSource());
            }
        });
        //设置默认选中的项
//		clientIdComboBox.setSelectedIndex(2);
        topicComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String selectedPort = (String) topicComboBox.getSelectedItem();
                if (!ValueWidget.isNullOrEmpty(selectedPort)) {
                    topicTextField.setText(selectedPort);
                }
            }
        });

        JLabel label_1 = new JLabel("是否持久化");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.insets = new Insets(0, 0, 5, 5);
        gbc_label_1.gridx = 0;
        gbc_label_1.gridy = 12;
        panelMqtt.add(label_1, gbc_label_1);

        cleanSessionTextField = new AssistPopupTextField();
        cleanSessionTextField.setText("false");
        cleanSessionTextField.setEditable(false);
        //双击变为可以编辑
        cleanSessionTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (!cleanSessionTextField.isEditable()) {
                        cleanSessionTextField.setEditable(true);
                        DialogUtil.focusSelectAllTF(cleanSessionTextField);
                    }
//					System.out.println("Double Click!");
                }
                super.mouseClicked(e);
            }

        });
        GridBagConstraints gbc_cleanSessionTextField = new GridBagConstraints();
        gbc_cleanSessionTextField.insets = new Insets(0, 0, 5, 0);
        gbc_cleanSessionTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_cleanSessionTextField.gridx = 2;
        gbc_cleanSessionTextField.gridy = 12;
        panelMqtt.add(cleanSessionTextField, gbc_cleanSessionTextField);
        cleanSessionTextField.setColumns(10);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.gridwidth = 3;
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 13;
        panelMqtt.add(panel, gbc_panel);

        startButton = new JButton("启动");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!validate22()) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startConnect();

                    }
                }).start();
            }
        });
        panel.add(startButton);

        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                stop();
                startButton.setEnabled(true);
            }
        });
        panel.add(stopButton);

        panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.gridwidth = 3;
        gbc_panel_1.insets = new Insets(0, 0, 5, 0);
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 14;
        panelMqtt.add(panel_1, gbc_panel_1);

        delayTextField = new AssistPopupTextField();
        panel_1.add(delayTextField);
        delayTextField.setColumns(20);

        label_2 = new JLabel("秒");
        panel_1.add(label_2);

        timeButton = new JButton("定时启动");
        timeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!startButton.isEnabled()) {
                    GUIUtil23.warningDialog("[启动]按钮不可用");
                    return;
                }
                String delayStr = delayTextField.getText();
                if (!DialogUtil.verifyTFEmpty(delayTextField, "定时启动时间")) {
                    return;
                }
                if (!ValueWidget.isInteger(delayStr)) {
                    GUIUtil23.warningDialog("定时启动的时间必须是数字[秒]");
                    return;
                }
                final int secondDelay = Integer.parseInt(delayStr);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!validate22()) {
                            return;
                        }
                        try {
                            timeButton.setEnabled(false);
                            delayTextField.setEditable(false);
                            Thread.sleep(secondDelay * 1000);//单位是毫秒
                            startConnect();
                            timeButton.setEnabled(true);
                            delayTextField.setEditable(true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        panel_1.add(timeButton);

        JButton cleanUpBtn = new JButton("清空");
        cleanUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTextPane.setText("");
            }
        });
        panel_1.add(cleanUpBtn);

        lblNewLabel = new JLabel("服务器topic");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 15;
        panelMqtt.add(lblNewLabel, gbc_lblNewLabel);

        targetTopicTField_1 = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.insets = new Insets(0, 0, 5, 0);
        gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_1.gridx = 2;
        gbc_textField_1.gridy = 15;
        panelMqtt.add(targetTopicTField_1, gbc_textField_1);
        targetTopicTField_1.setColumns(10);

        scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 16;
        panelMqtt.add(scrollPane, gbc_scrollPane);

        //执行结果显示窗口
        resultTextPane = new AssistPopupTextPane();
        resultTextPane.setContentType("text/html; charset=UTF-8");
//		resultTextArea.setText("<html></html>");
        resultTextPane.setEditable(false);
        DefaultCaret caret = (DefaultCaret) resultTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

//		resultTextArea.setLineWrap(true);
//		resultTextArea.setWrapStyleWord(true);

        scrollPane.setViewportView(resultTextPane);
//		scrollPane.setVerticalScrollBarPolicy(policy);
//		JScrollBar bar= scrollPane.getVerticalScrollBar();
        Border border1 = BorderFactory.createEtchedBorder(Color.white,
                new Color(148, 145, 140));
        TitledBorder openFileTitle = new TitledBorder(border1, "接收到的推送消息");
        scrollPane.setBorder(openFileTitle);

        sendScrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.gridwidth = 3;
        gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 0;
        gbc_scrollPane_1.gridy = 17;
        panelMqtt.add(sendScrollPane_1, gbc_scrollPane_1);

        sendTextArea = new JTextArea();
        sendTextArea.setLineWrap(true);
        sendTextArea.setWrapStyleWord(true);
        sendScrollPane_1.setViewportView(sendTextArea);
        Border border2 = BorderFactory.createEtchedBorder(Color.white,
                new Color(148, 145, 140));
        TitledBorder openFileTitle2 = new TitledBorder(border1, "要发送的消息");
        sendScrollPane_1.setBorder(openFileTitle2);

//		tabbedPane.addTab("刷新endpoint", null, getRefreshPane(), null);
        try {
            readConfig();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("selectedIndex:" + selectedIndex);
        if (selectedIndex > tabbedPane.getTabCount() - 1) {
            selectedIndex = tabbedPane.getTabCount() - 1;
        }
        tabbedPane.setSelectedIndex(selectedIndex);
        if (selectedIndex == 0) {
            setTitle("android 推送客户端(接收消息)");
            repaintSize(550, pushHeight);
            this.getContentPane().repaint();
        } else {
            setTitle(title);
        }

        tabbedPane.addTabbedPaneListener(new TabbedPaneListener() {

            @Override
            public void tabSelected(MyTab tab, Component component, int index) {
//				System.out.println("selected index:"+index);
                if (index == 0) {
                    repaintSize(550, pushHeight);
                } else {
                    setSize(550, 200);
                }
            }

            @Override
            public void tabRemoved(MyTab tab, Component component, int index) {

            }

            @Override
            public void tabAdded(MyTab tab, Component component, int index) {

            }

            @Override
            public boolean canTabClose(MyTab tab, Component component) {
                return false;
            }

            @Override
            public void allTabsRemoved() {

            }
        });
        init();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("closing....");
                saveConfig();
                stop();
                super.windowClosing(e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                /*System.out.println("closed");
				try {
					saveConfig();
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
                super.windowClosed(e);
            }
        });
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MqttClientSwing frame = new MqttClientSwing();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean validateClientId(String clientId) {
        if (ValueWidget.isNullOrEmpty(clientId)) {
            return false;
        }
        if (clientId.length() > 23) {
            GUIUtil23.warningDialog("总长度不能超过23个字符");
            return false;
        }
        if (!clientId.startsWith(Consumer_ID_tv + "@@@")) {
            GUIUtil23.warningDialog("必须以申请的Consumer ID 开头,后面跟@@@");
            return false;
        }
        return true;
    }

    private void readConfig() throws IOException {
//		GenericReadPropsUtil propUtil=new GenericReadPropsUtil();
        Properties prop = GenericReadPropsUtil.getProperties(!isInjar, CONF_PATH);
        /***
         * 从系统盘目录下读取配置文件
         */
//		props= GenericReadPropsUtil.getProperties(false, configFilePath);
        configFile = new File(configFilePath);
        if (configFile.exists()) {
            InputStream inStream = new FileInputStream(configFile);
            props.load(inStream);
            inStream.close();//及时关闭资源
        }
        String serverIp22 = null;
        String port22 = null;
        String topic22 = null;
        serverIp22 = props.getProperty(PROP_KEY_IP);
        port22 = props.getProperty(PROP_KEY_PORT_TF);
        topic22 = props.getProperty(PROP_KEY_TOPIC);
        String isAliyunStr = props.getProperty(PROP_KEY_IS_aliyun_mq_ONS);
        boolean isAliyun = "true".equalsIgnoreCase(isAliyunStr);
        if (isAliyun) {
            isAliyuncheckBox.setSelected(isAliyun);
        }
        setSwingInput(clientIdTextField, PROP_KEY_CLIENT_ID);
        setSwingInput(usernameTextField, PROP_KEY_USERNAME);
        setSwingInput(passwordTextField, PROP_KEY_PASSWORD);
        ComponentUtil.fillComboBox(portComboBox, getPropValue(PROP_KEY_PORT_COMBOBOX));
        ComponentUtil.fillComboBox(clientIdComboBox, getPropValue(PROP_KEY_CLIENT_ID_COMBOBOX));
        ComponentUtil.fillComboBox(topicComboBox, getPropValue(PROP_KEY_TOPIC_COMBOBOX));
        ComponentUtil.fillComboBox(ipComboBox, getPropValue(PROP_KEY_IP_COMBOBOX));
//		if(ValueWidget.isNullOrEmpty(prop)){
//			prop= GenericReadPropsUtil.getProperties(false, CONF_PATH);
//		}
        if (ValueWidget.isNullOrEmpty(prop)  ) {
        if(ValueWidget.isNullOrEmpty(props)){
            return;
            }else{
            prop = props;
            }
        }
        String key2 = "selected_index";
        if (ValueWidget.isNullOrEmpty(prop)) {
            selectedIndex = 0;
        } else {
        String selectedIndexStr =prop.getProperty(key2);
        if (null != selectedIndexStr) {
            selectedIndex = Integer.parseInt(selectedIndexStr);
            }
        }

        String propValue = null;
        if (ValueWidget.isNullOrEmpty(serverIp22)) {

            key2 = KEY_PROP_ACTIVEMQ_IP;
            propValue = prop.getProperty(key2);
            if (propValue != null) {
                this.ipTextField.setText(propValue);
            }
        } else {
            this.ipTextField.setText(serverIp22);
        }
        if (ValueWidget.isNullOrEmpty(port22)) {
            key2 = KEY_PROP_ACTIVEMQ_MQTT_PORT;
            propValue = prop.getProperty(key2);
            if (propValue != null) {
                this.portTextField.setText(propValue);
            }
        } else {
            this.portTextField.setText(port22);
        }

        if (ValueWidget.isNullOrEmpty(topic22)) {
            key2 = KEY_PROP_ACTIVEMQ_TOPIC;
            propValue = prop.getProperty(key2);
            if (propValue != null) {
                this.topicTextField.setText(propValue);
            }
        } else {
            this.topicTextField.setText(topic22);
        }

        key2 = KEY_PROP_ACTIVEMQ_CLIENT_ID;
        if (ValueWidget.isNullOrEmpty(prop)) {

        } else {
            propValue = prop.getProperty(key2);
        }
        if (propValue != null) {
            this.clientIdTextField.setText(propValue);
        }

    }

    private void init() {
        setGlobalShortCuts();
//		this.setTitle(title);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //关闭window时,也关闭与activeMQ的连接
//				MqttClientSwing.this.stop();
//				System.out.println("windowClosing");
                super.windowClosing(e);
            }

        });
    }

    /***
     * 设置软件图标icon
     */
    private void setIcon() {
        URL url = this.getClass().getResource("/com/mqtt/hw/img/icon.png");
        ImageIcon icon = new ImageIcon(url);
        this.setIconImage(icon.getImage());

    }

    /***
     * 重新设置窗口大小和居中
     *
     * @param width
     * @param height
     */
    private void repaintSize(int width, int height) {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(width, height);
        Dimension framesize = getSize();
        int x = (int) screensize.getWidth() / 2 - (int) framesize.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - (int) framesize.getHeight() / 2;
        setLocation(x, y);
    }

    private void startConnect() {
        String BROKER_URL = "tcp://" + ipTextField.getText().trim() + ":" + portTextField.getText().trim();
        ComponentUtil.appendResult(resultTextPane, "BROKER_URL:" + BROKER_URL, false);
        String clientId = clientIdTextField.getText()/*+client_id_index++*/;
        String TOPIC = topicTextField.getText();

        ComponentUtil.appendResult(resultTextPane, "clientId:" + clientId, false);

        boolean isSuccess = connect(BROKER_URL.trim(), clientId.trim(), TOPIC, Boolean.parseBoolean(cleanSessionTextField.getText()));
        if (isSuccess) {
            stopButton.setEnabled(true);
            startButton.setEnabled(false);
        }
    }

    private void send2Server() {

    }

    /***
     * 刷新endpoint
     *
     * @return
     */
    public JPanel getRefreshPane() {
        return new RefreshEndpointApp();
    }

    /***
     * 接收到推送消息
     *
     * @param message
     */
    public void receiveMessage(String message) {
//		ComponentUtil.appendResult(resultTextArea, "收到:", false);//http://stackoverflow.com/questions/4526192/convert-a-string-containing-ascii-to-unicode
        ComponentUtil.appendResult(resultTextPane, "receive time:" + TimeHWUtil.getCurrentMiniuteSecond(), false);
        ComponentUtil.appendResult(resultTextPane, message, MqttClientSwing.HTML_RED, true);
        //保证文本域始终保持在底部
        DefaultCaret caret = (DefaultCaret) resultTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        resultTextPane.setSelectionStart(resultTextPane.getText().length());
        scrollPane.scrollRectToVisible(new Rectangle(0, this.getBounds(null).height + 200, 1, 1));
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
        repaintFrame();

    }

    public void repaintFrame() {
        this.repaint();
        this.getContentPane().validate();
    }

    private void stop() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                mqttClient = null;
                String message = "断开连接";
                System.out.println(message);
                ComponentUtil.appendResult(resultTextPane, message, true);
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }
    }

    /***
     * 客户端和activeMQ服务器建立连接
     *
     * @param BROKER_URL
     * @param clientId       : 用于标识客户端,相当于ios中的device token
     * @param TOPIC
     * @param isCleanSession :false--可以接受离线消息;
     * @return 是否启动成功
     */
    private boolean connect(String BROKER_URL, String clientId, String TOPIC, boolean isCleanSession) {
        String sign;
        boolean isAliyun = isAliyuncheckBox.isSelected();
        if (isAliyun) {
            if (!validateClientId(clientId)) {
                return false;
            }
        }
        try {
            ComponentUtil.appendResult(resultTextPane, "connect start time:" + TimeHWUtil.getCurrentMiniuteSecond(), false);
            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
            ComponentUtil.appendResult(resultTextPane, "clientId" + clientId, false);
            options = new MqttConnectOptions();
            options.setCleanSession(isCleanSession);//mqtt receive offline message
            ComponentUtil.appendResult(resultTextPane, "isCleanSession:" + isCleanSession, true);
            int keepAliveInterval = 120;
            options.setKeepAliveInterval(keepAliveInterval);
            System.out.println("alive Interval:" + keepAliveInterval);
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            if (ValueWidget.isNullOrEmpty(username)) {
                username = null;
            }
            if (isAliyun) {
                sign = MacSignature.macSignature(Consumer_ID_tv, password);
                password = sign;
//            	System.out.println("password:"+password);
//            	options.setServerURIs(new String[] { BROKER_URL });
            }
            if (ValueWidget.isNullOrEmpty(password)) {
                password = null;
            } else {
                options.setPassword(password.toCharArray());
            }
            options.setUserName(username);
            MqttTopic mqttTopic = mqttClient.getTopic(TOPIC);
//            options.setWill(mqttTopic, payload, 0, true);
            //推送回调类,在此类中处理消息,用于消息监听
            mqttClient.setCallback(new MyCallBack(MqttClientSwing.this));
            boolean isSuccess = false;
            try {
                mqttClient.connect(options);//CLIENT ID CAN NOT BE SAME
                ComponentUtil.appendResult(resultTextPane, "connect success", MqttClientSwing.HTML_GREEN, true);
                isSuccess = true;
                if (!isAliyun) {
                    MqttTopic mqtttopic = mqttClient.getTopic("topic2222");
                    MqttMessage message2 = new MqttMessage(new byte[]{97, 98});
//					mqtttopic.publish(new byte[]{97,98}, 1, true);
                    mqtttopic.publish(message2);
                }
            } catch (Exception e) {
                if (isPrintException) {
                    e.printStackTrace();
                }
            }

            if (!isSuccess) {
                String message = "连接失败,请检查client id是否重复了 或者activeMQ是否启动";
                stop();
                ComponentUtil.appendResult(resultTextPane, message, true);
                GUIUtil23.warningDialog(message);
                return false;
            } else {
                //Subscribe to topics
                if (isAliyun) {
                    final String p2ptopic = TOPIC + "/p2p/";
                    //同时订阅两个topic，一个是基于标准mqtt协议的发布订阅模式，一个是扩展的点对点推送模式
                    topicFilters = new String[]{TOPIC, p2ptopic};
                } else {
                    topicFilters = new String[]{TOPIC, clientId};
                }
                mqttClient.subscribe(topicFilters);

                System.out.println("topic:" + TOPIC + ",  " + (clientId));
                ComponentUtil.appendResult(resultTextPane, "TOPIC:" + SystemHWUtil.formatArr(topicFilters, " , "), true);
            }

        } catch (MqttException e) {
            if (isPrintException) {
                e.printStackTrace();
            }
            GUIUtil23.errorDialog(e.getMessage());
            return false;
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    /***
     * 重新连接推送服务器
     *
     * @throws MqttException
     */
    public void reconnect() throws MqttException {
        if (null != mqttClient) {
            mqttClient.connect(options);
            System.out.println("reconnect success");
            mqttClient.subscribe(topicFilters);
            System.out.println("subscribe....success");
        }
    }

    private boolean validate22() {
        if (!DialogUtil.verifyTFEmpty(ipTextField, "服务器ip")) {
            return false;
        }
		/*if(!ValueWidget.isValidV4IP(ipTextField.getText())){
			GUIUtil23.warningDialog("ip 格式不对");
			ipTextField.requestFocus();
			ipTextField.selectAll();
			return false;
		}*/
        return true;
    }

    /***
     * 菜单
     */
    private void menu3() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileM = new JMenu("File");
        JMenuItem fileM_help = new JMenuItem("帮助");
//	        JMenuItem fileM_open = new JMenuItem(MenuUtil2.ACTION_STR_OPEN);
        JMenuItem fileM_save = new JMenuItem(MenuUtil2.ACTION_STR_SAVE);
//	        JMenuItem fileM_close = new JMenuItem(MenuUtil2.ACTION_STR_CLOSE);
//	        fileM_close.setActionCommand(MenuUtil2.ACTION_STR_CLOSE);
        JMenuItem fileM_exit = new JMenuItem(MenuUtil2.ACTION_STR_EXIT);
        fileM_exit.setActionCommand(MenuUtil2.ACTION_STR_EXIT);

        MyActionListener myMenuListener = new MyActionListener(this);
//	        fileM_close.addActionListener(myMenuListener);

        fileM_help.setActionCommand("help");
        fileM_help.addActionListener(myMenuListener);

//	        fileM_open.setActionCommand(MenuUtil2.ACTION_STR_OPEN);
//	        fileM_open.addActionListener(myMenuListener);

        fileM_save.setActionCommand(MenuUtil2.ACTION_STR_SAVE);
        fileM_save.addActionListener(myMenuListener);

        fileM_exit.addActionListener(myMenuListener);

        fileM.add(fileM_help);
//	        fileM.add(fileM_open);
        fileM.add(fileM_save);
//	        fileM.add(fileM_close);
        fileM.add(fileM_exit);

        menuBar.add(fileM);
        this.setJMenuBar(menuBar);

    }

    /***
     * 增加全局快捷键.<Br>
     * Ctrl+S,保存参数到配置文件
     */
    protected void setGlobalShortCuts() {
        // Add global shortcuts
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // 注册应用程序全局键盘事件, 所有的键盘事件都会被此事件监听器处理.
        toolkit.addAWTEventListener(new java.awt.event.AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if (event.getClass() == KeyEvent.class) {
                    KeyEvent kE = ((KeyEvent) event);
                    // 处理按键事件 Ctrl+S
                    if (kE.getKeyCode() == KeyEvent.VK_S
                            && kE.isControlDown()
                            && kE.getID() == KeyEvent.KEY_PRESSED) {
                        saveConfig();
                    }
                }
            }
        }, java.awt.AWTEvent.KEY_EVENT_MASK);

    }

    /***
     * 保存到配置文件中
     */
    protected void saveConfig() {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            try {
                SystemHWUtil.createEmptyFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
                GUIUtil23.errorDialog(e);
                return;
            }
        }
        if (ValueWidget.isNullOrEmpty(props)) {
            props = new Properties();
        }

        String ip2 = ipTextField.getText();
        if (!ValueWidget.isNullOrEmpty(ip2)) {
            props.setProperty(PROP_KEY_IP, ip2);
        }
        String port2 = portTextField.getText();
        if (!ValueWidget.isNullOrEmpty(port2)) {
            props.setProperty(PROP_KEY_PORT_TF, port2);
        }

        String topic2 = topicTextField.getText();
        if (!ValueWidget.isNullOrEmpty(topic2)) {
            props.setProperty(PROP_KEY_TOPIC, topic2);
        }

        String client_id2 = clientIdTextField.getText();
        if (!ValueWidget.isNullOrEmpty(client_id2)) {
            props.setProperty(PROP_KEY_CLIENT_ID, client_id2);
        }

        String username2 = usernameTextField.getText();
        if (!ValueWidget.isNullOrEmpty(username2)) {
            props.setProperty(PROP_KEY_USERNAME, username2);
        }

        String mqtt_password2 = passwordTextField.getText();
        if (!ValueWidget.isNullOrEmpty(mqtt_password2)) {
            props.setProperty(PROP_KEY_PASSWORD, mqtt_password2);
        }
        props.setProperty(PROP_KEY_IS_aliyun_mq_ONS, String.valueOf(isAliyuncheckBox.isSelected()));
        setCombox(PROP_KEY_PORT_COMBOBOX, portTextField, portComboBox, false);

        setCombox(PROP_KEY_CLIENT_ID_COMBOBOX, clientIdTextField, clientIdComboBox, false);
        setCombox(PROP_KEY_TOPIC_COMBOBOX, topicTextField, topicComboBox, false);
        setCombox(PROP_KEY_IP_COMBOBOX, ipTextField, ipComboBox, false);

        CMDUtil.show(configFilePath);//因为隐藏文件是只读的
        try {
            OutputStream out = new FileOutputStream(configFilePath);
            props.store(out, TimeHWUtil.formatDateTimeZH(null));
            out.close();//及时关闭资源
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//			FileUtils.writeToFile(configFilePath, content);
//			CMDUtil.executeCmd("attrib "+configFilePath+" +H");
        CMDUtil.hide(configFilePath);
    }
}
