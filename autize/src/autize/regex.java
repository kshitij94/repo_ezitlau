package autize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regex {
	public static void main(String [] args)
	{
		String data = "https://192.168.9.90/download/l-1-4-1&-1";
		String pattern = "[\\w\\d]+-[\\w\\d]+-[\\w\\d]+-[\\w\\d]+";

	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);
int count = 0;
	      // Now create matcher object.
	      Matcher m = r.matcher(data);
	      while(m.find())
	      {
	    	  System.out.println("count "+count);
	    	  System.out.println("start " + m.start());
	    	  System.out.println("end " + m.end());
	    	  System.out.println();
	    	  
	    	  System.out.println("pattern matched" +data.subSequence(m.start(), m.end()));
	    	 count++;
	    	  
	      }
	}
}
