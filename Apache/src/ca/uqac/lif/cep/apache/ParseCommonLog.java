/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.apache;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.cep.apache.HttpRequest.Method;
import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Parses a line of an Apache log in the 
 * {@link https://httpd.apache.org/docs/1.3/logs.html common log format}
 * format and creates an {@link HttpRequest} object with it
 * @author Sylvain Hallé
 *
 */
public class ParseCommonLog extends UnaryFunction<String, HttpRequest>
{
	public static final ParseCommonLog instance = new ParseCommonLog();

	/**
	 * An instance of calendar to convert dates into timestamps
	 */
	protected final Calendar m_calendar = Calendar.getInstance();

	/**
	 * The pattern to parse a line of the log
	 */
	protected static final transient Pattern s_linePattern = Pattern.compile("([^\\s]+) ([^\\s]+) ([^\\s]+) \\[(.*?)\\] \"([^\"]*)\" (\\d+) (\\d+)");

	/**
	 * The pattern to parse a URL
	 */
	protected static final transient Pattern s_urlPattern = Pattern.compile("([^\\?]+)(\\?([^\\s]*)){0,1}");

	ParseCommonLog()
	{
		super(String.class, HttpRequest.class);
	}

	@Override
	public HttpRequest getValue(String log_line) 
	{
		Matcher line_mat = s_linePattern.matcher(log_line);
		if (line_mat.find())
		{
			String ip = line_mat.group(1);
			String inetd = line_mat.group(2);
			String userid = line_mat.group(3);
			String time_string = line_mat.group(4);
			String full_url = line_mat.group(5);
			int ret_code = Integer.parseInt(line_mat.group(6));
			int size = Integer.parseInt(line_mat.group(7));
			// Parse time string
			long parsed_time = parseTime(m_calendar, time_string);
			// Parse URL
			String[] url_parts = full_url.split(" ");
			Method m = stringToMethod(url_parts[0]);
			Matcher url_mat = s_urlPattern.matcher(url_parts[1]);
			Map<String,String> parameters = null;
			String path = "";
			if (url_mat.find())
			{
				path = url_mat.group(1);
				parameters = queryToMap(url_mat.group(3), m);
			}
			HttpRequest hr = new HttpRequest(ip, inetd, userid, parsed_time, m, path, parameters, ret_code, size);
			return hr;
		}
		return null;
	}

	public static long parseTime(Calendar calendar, String time_string)
	{
		String[] parts = time_string.split("[/ :]");
		int day = Integer.parseInt(parts[0]);
		int month = stringToMonth(parts[1]);
		int year = Integer.parseInt(parts[2]);
		int hours = Integer.parseInt(parts[3]);
		int minutes = Integer.parseInt(parts[4]);
		int seconds = Integer.parseInt(parts[5]);
		calendar.set(year, month, day, hours, minutes, seconds);
		return calendar.getTime().getTime() / 1000;
	}
	
	public static int stringToMonth(String m)
	{
		if (m.compareTo("Jan") == 0)
			return 1;
		if (m.compareTo("Feb") == 0)
			return 1;
		if (m.compareTo("Mar") == 0)
			return 1;
		if (m.compareTo("Apr") == 0)
			return 1;
		if (m.compareTo("May") == 0)
			return 1;
		if (m.compareTo("Jun") == 0)
			return 1;
		if (m.compareTo("Jul") == 0)
			return 1;
		if (m.compareTo("Aug") == 0)
			return 1;
		if (m.compareTo("Sep") == 0)
			return 1;
		if (m.compareTo("Oct") == 0)
			return 1;
		if (m.compareTo("Nov") == 0)
			return 1;
		return 12;
	}
	
	/**
	 * Convenience method to transform a GET query into a map of
	 * attribute-value pairs. For example, given an URI object
	 * representing the URL "http://abc.com/xyz?a=1&amp;b=2", the method
	 * will return an object mapping "a" to "1" and "b" to "2".
	 * @param query The URI to process
	 * @param m The method (GET, POST, etc.) of the request
	 * @return A map of attribute-value pairs
	 */
	public static Map<String,String> queryToMap(String query, Method m)
	{
		Map<String,String> out = new HashMap<String,String>();
		if (query == null)
			return out;
		String[] pairs = query.split("&");
		if (pairs.length == 1 && pairs[0].indexOf("=") < 0)
		{
			if (m == Method.GET)
			{
				// Single param with no value
				out.put(pairs[0], "");
			}
			else
			{
				// No params; likely a POST request with payload
				out.put("", pairs[0]);
			}
		}
		else
		{
			// List of attribute/value pairs
			for (String pair : pairs)
			{
				String[] av = pair.split("=");
				String att = av[0];
				String val;
				if (av.length > 1)
					val = av[1];
				else
					val = "";
				out.put(att, val);
			}
		}
		return out;    
}

	/**
	 * Creates a string out of a method
	 * @param m The method
	 * @return The method in string
	 */
	public static final String methodToString(HttpRequest.Method m)
	{
		switch (m)
		{
		case GET:
			return "GET";
		case POST:
			return "POST";
		case PUT:
			return "PUT";
		case DELETE:
			return "DELETE";
		}
		return "";
	}
	
	public static Method stringToMethod(String s)
	{
		if (s.compareTo("POST") == 0)
		{
			return Method.POST;
		}
		if (s.compareTo("PUT") == 0)
		{
			return Method.PUT;
		}
		if (s.compareTo("DELETE") == 0)
		{
			return Method.DELETE;
		}
		return Method.GET;
	}

	public static Map<String,String> parseParameters(String param_string)
	{
		return null;

	}

}
