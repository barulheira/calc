package st.wit.calc;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CalcServer {
    
    private static final Logger logger = LoggerFactory.getLogger(CalcServer.class);
    private static final String RPC_QUEUE_NAME = "calc_queue_";
    
    public CalcServer(@Value("${st.wit.rabbitmq.host}") String hostName) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostName);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            logger.info("Awaiting RPC requests");

            Object monitor = new Object();

            for(Calculator calc : Calculator.values()) {
                String queueName = RPC_QUEUE_NAME+calc;
                channel.queueDeclare(queueName, false, false, false, null);
                channel.queuePurge(queueName);
                channel.basicConsume(queueName, false,
                    new Cbk(monitor, channel, calc), (consumerTag -> { }));
                
            }
            channel.basicQos(1);

            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        logger.error("Monitor was interrupted");
                    }
                }
            }
        }
    }

    private static class Cbk implements DeliverCallback {

        private final Object monitor;
        private final Channel channel;
        private final Calculator calculator;

        public Cbk(Object monitor, Channel channel, Calculator calculator) {
            this.monitor = monitor;
            this.channel = channel;
            this.calculator = calculator;
        }

        @Override
        public void handle(String consumerTag, Delivery delivery) throws IOException {
            final String correlationId = delivery.getProperties().getCorrelationId();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(correlationId)
                    .build();
            
            String response = "";
            
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                String[] parts = message.split("\\t");
                response = calculator.calc(new BigDecimal(parts[0]),
                        new BigDecimal(parts[1])).toString();
                
                logger.info("Correlation-Id: "+correlationId + " " + calculator + ": "+response);
            } catch (RuntimeException e) {
                logger.error("Calculator error", e);
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
                synchronized (monitor) {
                    monitor.notify();
                }
            }
        }
    }
}