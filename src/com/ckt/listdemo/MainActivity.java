package com.ckt.listdemo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	public static final String HOST = "http://www.doroexperience.com";
	private static final String TAG = "myTAG";

	private ListView lv;
	private ArrayList<String> lv_list = new ArrayList<String>();
	private City city[];

	private static final int IS_FINISH = 0x55;
	private static final String URL = "http://www.doroexperience.com/ysphere-mosvc/service/y-box/weather/search/cd";// "http://api.eoe.cn/client/blog?k=lists&t=top";

	// private static final String[] strs = new String[] { "aaaaaaaaaaaaaaaaa",
	// "bbbbbbbbbbbbbbbbb", "ccccccccccccccccc", "ddddddddddddddddd",
	// "eeeeeeeeeeeeeeeee", "fffffffffffffffff" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lv = (ListView) findViewById(R.id.lv);

		new Thread(new MyThread()).start();
	}

	public class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL);
			HttpResponse response = null;

			try {
				response = client.execute(httpGet);
				Log.d(TAG, "Runnable");
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
				Log.d(TAG, "handleMessage" + mStr);

				lv_list = json_decode(mStr);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						lv_list);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Log.d(TAG, "click on item:" + position + ", " + city[position].getName());
						Intent intent = new Intent(MainActivity.this, ForecastWeatherActivity.class);
						intent.putExtra("name", city[position].getName());
						intent.putExtra("longitude", city[position].getLongitude());
						intent.putExtra("latitude", city[position].getLatitude());
						startActivity(intent);
					}
				});
				super.handleMessage(msg);
			}
		}
	};

	private ArrayList<String> json_decode(String content) {
		String city_name;
		double city_longitude;
		double city_latiude;
		
		try {
			JSONArray jArray = new JSONArray(content);
			int arrayLength = jArray.length();
			city = new City[arrayLength];

			for (int i = 0; i < arrayLength; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				
				city_name = obj.getString("name").toString();
				city_longitude = obj.getDouble("longitude");
				city_latiude = obj.getDouble("latitude");
				city[i] = new City(city_name, city_longitude, city_latiude);
				
				lv_list.add(city_name);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lv_list;
	}
}
