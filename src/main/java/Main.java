import chart.Chart;
import flink.Analyzer;

public class Main {
    public static void main(String[] args) throws Exception {
        Chart.initChart();
        Analyzer.analyse();
        System.exit(0);
    }
}
