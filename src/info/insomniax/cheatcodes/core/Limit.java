package info.insomniax.cheatcodes.core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Limit")
public class Limit implements ConfigurationSerializable{
	
	public enum Unit
	{
		MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS,WEEKS,MONTHS,YEARS
	}
	
	public static String SERIALIZED_DURATION="duration",SERIALIZED_amount="amount";
	
	public static int NEVER=-1,FOREVER=-2;
	
	int amount;
	
	int unit;
	
	public Limit(int amount, Unit unit)
	{
		this.amount = amount;
		
		switch(unit){
		case MILLISECONDS: this.unit = Calendar.MILLISECOND; break;
		case SECONDS: this.unit = Calendar.SECOND; break;
		case MINUTES: this.unit = Calendar.MINUTE; break;
		case HOURS: this.unit = Calendar.HOUR_OF_DAY; break;
		case DAYS: this.unit = Calendar.DAY_OF_YEAR; break;
		case WEEKS: this.unit = Calendar.WEEK_OF_YEAR; break;
		case MONTHS: this.unit = Calendar.MONTH; break;
		case YEARS: this.unit = Calendar.YEAR; break;
		}
	}
	
	public Limit(Map<String, Object> map)
	{
		this.amount = (int) map.get(SERIALIZED_amount);
		this.unit = (int) map.get(SERIALIZED_DURATION);
	}
	
	public boolean passesLimit(Calendar calendar)
	{
		if(calendar == null)
			return true;
		else if(unit == NEVER)
			return true;
		else if(unit == FOREVER)
			return false;
		else
		{
			calendar.add(unit, amount);
			return calendar.before(Calendar.getInstance()); // If not never and not forever, check if the limit is before current time
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(SERIALIZED_DURATION,unit);
		map.put(SERIALIZED_amount, amount);
		return null;
	}
}
