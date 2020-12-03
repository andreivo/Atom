
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andre
 */
public class AMQPLib {

    //
    private final ConnectionFactory factory;

    public AMQPLib(String userName, String virtualHost, String password, String host) {
        factory = new ConnectionFactory();
        factory.setUsername(userName);
        factory.setVirtualHost(virtualHost);
        factory.setPassword(password);
        factory.setHost(host);
    }

    public String createQueue(String queueName, String exchangeName, String routingKey) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            //DeclareQueue: Ensuring that the queue exists
            channel.queueDeclare(queueName, true, false, false, null);

            //Defines Exchange, responsible for distributing messages in the queues
            channel.exchangeDeclare(exchangeName, "direct", true);

            //Defines the key routes that forward messages to the queue
            channel.queueBind(queueName, exchangeName, routingKey);

            return "The queue has been created!";
        } catch (IOException e) {
        } catch (TimeoutException e) {
            return "CreateQueue Error: " + e.getMessage();
        }
        return "Unknow Error";
    }

    public String publishMessage(String deliveryType, String exchangeName, String routingKey, String message) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            //Defines Exchange, responsible for distributing messages in the queues
            channel.exchangeDeclare(exchangeName, deliveryType, true);

            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
            return "Message is published in '" + routingKey + "' : '" + message + "'";

        } catch (IOException e) {
        } catch (TimeoutException e) {
            return "PublishMessage Error: " + e.getMessage();
        }
        return "Unknow Error";
    }

}
