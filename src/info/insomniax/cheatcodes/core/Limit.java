package info.insomniax.cheatcodes.core;

public class Limit {
	
	public enum Duration
	{
		MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS,WEEKS,MONTHS,YEARS,
		MILLISECOND,SECOND,MINUTE,HOUR,DAY,WEEK,MONTH,YEAR,FOREVER;
		
		public Duration getFromString(String value)
		{
			String withoutS = value;
			
			if(withoutS.endsWith("s") || withoutS.endsWith("S"))
				withoutS = value.substring(0,value.length()-1);
			
			for(Duration d : Duration.values())
			{
				if(d.toString().equalsIgnoreCase(value))
					return d;
				if(d.toString().equalsIgnoreCase(withoutS))
					return d;
			}
			return null;
		}
	}
	
	//All values in MS
	int millisecond = 1;
	int second = millisecond*1000;
	int minute = second*60;
	int hour = minute*60;
	int day = hour*24;
	int week = day*7;
	double month = week*4.34812;
	double year = month*12;
	
	int quantity;
	
	Duration unit;
	
	public Limit(int quantity, Duration unit)
	{
		this.quantity = quantity;
		this.unit = unit;
	}

}
