package android.example.weatherwizard;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InformationActivity extends AppCompatActivity {
    TextView mText;
    TextView placetext;
    TextView currtemp;
    TextView maxtemp;
    TextView mintemp;
    TextView mhumidty;
    TextView mpressure;
    TextView weather_desc;
    TextView mweather_info;
    ImageView weatherpic;
    TextView msunrise;
    TextView msunset;
    TextView mcloud;
    TextView mwind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inforation);
        placetext=findViewById(R.id.info_location);
        currtemp=findViewById(R.id.curr_temp);
        maxtemp=findViewById(R.id.maxtemp);
        mintemp=findViewById(R.id.mintemp);
        weatherpic=findViewById(R.id.weather_pic);
        mhumidty=findViewById(R.id.info_humidity);
        mpressure=findViewById(R.id.info_pressure);
        weather_desc=findViewById(R.id.weather_desc);
        mweather_info=findViewById(R.id.weather_info);
        msunrise=findViewById(R.id.sunrise);
        msunset=findViewById(R.id.sunset);
        mcloud=findViewById(R.id.cloud_info);
        mwind=findViewById(R.id.wind_info);
        Intent intent = getIntent();
        String value = intent.getStringExtra("location");
        String gpspara=intent.getStringExtra("gps");
        String latitude=""+intent.getDoubleExtra("latitude",0.0);
        String longitude=""+intent.getDoubleExtra("longitude",0.0);
        new FetchData().execute(value,gpspara,latitude,longitude);


    }

    public class FetchData extends AsyncTask<String, Void, Void> {
        String result = "";
        boolean flag = true;

        String curr_weather,location,humidty,pressure,weather_desc_long,sunrise,sunset;
        double curr_temp,max_temp,min_temp,cloudquantity,windspeed,windangle;
        Date rise,set;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            }

        @Override
        protected Void doInBackground(String... params) {
            try {
                JSONObject root;
                JSONArray array;
                JSONObject object1,object2,object3,cloud,wind;
                URL url;
                String loc;
                String gps;
                String lat,longi;
                loc = params[0];
                gps=params[1];
                lat=params[2];
                longi=params[3];
                if(gps.matches("true")){
                    url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon="+longi+"&APPID=a1e2a15fb9d52c5011d32ef1ee047782");
                }
                else {
                    url=new URL("https://api.openweathermap.org/data/2.5/weather?q=" + loc + "&APPID=a1e2a15fb9d52c5011d32ef1ee047782");
                }
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() != 200) {
                    flag = false;
                } else {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while (line != null) {
                        line = bufferedReader.readLine();
                        result = result + line;
                    }
                    root=new JSONObject(result);
                    array= root.getJSONArray("weather");
                    object1= array.getJSONObject(0);
                    object2= root.getJSONObject("main");
                    object3=root.getJSONObject("sys");
                    cloud=root.getJSONObject("clouds");
                    wind=root.getJSONObject("wind");

                    location=root.getString("name");
                    curr_weather=object1.getString("main");
                    weather_desc_long=object1.getString("description").toUpperCase();
                    curr_temp=Math.round(object2.getDouble("temp")-273.15);
                    max_temp=Math.round(object2.getDouble("temp_max")-273.15);
                    min_temp=Math.round(object2.getDouble("temp_min")-273.15);
                    humidty=object2.getString("humidity");
                    pressure=object2.getString("pressure");
                    cloudquantity=cloud.getInt("all");
                    windspeed=wind.getDouble("speed");
                    windangle=wind.getDouble("deg");

                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
                    rise=new Date(object3.getLong("sunrise")*1000);
                    set=new Date(object3.getLong("sunset")*1000);
                    sunrise=sdf.format(rise);
                    sunset=sdf.format(set);

                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (flag) {
                placetext.setText(location);
                weather_desc.setText(curr_weather);
                currtemp.setText(""+curr_temp+"\u2103");
                maxtemp.setText(""+max_temp+"\u2103");
                mintemp.setText(""+min_temp+"\u2103");
                mhumidty.setText("Humidity: "+humidty+"%");
                mpressure.setText("Pressure:"+pressure+"mb");
                mweather_info.setText(weather_desc_long);
                msunrise.setText(sunrise);
                msunset.setText(sunset);
                mcloud.setText(""+cloudquantity+"%\nCloudiness");
                mwind.setText("Wind Speed: "+windspeed+"m/s\n\nWind Angle: "+windangle+"\u00B0");

                if(curr_weather.matches("Thunderstorm")){
                    weatherpic.setImageResource(R.drawable.thunderstorm);
                }
                else if(curr_weather.matches("Drizzle")){
                    weatherpic.setImageResource(R.drawable.drizzle);
                }
                else if(curr_weather.matches("Rain")){
                    weatherpic.setImageResource(R.drawable.rain);
                }
                else if(curr_weather.matches("Snow")){
                    weatherpic.setImageResource(R.drawable.snow);
                }
                else if(curr_weather.matches("Dust")||curr_weather.matches("Mist")||curr_weather.matches("Smoke")||curr_weather.matches("Haze")||curr_weather.matches("Fog")||curr_weather.matches("Ash")||curr_weather.matches("Squall")||curr_weather.matches("Tornado")){
                    weatherpic.setImageResource(R.drawable.haze);
                }
                else if(curr_weather.matches("Clear")){
                    weatherpic.setImageResource(R.drawable.clear);
                }
                else if(curr_weather.matches("Clouds")){
                    weatherpic.setImageResource(R.drawable.clouds);
                }
                else{
                    weatherpic.setImageResource(R.drawable.sunny);
                }

            } else {
                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        }
    }

}
