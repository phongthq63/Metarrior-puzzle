package com.bamisu.log.gameserver.base;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.log.gameserver.RabbitMQHandler;
import com.bamisu.log.rabbitmq.RabbitMQManager;
import com.bamisu.log.rabbitmq.entities.Configs;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import org.apache.log4j.Logger;

public class OnServerReadyHandler extends BaseServerEventHandler {
    @Override
    public void handleServerEvent(ISFSEvent isfsEvent) {
        BaseExtension extension = (BaseExtension) getParentExtension();
        extension.onServerReady();
        Logger logger = Logger.getLogger(OnServerReadyHandler.class);
        try {
            RabbitMQManager.getInstance().getChannel().basicConsume(Configs.GAME_QUEUE, true, ((consumerTag, message) -> {
                String body = new String(message.getBody());
                String cmdId = message.getProperties().getHeaders().get("cmdId").toString();
                logger.info(cmdId + ": " + body);
                RabbitMQHandler.getInstance().onMessage(body, cmdId);
            }), System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }
}
