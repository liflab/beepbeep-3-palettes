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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

public class HttpListenerTest
{
	HttpDownstreamGateway listener = null;
	
	@After
	public void stopListener() throws ProcessorException
	{
		if (listener != null)
		{
			listener.stop();
		}
	}
	
	@Test(timeout = 2000)
	public void testPushGet1() throws Exception
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
	public void testGet1() throws Exception
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
	public void testPushPost1() throws Exception
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

}
