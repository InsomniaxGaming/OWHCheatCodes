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
	
	public static String SERIALIZED_UNIT="unit",SERIALIZED_amount="amount";
	
	int amount;
	
	int calendarUnit;
	
	public Limit(int amount, Unit unit)
	{
		this.amount = amount;
		
		switch(unit)
		{
		case MILLISECONDS: this.calendarUnit = Calendar.MILLISECOND; break;
		case SECONDS: this.calendarUnit = Calendar.SECOND; break;
		case MINUTES: this.calendarUnit = Calendar.MINUTE; break;
		case HOURS: this.calendarUnit = Calendar.HOUR_OF_DAY; break;
		case DAYS: this.calendarUnit = Calendar.DAY_OF_YEAR; break;
		case WEEKS: this.calendarUnit = Calendar.WEEK_OF_YEAR; break;
		case MONTHS: this.calendarUnit = Calendar.MONTH; break;
		case YEARS: this.calendarUnit = Calendar.YEAR; break;
		}
	}
	
	public Limit(Map<String, Object> map)
	{
		this.amount = (int) map.get(SERIALIZED_amount);
		this.calendarUnit = (int) map.get(SERIALIZED_UNIT);
	}
	
	public boolean passesLimit(Calendar calendar)
	{
		if(calendar == null)
			return true;
		else
		{
			calendar.add(calendarUnit, amount);
			return calendar.before(Calendar.getInstance()); // If not never and not forever, check if the limit is before current time
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(SERIALIZED_UNIT,calendarUnit);
		map.put(SERIALIZED_amount, amount);
		return map;
	}
}
