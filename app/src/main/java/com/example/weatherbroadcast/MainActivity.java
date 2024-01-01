package com.example.weatherbroadcast;

import android.util.JsonReader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.Formatter;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
  static final String outputText =
          "%s\n"
          + "Temperature: %.1fº\n"
          + "Temperature feels like: %.1fº\n"
          + "Wind speed: %.2f kph\n"
          + "Pressure: %.2f mm\n"
          + "Humidity: %d %% \n" // %
          + "Cloud: %d %% \n" // %
          + "UV index: %.2f";
  TextView weatherTW;
  TextView cityTW;
  Button startButton;
  EditText inputET;
  ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    weatherTW = findViewById(R.id.weatherInfo);
    cityTW = findViewById(R.id.cityName);
    startButton = findViewById(R.id.butt);
    inputET = findViewById(R.id.et);
    imageView = findViewById(R.id.IV);

    registerReceiver(receiver, new IntentFilter("MeteoService"), RECEIVER_EXPORTED);

    Intent intent = new Intent(this, MeteoService.class);
    startService(intent);

    startButton.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String city = inputET.getText().toString();
            Intent intent =
                new Intent(MainActivity.this, MeteoService.class).putExtra("CITY", city);
            startService(intent);
            cityTW.setText(city);
          }
        });
  }

  BroadcastReceiver receiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          Log.d("RESULT", intent.getStringExtra("INFO"));
          Formatter formatter = new Formatter();
          String str = intent.getStringExtra("INFO");
          try {
            JSONObject start = new JSONObject(str);
            JSONObject current = start.getJSONObject("current");

            double temp = current.getDouble("temp_c");
            double feelslikeTemp = current.getDouble("feelslike_c");
            double windKph = current.getDouble("wind_kph");
            double pressureMm = current.getDouble("precip_mm");
            int humidity = current.getInt("humidity");
            int cloud = current.getInt("cloud");
            double uv = current.getDouble("uv");

            JSONObject condition = current.getJSONObject("condition");
            String conditionText = condition.getString("text");
            String iconURL = condition.getString("icon");

            Glide.with(MainActivity.this).load("https:" + iconURL).into(imageView);
            formatter.format(
                outputText, conditionText, temp, feelslikeTemp, windKph, pressureMm, humidity, cloud, uv);
            weatherTW.setText(formatter.toString());
            formatter.close();
          } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
          }
        }
      };

  @Override
  protected void onPause() {
    super.onPause();

    Intent intent = new Intent(this, MeteoService.class);
    stopService(intent);
  }
}
