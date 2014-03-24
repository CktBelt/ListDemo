package com.ckt.listdemo;

public class City {

	private String name;
	private double longitude;
	private double latitude;
	public Weather weather[] = new Weather[4];

	City(String name, double city_longitude, double city_latiude) {
		this.name = name;
		this.longitude = city_longitude;
		this.latitude = city_latiude;
	}

	public String getName() {
		return this.name;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setWeather(int index, int condition, double Temperature, double minTemperature,
			double maxTemperature) {
		weather[index] = new Weather(condition, Temperature, minTemperature, maxTemperature);
	}

	public class Weather {

		private int condition;
		private double Temperature;
		private double minTemperature;
		private double maxTemperature;

		Weather(int condition, double Temperature, double minTemperature, double maxTemperature) {
			this.condition = condition;
			this.Temperature = Temperature;
			this.minTemperature = minTemperature;
			this.maxTemperature = maxTemperature;
		}
		
		public int getCondition(){
			return this.condition;
		}
		
		public double getTemperature(){
			return this.Temperature;
		}
		
		public double getMinTemp(){
			return this.minTemperature;
		}
		
		public double getMaxTemp(){
			return this.maxTemperature;
		}
	}
	
}
