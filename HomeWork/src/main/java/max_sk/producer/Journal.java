package max_sk.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Journal {
    private static final String EXCHANGE_NAME = "it_blog";
    private static ConnectionFactory factory;
    private static Scanner scanner;

    public static void main(String[] argv) throws Exception {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            System.out.println("Введите тему: \n");
            scanner = new Scanner(System.in);
            String routingKey = scanner.nextLine();
            System.out.println("Введите сообщение: \n");
            String message = scanner.nextLine();
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Сообщение (" + message  + ") с ключём " + routingKey + " отправленно" );
        }
    }
}
