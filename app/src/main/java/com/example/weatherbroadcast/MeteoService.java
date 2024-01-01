package com.example.weatherbroadcast;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MeteoService extends Service {

  Handler handler;

  @Override
  public void onCreate() {
    super.onCreate();
    handler = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        String str = (String) msg.obj;
        Intent intent = new Intent("MeteoService");
        intent.putExtra("INFO", str);
        sendBroadcast(intent);
      }
    };
    Toast.makeText(this, "Сервис создан!", Toast.LENGTH_SHORT).show();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String city = intent.getStringExtra("CITY");
    Thread weatehrThread = new Thread(new HttpsRequest(handler, city));
    weatehrThread.start();
    Toast.makeText(this, "Сервис запущен!", Toast.LENGTH_SHORT).show();

    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    Toast.makeText(this, "Сервис уничтожен!", Toast.LENGTH_SHORT).show();

    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
