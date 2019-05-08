package mqtt;

import flink.WeatherInfo;

import java.util.*;

public class MessageParser {

    public static List<WeatherInfo> parse(String text) {
        String cleanText = text.replaceAll("\\[", "").replaceAll("]", "")
                .replaceAll("'", "").replaceAll(" ", "");
        String[] split = cleanText.split("},\\{");
        List<WeatherInfo> list = new ArrayList<>();
        Arrays.stream(split).forEach(x -> {
            String clean = x.replaceAll("\\{", "").replaceAll("}", "");
            String[] split1 = clean.split(",");
            // python dictionary is unordered, we have to read parameters directly
            String type = getParameter(split1, "type");
            String localisation = getParameter(split1, "localisation");
            String temperature = getParameter(split1, "temp");
            String humidity = getParameter(split1, "humidity");

            list.add(new WeatherInfo(Double.parseDouble(unboxValue(temperature)), Double.parseDouble(unboxValue(humidity)), unboxValue(localisation),  unboxValue(type)));
        });
        return list;
    }

    private static String getParameter(String[] split, String param) {
        for (String str: split) {
            if (str.contains(param)) {
                return str;
            }
        }
        return ""; // this solution is not safe
    }

    private static String unboxValue(String val) {
        return val.substring(val.indexOf(":") + 1);
    }
}
