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

import java.util.Map;

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
	 * @param source_ip
	 * @param identd
	 * @param userid
	 * @param unix_time
	 * @param method
	 * @param path
	 * @param parameters
	 * @param response_code
	 * @param size
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
}
