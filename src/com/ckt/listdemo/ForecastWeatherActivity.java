package com.ckt.listdemo;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class ForecastWeatherActivity extends Activity {

	private static final int IS_FINISH = 0xFF;
	private static final String TAG = "myTAG";

	private City city = null;
	private String Url;

	private TextView tv_name;
	private TextView tv_longitude;
	private TextView tv_latitude;
	private TextView tv_weather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast_weather_activity);

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_longitude = (TextView) findViewById(R.id.tv_long);
		tv_latitude = (TextView) findViewById(R.id.tv_lati);
		tv_weather = (TextView) findViewById(R.id.tv_weather);

		String name;
		double longitude;
		double latitude;

		name = getIntent().getStringExtra("name");
		longitude = getIntent().getDoubleExtra("longitude", 0);
		latitude = getIntent().getDoubleExtra("latitude", 0);

		city = new City(name, longitude, latitude);
		tv_name.setText("city: " + city.getName().toString());
		tv_longitude.setText("longitude: " + city.getLongitude());
		tv_latitude.setText("tv_latitude: " + city.getLatitude());

		Url = new String(MainActivity.HOST
				+ "/ysphere-mosvc/service/y-box/weather/"
				+ String.valueOf(city.getLatitude()) + "/"
				+ String.valueOf(city.getLongitude()));
		Log.d(TAG, Url);
		new Thread(new MyThread()).start();
	}

	private class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(Url);
			HttpResponse response = null;

			try {
				response = client.execute(httpGet);
				Log.d(TAG, "ForecastWeatherActivity Runnable "
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					byte[] data = EntityUtils.toByteArray(response.getEntity());
					Message msg = Message.obtain();
					msg.obj = data;
					msg.what = IS_FINISH;
					handler.sendMessage(msg);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			byte[] data = (byte[]) msg.obj;
			if (msg.what == IS_FINISH) {
				String mStr = EncodingUtils.getString(data, "UTF-8");
				Log.d(TAG, "ForecastWeatherActivity handleMessage" + mStr);

				json_decode(mStr);

				String s = new String("\ncondition: "+String.valueOf(city.weather[0].getCondition())
						+"\ntemperature: "+String.valueOf(city.weather[0].getTemperature())
						+"\nminTemperature: "+String.valueOf(city.weather[0].getMinTemp())
						+"\nmaxTemperature: "+String.valueOf(city.weather[0].getMaxTemp()));
				
				Log.d(TAG, "s");
				tv_weather.setText(s);
			}
			super.handleMessage(msg);
		}
	};

	private void json_decode(String content) {
		try {
			int condition;
			double temperature;
			double minTemp;
			double maxTemp;
			JSONObject jObject[] = new JSONObject[4];
			
			jObject[0] = (new JSONObject(content)).getJSONObject("now");
			condition = jObject[0].getInt("condition");
			temperature = jObject[0].getDouble("temperature");
			minTemp = jObject[0].getDouble("minTemperature");
			maxTemp = jObject[0].getDouble("maxTemperature");
			city.setWeather(0, condition, temperature, minTemp, maxTemp);

			for (int i = 1; i <= 3; i++) {
				jObject[i] = (new JSONObject(content)).getJSONObject(new String("now+"+String.valueOf(i)));
				condition = jObject[0].getInt("condition");
				temperature = jObject[0].getDouble("temperature");
				minTemp = jObject[0].getDouble("minTemperature");
				maxTemp = jObject[0].getDouble("maxTemperature");
				city.setWeather(i, condition, temperature, minTemp, maxTemp);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
