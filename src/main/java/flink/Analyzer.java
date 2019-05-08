package flink;

import chart.Chart;
import model.WeatherInfo;
import mqtt.parser.MessageParser;
import mqtt.sources.MqttSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.*;

public class Analyzer {

    public static void analyse() throws Exception {
        MqttSource mqttSource = new MqttSource();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        DataStreamSource<String> dataStreamSource = env.addSource(mqttSource);
        SingleOutputStreamOperator<List<WeatherInfo>> streamList = dataStreamSource
                .map((MapFunction<String, List<WeatherInfo>>) MessageParser::parse);
        streamList.print();
        streamList.map((MapFunction<List<WeatherInfo>, String>) Chart::updateChart);

        env.execute("Analysis");
    }

}
