package mqtt.parser;

import model.WeatherInfo;

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
            list.add(new WeatherInfo(Double.parseDouble(unboxValue(findPropertyInDictionary(split1, "temp"))),
                    Double.parseDouble(unboxValue(findPropertyInDictionary(split1, "humidity"))),
                    unboxValue(findPropertyInDictionary(split1, "localisation")),
                    unboxValue(findPropertyInDictionary(split1, "type"))));
        });
        return list;
    }

    private static String findPropertyInDictionary(String[] split, String property) {
        return Arrays.stream(split).filter(x -> x.contains(property)).findAny().orElse("");
    }

    private static String unboxValue(String val) {
        return val.substring(val.indexOf(":") + 1);
    }
}
