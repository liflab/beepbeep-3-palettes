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

import java.util.Queue;

import org.junit.After;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.jerrydog.CallbackResponse;
import ca.uqac.lif.jerrydog.RequestCallback;
import ca.uqac.lif.jerrydog.RequestCallback.Method;
import ca.uqac.lif.jerrydog.Server;

public class HttpListenerTest
{
	HttpDownstreamGateway listener = null;
	
	HttpUpstreamGateway hug = null;
	
	@Test(timeout = 2000)
	public void testPushDownstreamGet1() throws Exception
	{
		listener = new HttpDownstreamGateway(10123, "/foo", Method.GET);
		listener.setPushOnReceive(true);
		QueueSink sink = new QueueSink();
		Queue<Object> q = sink.getQueue();
		Connector.connect(listener, sink);
		listener.start();
		HttpGateway.sendGet("http://localhost:10123/foo?5");
		Thread.sleep(100);
		assertFalse(q.isEmpty());
		String s = (String) q.poll();
		assertEquals("5", s);
		HttpGateway.sendGet("http://localhost:10123/foo?abcd");
		Thread.sleep(100);
		s = (String) q.poll();
		assertEquals("abcd", s);
	}
	
	@Test(timeout = 2000)
	public void testDownstreamGet1() throws Exception
	{
		listener = new HttpDownstreamGateway(10125, "/foo", Method.GET);
		listener.setPushOnReceive(false);
		listener.start();
		Pullable p = listener.getPullableOutput();
		HttpGateway.sendGet("http://localhost:10125/foo?5");
		Thread.sleep(100);
		assertTrue(p.hasNext());
		String s = (String) p.pull();
		assertEquals("5", s);
		HttpGateway.sendGet("http://localhost:10125/foo?abcd");
		Thread.sleep(100);
		s = (String) p.pull();
		assertEquals("abcd", s);
	}
	
	@Test(timeout = 2000)
	public void testPushDownstreamPost1() throws Exception
	{
		HttpDownstreamGateway listener = new HttpDownstreamGateway(10124, "/foo", Method.POST);
		listener.setPushOnReceive(true);
		QueueSink sink = new QueueSink();
		Queue<Object> q = sink.getQueue();
		Connector.connect(listener, sink);
		listener.start();
		HttpGateway.sendPost("http://localhost:10124/foo", "5");
		Thread.sleep(100);
		assertFalse(q.isEmpty());
		String s = (String) q.poll();
		assertEquals("5", s);
		HttpGateway.sendPost("http://localhost:10124/foo", "abcd");
		Thread.sleep(100);
		s = (String) q.poll();
		assertEquals("abcd", s);
	}
	
	@Test(timeout = 10000)
	public void testPullUpstream() throws Exception
	{
		QueueSource qs = new QueueSource();
		qs.addEvent("A");
		qs.addEvent("B");
		qs.addEvent("C");
		HttpUpstreamGateway hug = new HttpUpstreamGateway("http://localhost:11123/foo", Method.GET, "/pull", 11124);
		Connector.connect(qs, hug);
		hug.start();
		String s = HttpGateway.sendGet("http://localhost:11124/pull");
		assertNotNull(s);
		s = s.trim();
		assertEquals("A", s);
		s = HttpGateway.sendGet("http://localhost:11124/pull");
		assertNotNull(s);
		s = s.trim();
		assertEquals("B", s);
		s = HttpGateway.sendGet("http://localhost:11124/pull");
		assertNotNull(s);
		s = s.trim();
		assertEquals("C", s);
	}
	
	@Test//(timeout = 10000)
	public void testPushUpstream() throws Exception
	{
		HttpUpstreamGateway hug = new HttpUpstreamGateway("http://localhost:11123/push");
		hug.start();
		Pushable p = hug.getPushableInput();
		Server test_server = new Server();
		test_server.setServerPort(11123);
		TestCallback cb = new TestCallback();
		test_server.registerCallback(cb);
		test_server.startServer();
		Thread.sleep(100);
		p.push("A");
		Thread.sleep(100);
		assertEquals(1, cb.getRequestCount());
		assertEquals("A", cb.m_lastBodyContents);
		p.push("B");
		Thread.sleep(100);
		assertEquals(2, cb.getRequestCount());
		assertEquals("B", cb.m_lastBodyContents);
		p.push("C");
		Thread.sleep(100);
		assertEquals(3, cb.getRequestCount());
		assertEquals("C", cb.m_lastBodyContents);
		test_server.stopServer();
	}
	
	@After
	public void stopListener() throws ProcessorException
	{
		if (listener != null)
		{
			listener.stop();
		}
		if (hug != null)
		{
			hug.stop();
		}
	}
	
	protected static class TestCallback extends RequestCallback
	{
		public String m_lastBodyContents = "";
		
		private int m_requestCount = 0;
		
		@Override
		public boolean fire(HttpExchange t)
		{
			String path = t.getRequestURI().getPath();
			return t.getRequestMethod().compareToIgnoreCase("POST") == 0 && path.startsWith("/push");
		}

		@Override
		public CallbackResponse process(HttpExchange t)
		{
			CallbackResponse cbr = new CallbackResponse(t);
			cbr.setCode(CallbackResponse.HTTP_OK);
			m_lastBodyContents = Server.streamToString(t.getRequestBody()).trim();
			m_requestCount++;
			return cbr;
		}
		
		public int getRequestCount()
		{
		  return m_requestCount;
		}
	}

}
