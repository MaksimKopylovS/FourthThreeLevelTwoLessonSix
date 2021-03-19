package max_sk.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;


public class Reader {
    private static final String EXCHANGE_NAME = "it_blog";
    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    private static Scanner scanner;
    private static String routingKey;
    private static Thread thread;
    private static DeliverCallback deliverCallback;
    private static Channel channel;
    private static String queueName;
    private static String unchekedName;


    public static void main(String[] argv) throws Exception {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        queueName = channel.queueDeclare().getQueue();
        System.out.println("Введине название темы: ");
        scanner = new Scanner(System.in);
        routingKey = scanner.nextLine();
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        System.out.println("Ожидаем новый блог с подпиской " + routingKey );
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("Введите новую тему: ");
                        scanner = new Scanner(System.in);
                        routingKey = scanner.nextLine();

                        System.out.println("введите тему для отписания: ");
                        scanner = new Scanner(System.in);
                        unchekedName = scanner.nextLine();
                        channel.queueUnbind(queueName,EXCHANGE_NAME, unchekedName);

                        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
                        System.out.println("Ожидаем новый блог с подпиской " + routingKey );
                        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Полученное сообщение \n" + "Тема: " + delivery.getEnvelope().getRoutingKey() + "\n Сообщение: " + message);

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
        thread.start();


    }


}

