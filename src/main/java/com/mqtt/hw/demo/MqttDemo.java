package com.mqtt.hw.demo;

import com.common.util.SystemHWUtil;
import com.mqtt.hw.util.MacSignature;
import com.time.util.TimeHWUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import java.io.IOException;
import java.util.Date;

public class MqttDemo {
    public static void main(String[] args) throws IOException {
        String broker = null;
        String acessKey = null;
        String secretKey = null;
        String clientId = null;
        broker = "tcp://mqtt.ons.aliyun.com:1883";
        acessKey = "K/xLogkkD/AAkQ4qDmmeR61qiLT6N4M9";//已加密
        secretKey = "79PkBKPGRsLWsJV5Du8Tu1rkRAdW+75uSihfd7LTFAA=";//已加密
        final String topic = "com_hbjltv";
        // 如果该设备需要接收点对点的推送，那么需要订阅二级topic，topic/p2p/，但凡以topic/p2p/为前缀的，都认为是点
        // 对点推送
        final String p2ptopic = topic + "/p2p/";
        // 同时订阅两个topic，一个是基于标准mqtt协议的发布订阅模式，一个是扩展的点对点推送模式
        final String[] topicFilters = new String[]{topic,
                p2ptopic};
        clientId = "CID_tv_mobile@@@abaa";
        String sign;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            final MqttClient sampleClient = new MqttClient(broker, clientId,
                    persistence);
            final MqttConnectOptions connOpts = new MqttConnectOptions();
            System.out.println("Connecting to broker: " + broker);
            sign = MacSignature.macSignature(clientId.split("@@@")[0],
                    secretKey);
            connOpts.setUserName(acessKey);
            // connOpts.setServerURIs(new String[] { broker });
            connOpts.setPassword(sign.toCharArray());
            connOpts.setCleanSession(false);
            connOpts.setKeepAliveInterval(100);
            sampleClient.setCallback(new MqttCallback() {
                public void connectionLost(Throwable throwable) {
                    while (true) {
                        try {
                            System.out.println("connectionLost");
                            throwable.printStackTrace();
                            Thread.sleep(1000L);
                            sampleClient.connect(connOpts);
                            System.out.println("reconnect success");
                            sampleClient.subscribe(topicFilters);
                            System.out.println("subscribe....success");
                            break;
                        } catch (MqttSecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (MqttException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            /*
							 * if (e.getReasonCode()==MqttException.
							 * REASON_CODE_CLIENT_CONNECTED) { break; }
							 */
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                public void messageArrived(String topic, MqttMessage mqttMessage)
                        throws Exception {
                    System.out.println("messageArrived:" + topic + "------"
                            + new String(mqttMessage.getPayload()));
                }

                public void deliveryComplete(
                        MqttDeliveryToken iMqttDeliveryToken) {
                    // System.out.println("deliveryComplete:" +
                    // iMqttDeliveryToken.getMessageId());
                }

                @Override
                public void messageArrived(MqttTopic arg0, MqttMessage message)
                        throws Exception {
                    System.out.println("messageArrived...." + TimeHWUtil.getCurrentMiniuteSecond());
                    String source = new String(message.getPayload(), SystemHWUtil.CHARSET_UTF);
                    System.out.println("source:" + source);
                    String messageStr = StringEscapeUtils.unescapeHtml(source);
                    System.out.println("message:" + messageStr);

                }
            });
            sampleClient.connect(connOpts);
            sampleClient.subscribe(topicFilters);
            System.out.println("subscribe....success");
            Thread.sleep(15000L);
            for (int i = 0; i < 1; i++) {
                try {
                    String scontent = new Date() + "MQTT Test body" + i;
                    final MqttMessage message = new MqttMessage(
                            scontent.getBytes());
                    message.setQos(1);
                    System.out.println(i + " pushed at " + new Date() + " "
                            + scontent);
                    // 消息发送到某个主题topic，所有订阅这个topic的设备都能收到这个消息。遵循mqtt的发布订阅规范，topic也
                    // 可以是多级topic。（除了点对点topic/p2p/这个前缀的，作为点对点发送的特殊topic，不遵循发布订阅模式）
                    // sampleClient.publish(topic+"/notice/", message);
                    // 发送给指定设备，格式为topic/p2p/targetClientId,
                    // targetClientId的格式详见user.properties文件
                    // sampleClient.publish(p2ptopic+properties.getProperty("ConsumerId"),
                    // message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception me) {
            me.printStackTrace();
        }
    }
}
