package ro.pub.cs.systems.eim.practicaltest2;

public class Information {
    public Information(String temperature, String wind_speed, String humidity, String feels_like, String pressure) {
        this.temperature = temperature;
        this.wind_speed = wind_speed;
        this.humidity = humidity;
        this.feels_like = feels_like;
        this.pressure = pressure;
    }

    private String temperature;
    private String wind_speed;
    private String humidity;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(String feels_like) {
        this.feels_like = feels_like;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    private String feels_like;
    private String pressure;
}
