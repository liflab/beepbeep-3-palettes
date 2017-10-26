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
package ca.uqac.lif.cep.http;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

public class HttpListenerTest
{
	@Test
	public void testPushGet1() throws Exception
	{
		HttpListener listener = new HttpListener(10123, "/foo", Method.GET);
		listener.setPushOnReceive(true);
		QueueSink sink = new QueueSink();
		Queue<Object> q = sink.getQueue();
		Connector.connect(listener, sink);
		listener.start();
		sendGet("http://localhost:10123/foo?5");
		Thread.sleep(100);
		assertFalse(q.isEmpty());
		String s = (String) q.poll();
		assertEquals("5", s);
		sendGet("http://localhost:10123/foo?abcd");
		Thread.sleep(100);
		s = (String) q.poll();
		assertEquals("abcd", s);
		
	}
	
	protected static String sendGet(String url) throws Exception
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", "Dummy agent");

		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
}
