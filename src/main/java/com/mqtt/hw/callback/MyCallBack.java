package com.mqtt.hw.callback;

import com.common.util.SystemHWUtil;
import com.mqtt.hw.MqttClientSwing;
import com.time.util.TimeHWUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.paho.client.mqttv3.*;

public class MyCallBack implements MqttCallback {
    private MqttClientSwing mqttSwing;


    public MyCallBack(MqttClientSwing mqttSwing) {
        super();
        this.mqttSwing = mqttSwing;
    }

    @Override
    public void connectionLost(Throwable cause) {
        while (true) {
            try {
                System.out.println("connectionLost");
                Thread.sleep(1000L);
                this.mqttSwing.reconnect();
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

    @Override
    public void messageArrived(MqttTopic topic, MqttMessage message)
            throws Exception {
        System.out.println("messageArrived...." + TimeHWUtil.getCurrentMiniuteSecond());
        String source = new String(message.getPayload(), SystemHWUtil.CHARSET_UTF);
        System.out.println("source:" + source);
        String messageStr = StringEscapeUtils.unescapeHtml(source);
        System.out.println("message:" + messageStr);
        this.mqttSwing.receiveMessage(messageStr);
//		byte[]payload=new byte[]{97,98};
//		topic.publish(payload, 2, true);
        //使窗口处于激活状态
//		this.mqttSwing.toFront();
//		this.mqttSwing.requestFocus();
//		MqttMessage mesg=new MqttMessage();
//		mesg.setPayload("back".getBytes());
//		mesg.setRetained(true);

//		topic.publish(message);
    }

    @Override
    public void deliveryComplete(MqttDeliveryToken token) {

    }

}
