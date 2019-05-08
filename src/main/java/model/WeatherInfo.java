package model;

public class WeatherInfo {
    private double temp;
    private double humidity;
    private String localisation;
    private String type;

    public WeatherInfo(double temp, double humidity, String localisation, String type) {
        this.temp = temp;
        this.humidity = humidity;
        this.localisation = localisation;
        this.type = type;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
