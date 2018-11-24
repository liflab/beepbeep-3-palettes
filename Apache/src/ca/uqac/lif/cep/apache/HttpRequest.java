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
import java.util.Map;

import ca.uqac.lif.cep.functions.BinaryFunction;
import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Representation of an HTTP request, as logged by an Apache server
 * @author Sylvain Hallé
 */
public class HttpRequest 
{
	/**
	 * The HTTP method used in the request
	 */
	public static enum Method {GET, POST, PUT, DELETE};

	/**
	 * The source IP address
	 */
	protected final String m_sourceIp;

	/**
	 * Remote identification of the user
	 */
	protected final String m_identd;

	/**
	 * User ID
	 */
	protected final String m_userId;

	/**
	 * The time of the request: a Unix timestamp in seconds
	 */
	protected final long m_unixTime;

	/**
	 * The method used for the request (GET, POST)
	 */
	protected final Method m_method;

	/**
	 * The path of the request
	 */
	protected final String m_path;

	/**
	 * The parameters of this method
	 */
	protected final Map<String,String> m_parameters;

	/**
	 * The response code sent
	 */
	protected final int m_responseCode;

	/**
	 * The size of the response sent
	 */
	protected final int m_size;

	/**
	 * Creates a new HTTP request event
	 * @param source_ip The source IP address
	 * @param identd The identifier
	 * @param userid The user ID
	 * @param unix_time A Unix timestamp
	 * @param method The method (i.e. GET, POST, etc.)
	 * @param path The URL's path
	 * @param parameters The URL's parameters, if any
	 * @param response_code The response code of this request
	 * @param size The size of the request
	 */
	public HttpRequest(String source_ip, String identd, String userid, long unix_time, Method method, String path, Map<String,String> parameters, int response_code, int size)
	{
		super();
		m_sourceIp = source_ip;
		m_identd = identd;
		m_userId = userid;
		m_unixTime = unix_time;
		m_method = method;
		m_path = path;
		m_parameters = parameters;
		m_responseCode = response_code;
		m_size = size;
	}

	/**
	 * Gets the source IP address of this HTTP request
	 * @return The address
	 */
	public String getSourceIp() 
	{
		return m_sourceIp;
	}

	/**
	 * Gets the timestamp of this request
	 * @return The Unix timestamp
	 */
	public long getTimestamp()
	{
		return m_unixTime;
	}

	/**
	 * Gets the size of the response to this HTTP request
	 * @return The size of the response
	 */
	public int getResponseSize()
	{
		return m_size;
	}

	/**
	 * Gets the response code of this HTTP request
	 * @return The code
	 */
	public int getResponseCode()
	{
		return m_responseCode;
	}

	/**
	 * Gets the user associated to this HTTP request
	 * @return The user
	 */
	public String getUser()
	{
		return m_userId;
	}

	/**
	 * Gets the method of this HTTP request
	 * @return The method
	 */
	public Method getMethod() 
	{
		return m_method;
	}

	/**
	 * Gets the path of this HTTP request
	 * @return The path
	 */
	public String getPath() 
	{
		return m_path;
	}

	/**
	 * Returns the parameters associated with this HTTP request
	 * @return The parameters
	 */
	public Map<String,String> getParameters()
	{
		return m_parameters;
	}

	/**
	 * Gets the value of a parameter in the HTTP request
	 * @param parameter_name The name of the parameter
	 * @return The value if the parameter exists, {@code null} otherwise
	 */
	public String get(String parameter_name)
	{
		if (m_parameters.containsKey(parameter_name))
		{
			return m_parameters.get(parameter_name);
		}
		return null;
	}

	/**
	 * Function to get the path of an HTTP request
	 */
	public static class GetPath extends UnaryFunction<HttpRequest,String>
	{
		public static final GetPath instance = new GetPath();

		GetPath()
		{
			super(HttpRequest.class, String.class);
		}

		@Override
		public String getValue(HttpRequest request)
		{
			return request.getPath();
		}
	}
	
	/**
	 * Function to get the path of an HTTP request
	 */
	public static class GetParameter extends UnaryFunction<HttpRequest,String>
	{
		/**
		 * The name of the parameter to get
		 */
		protected String m_parameterName;
		
		public GetParameter(String parameter_name)
		{
			super(HttpRequest.class, String.class);
			m_parameterName = parameter_name;
		}

		@Override
		public String getValue(HttpRequest request)
		{
			String s = request.get(m_parameterName);
			if (s == null)
				return "";
			return s;
		}
	}
	
	/**
	 * Function to get the timestamp of an HTTP request
	 */
	public static class GetTimestamp extends UnaryFunction<HttpRequest,Long>
	{
		public static final GetTimestamp instance = new GetTimestamp();

		GetTimestamp()
		{
			super(HttpRequest.class, Long.class);
		}

		@Override
		public Long getValue(HttpRequest request)
		{
			return request.getTimestamp();
		}
	}

	/**
	 * Function to compare the request's time with another time
	 */
	public static class RequestIsAfter extends BinaryFunction<HttpRequest,Number,Boolean>
	{
		public static final RequestIsAfter instance = new RequestIsAfter();

		RequestIsAfter()
		{
			super(HttpRequest.class, Number.class, Boolean.class);
		}

		@Override
		public Boolean getValue(HttpRequest request, Number n)
		{
			return request.getTimestamp() > n.longValue();
		}
	}

	/**
	 * Converts a date in the format YYYY-MM-DD HH:MM:SS into a
	 * Unix timestamp. The time is optional, and if specified, the
	 * seconds are optional.
	 * @param calendar An instance of calendar object
	 * @param time_string The time string
	 * @return The timestamp
	 */
	public static long dateToTimestamp(Calendar calendar, String time_string)
	{
		String[] parts = time_string.split("[/ :\\-]");
		int day = Integer.parseInt(parts[2]);
		int month = Integer.parseInt(parts[1]);
		int year = Integer.parseInt(parts[0]);
		int hours = 0, minutes = 0, seconds = 0;
		if (parts.length > 3)
		{
			hours = Integer.parseInt(parts[3]);
			minutes = Integer.parseInt(parts[4]);
		}
		if (parts.length > 5)
		{
			seconds = Integer.parseInt(parts[5]);
		}
		calendar.set(year, month, day, hours, minutes, seconds);
		return calendar.getTime().getTime() / 1000;
	}
	
	@Override
	public String toString()
	{
		return m_path + m_parameters;
	}
}
