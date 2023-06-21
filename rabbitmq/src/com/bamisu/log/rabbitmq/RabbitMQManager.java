package com.bamisu.log.rabbitmq;

import com.bamisu.log.rabbitmq.entities.Configs;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class RabbitMQManager {
    private Channel channel;


    private static RabbitMQManager instance = new RabbitMQManager();
    private RabbitMQManager() {
        this.init();
    }

    public static RabbitMQManager getInstance() {
        return instance;
    }

    public static void resetConnection() {
        instance = new RabbitMQManager();
    }

    private void init() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = null;
            try {
                String currentDir = System.getProperty("user.dir");
                inputStream = Files.newInputStream(Paths.get(currentDir + "/conf/rabbitmq.properties"));
                properties.load(inputStream);
                System.out.println(properties.getProperty("username"));
                System.out.println(properties.getProperty("password"));
                // init connection
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(properties.getProperty("host"));
                factory.setUsername(properties.getProperty("username"));
                factory.setPassword(properties.getProperty("password"));
                factory.setVirtualHost("/");
                Connection connection = factory.newConnection();
                this.channel = connection.createChannel();
                // declare exchange
                channel.exchangeDeclare(properties.getProperty("exchange"), BuiltinExchangeType.DIRECT, true);
                // declare queues
                this.channel.queueDeclare(properties.getProperty("queue"), false, false, false, null);
                this.channel.queueDeclare(properties.getProperty("marketQueue"), false, false, false, null);
                // binding queue
                this.channel.queueBind(properties.getProperty("queue"), properties.getProperty("exchange"), properties.getProperty("routingKey"));
                this.channel.queueBind(properties.getProperty("marketQueue"), properties.getProperty("exchange"), properties.getProperty("marketRoutingKey"));
//                this.channel.queueBind(Configs.MARKET_QUEUE, Configs.EXCHANGE_NAME, Configs.MARKET_ROUTING_KEY);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel() {
        return this.channel;
    }
}
