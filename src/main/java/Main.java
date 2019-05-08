import flink.Analyzer;

public class Main {
    public static void main(String[] args) throws Exception {
        Analyzer.initChart();
        Analyzer.analyse();
        System.exit(0);
    }
}
