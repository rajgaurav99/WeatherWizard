package android.example.weatherwizard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    public static List<City> cityList=new ArrayList<City>();
    RecyclerView recyclerView;
    SearchView msearch;
    boolean geo=false;

    protected LocationManager locationManager;
    public static double latitude=-1.0, longitude=-1.0;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cityList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msearch=findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        new FetchData().execute("New Delhi");
        new FetchData().execute("Mumbai");
        new FetchData().execute("Bangalore");
        new FetchData().execute("Kolkata");
        new FetchData().execute("vellore");


        CityAdapter mAdapter = new CityAdapter(this,cityList);
        recyclerView.setAdapter(mAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(geo){
                    Intent intent=new Intent(MainActivity.this,InformationActivity.class);
                    intent.putExtra("gps","true");
                    intent.putExtra("location","false");
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(view, "GPS functionality not yet added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        msearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent=new Intent(MainActivity.this,InformationActivity.class);
                intent.putExtra("location", query);
                intent.putExtra("gps","false");
                intent.putExtra("latitude",latitude);
                intent.putExtra("latitude",longitude);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        } );


    }

    @Override
    public void onLocationChanged (Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        geo=true;
    }

    @Override
    public void onProviderDisabled (String provider){
        Log.d("Latitude", "disable");
    }


    @Override
    public void onProviderEnabled (String provider){
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged (String provider,int status, Bundle extras){
        Log.d("Latitude", "status");
    }

    public class FetchData extends AsyncTask<String, Void, City> {
        boolean flag = true;
        String result = "";
        City backcity;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected City doInBackground(String... params) {

            try {
                JSONObject root;
                JSONArray array;
                JSONObject object1,object2;
                URL url;
                String loc;
                loc = params[0];
                url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + loc + "&APPID=a1e2a15fb9d52c5011d32ef1ee047782");

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
                    backcity=new City(root.getString("name"),object1.getString("main"),Math.round(object2.getDouble("temp")-273.15), Math.round(object2.getDouble("temp_max")-273.15),Math.round(object2.getDouble("temp_min")-273.15),Double.parseDouble(object2.getString("humidity")), Double.parseDouble(object2.getString("pressure")));
                    return backcity;

                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
            return backcity;
        }
        @Override
        protected void onPostExecute(City city) {
            super.onPostExecute(city);
            if (flag) {
                cityList.add(city);
            } else {
                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
