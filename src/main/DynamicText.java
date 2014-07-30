package main;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DynamicText {
	
	public static String translate(String text){
		String txt = text;
		Pattern datePattern = Pattern.compile("\\$DATE\\$", Pattern.CASE_INSENSITIVE);
		Matcher dateMatcher = datePattern.matcher(txt);
		txt = dateMatcher.replaceAll(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		
		return txt;
	}
}
