package ru.skhatuntsev;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.concurrent.TimeoutException;

public class MessageProducer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        Connection conn = null;
        try {
            conn = factory.newConnection();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        Channel channel = null;
        if (conn != null) {
            channel = conn.createChannel();
        }
        String queueName = "myQueue";
        String exchangeName = "myExchange";
        String routingKey = "myRoute";
        boolean keepSendingMessages = true;
        boolean durable = true;
        if (channel != null) {
            channel.exchangeDeclare(exchangeName, "direct", durable);
        }
        if (channel != null) {
            channel.queueDeclare(queueName, durable, false, false, null);
        }
        if (channel != null) {
            channel.queueBind(queueName, exchangeName, routingKey);
        }

        for (long i = 0; keepSendingMessages; i++) {
            byte[] messageBodyBytes = ("Sent Message # " + i).getBytes();
            if (channel != null) {
                channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            channel.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        conn.close();
    }
}
