package android.example.weatherwizard;

public class City {
    private double temp;
    private double max_temp;
    private double min_temp;
    private String weather;
    private String location;
    private double pressure;
    private double humidity;

    public City(String location,String weather,double temp,double max_temp,double min_temp,double humidity,double pressure){
        this.location=location;
        this.weather=weather;
        this.temp=temp;
        this.max_temp=max_temp;
        this.min_temp=min_temp;
        this.humidity=humidity;
        this.pressure=pressure;
    }

    public String getLocation(){
        return location;
    }
    public String getWeather(){
        return weather;
    }
    public double getTemp(){
        return temp;
    }
    public double getMaxtemp(){
        return max_temp;
    }
    public double getMintemp(){
        return min_temp;
    }
    public double getHumidity(){
        return humidity;
    }
    public double getPressure(){
        return pressure;
    }

}
