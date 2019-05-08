package flink;

import mqtt.MqttConstants;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import mqtt.MessageParser;
import mqtt.sources.MqttSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.knowm.xchart.XYChartBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Analyzer {

    private static XYChart chart;
    private static SwingWrapper<XYChart> sw;
    private static Map<String, Queue<Double>> xQueueMap = new HashMap<>();
    private static Map<String, Queue<Double>> yQueueMap = new HashMap<>();
    private static final List<String> CITIES = Arrays.asList("London", "KrakowRPI", "Stockholm");

    public static void initChart() {
        chart = new XYChartBuilder()
                .width(1900)
                .height(900)
                .title("Humidity analysis")
                .build();

        LinkedList<Double> yValues = Stream.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                .map(Double::valueOf)
                .collect(Collectors.toCollection(LinkedList::new));
        LinkedList<Double> xValues = Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                .map(Double::valueOf)
                .collect(Collectors.toCollection(LinkedList::new));

        CITIES.forEach(city -> {
            xQueueMap.put(city, Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                    .map(Double::valueOf)
                    .collect(Collectors.toCollection(LinkedList::new)));
            yQueueMap.put(city, Stream.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                    .map(Double::valueOf)
                    .collect(Collectors.toCollection(LinkedList::new)));
            chart.addSeries(city, xValues, yValues);
        });

        sw = new SwingWrapper<>(chart);
        sw.displayChart();
    }

    public static void analyse() throws Exception {
        MqttSource mqttSource = new MqttSource();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        DataStreamSource<String> dataStreamSource = env.addSource(mqttSource);
        SingleOutputStreamOperator<List<WeatherInfo>> streamList = dataStreamSource
                .map((MapFunction<String, List<WeatherInfo>>) MessageParser::parse);
        streamList.print();
        streamList.map((MapFunction<List<WeatherInfo>, String>) Analyzer::updateChart);

        env.execute("Analysis");
    }

    private static String updateChart(List<WeatherInfo> list) {
        CITIES.forEach(city -> list.stream()
                .filter(info -> info.getLocalisation().contains(city))
                .forEach(info -> {
                    if (MqttConstants.CHART_PROPERTY.equals("temp")) {
                        updateSerie(city, info, WeatherInfo::getTemp);
                    } else {
                        updateSerie(city, info, WeatherInfo::getHumidity);
                    }
                }));
        sw.repaintChart();

        return null;
    }

    private static void updateSerie(String name, WeatherInfo info, Function<WeatherInfo, Double> function) {
        Queue<Double> yValues = yQueueMap.get(name);
        yValues.add(function.apply(info));
        yValues.poll();
        Queue<Double> xValues = xQueueMap.get(name);
        xValues.add(xValues.poll() + 20.0);

        chart.updateXYSeries(name, getDoubles(xValues), getDoubles(yValues), null);
    }

    private static List<Double> getDoubles(Queue<Double> queue) {
        return new ArrayList<>(queue);
    }
}
