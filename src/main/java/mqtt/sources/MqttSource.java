package mqtt.sources;

import mqtt.MqttConstants;
import org.apache.flink.api.common.functions.StoppableFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MqttSource implements SourceFunction<String>, StoppableFunction {

    private transient IMqttClient client;
    private transient volatile boolean running;
    private transient Object waitLock;

    @Override
    public void stop() {
        close();
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        client = new MqttClient(MqttConstants.SERVER_URI, MqttConstants.SUBSCRIBER_ID);
        client.connect();

        client.subscribe(MqttConstants.TOPIC, (topic, message) -> {
            String msq = new String(message.getPayload(), StandardCharsets.UTF_8);
            sourceContext.collect(msq);
        });

        running = true;
        waitLock = new Object();

        while (running) {
            synchronized (waitLock) {
                waitLock.wait(100L);
            }
        }
    }

    @Override
    public void cancel() {
        close();
    }

    private void close() {
        try {
            if (Objects.nonNull(client)) {
                client.disconnect();
            }
        } catch (MqttException e) {
            System.out.println(e.getMessage());
        } finally {
            running = false;
        }

        synchronized (waitLock) {
            waitLock.notify();
        }
    }
}
