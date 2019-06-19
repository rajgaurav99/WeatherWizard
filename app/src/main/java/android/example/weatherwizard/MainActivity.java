package android.example.weatherwizard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static List<City> cityList=new ArrayList<City>();
    RecyclerView recyclerView;
    SearchView msearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cityList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msearch=findViewById(R.id.search_bar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                Snackbar.make(view, "GPS functionality not yet added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        } );


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
