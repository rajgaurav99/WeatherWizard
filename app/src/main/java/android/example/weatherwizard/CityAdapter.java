package android.example.weatherwizard;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private Context mCtx;
    private List<City> cityList;
    public CityAdapter(Context mCtx, List<City> cityList) {
        this.mCtx = mCtx;
        this.cityList = cityList;
    }
    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_city, null);
        return new CityViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(CityViewHolder holder, final int position) {
        //getting the product of the specified position
        final City city = cityList.get(position);

        //binding the data with the viewholder views

        if(position==0){
            holder.mrel.setBackgroundResource(R.drawable.cardimage);
        }
        else if(position==1){
            holder.mrel.setBackgroundResource(R.drawable.cardimage2);
        }
        else if(position==2){
            holder.mrel.setBackgroundResource(R.drawable.cardimage3);
        }
        else if(position==3){
            holder.mrel.setBackgroundResource(R.drawable.cardimage4);
        }
        else{
            holder.mrel.setBackgroundResource(R.drawable.cardimage5);
        }
        holder.mlocation.setText(city.getLocation());
        holder.mtemp.setText(String.valueOf(city.getTemp())+"\u2103");
        holder.mmaxtemp.setText("Max:"+ String.valueOf(city.getMaxtemp())+"\u2103");
        holder.mmintemp.setText("Min:"+String.valueOf(city.getMintemp())+"\u2103");

        String temp=city.getWeather();
        holder.mweather.setText(city.getWeather());
        if(temp.matches("Thunderstorm")){
            holder.weather_pic.setImageResource(R.drawable.thunderstorm);
        }
        else if(temp.matches("Drizzle")){
            holder.weather_pic.setImageResource(R.drawable.drizzle);
        }
        else if(temp.matches("Rain")){
            holder.weather_pic.setImageResource(R.drawable.rain);
        }
        else if(temp.matches("Snow")){
            holder.weather_pic.setImageResource(R.drawable.snow);
        }
        else if(temp.matches("Dust")||temp.matches("Mist")||temp.matches("Smoke")||temp.matches("Haze")||temp.matches("Fog")||temp.matches("Ash")||temp.matches("Squall")||temp.matches("Tornado")){
            holder.weather_pic.setImageResource(R.drawable.haze);
        }
        else if(temp.matches("Clear")){
            holder.weather_pic.setImageResource(R.drawable.clear);
        }
        else if(temp.matches("Clouds")){
            holder.weather_pic.setImageResource(R.drawable.clouds);
        }
        else{
            holder.weather_pic.setImageResource(R.drawable.sunny);
        }


        holder.mrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InformationActivity.class);
                intent.putExtra("location",city.getLocation() );
                intent.putExtra("gps","false");
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder {

        TextView mlocation, mtemp,mmaxtemp,mmintemp,mweather;
        RelativeLayout mrel;
        ImageView weather_pic;


        public CityViewHolder(View itemView) {
            super(itemView);
            mrel=itemView.findViewById(R.id.background);
            mlocation = itemView.findViewById(R.id.info_location);
            mtemp = itemView.findViewById(R.id.current_temp);
            mmaxtemp = itemView.findViewById(R.id.max_temp);
            mmintemp = itemView.findViewById(R.id.min_temp);
            mweather=itemView.findViewById(R.id.current_weather);
            weather_pic=itemView.findViewById(R.id.small_weather);


        }
    }





}
