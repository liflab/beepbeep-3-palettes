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

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.net.httpserver.HttpExchange;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.jerrydog.CallbackResponse;
import ca.uqac.lif.jerrydog.RequestCallback;
import ca.uqac.lif.jerrydog.RequestCallback.Method;
import ca.uqac.lif.jerrydog.Server;

/**
 * Outputs character strings received over the network from an upstream
 * connection. A <code>HttpDownstreamGateway</code> listens to
 * HTTP requests on a given TCP port. When such a request comes in, it
 * extracts the payload from that request, and pushes it downstream as
 * a String event.
 * <p>
 * This processor can also be pulled; in such a case, if a pull URL has
 * been specified, the processor sends an HTTP request to that URL, and
 * uses the response's payload as the next event.
 * <p>
 * The downstream gateway generally works in tandem with an <em>upstream</em>
 * gateway, which works in reverse.
 * 
 * @author Sylvain Hallé
 *
 */
public class HttpDownstreamGateway extends Source
{
	/**
	 * A queue of character strings received over the network
	 */
	protected Queue<String> m_receivedStrings;

	/**
	 * A lock to access the queue
	 */
	protected Lock m_queueLock;

	/**
	 * The TCP port on which the gateway listens for requests
	 */
	protected int m_port;

	/**
	 * An internal instance of HTTP server
	 */
	protected Server m_server;

	/**
	 * The URL on which the gateway listens for requests
	 */
	protected String m_url;

	/**
	 * The HTTP method
	 */
	protected Method m_method;

	/**
	 * Whether this processor makes a call to <code>push()</code> when a
	 * request is received
	 */
	protected boolean m_pushOnReceive = true;

	/**
	 * The URL to call when this processor is asked for a new event. This
	 * can be null; in such a case, the processor won't do anything when
	 * being pulled.
	 */
	protected String m_pullUrl = null;

	/**
	 * Creates a new downstream gateway.
	 * @param port
	 * @param url
	 * @param m
	 */
	public HttpDownstreamGateway(int port, String url, Method m)
	{
		this(port, url, m, null);
	}

	/**
	 * Creates a new downstream gateway.
	 * @param port
	 * @param url
	 * @param m
	 * @param pull_url
	 */
	public HttpDownstreamGateway(int port, String url, Method m, String pull_url)
	{
		super(1);
		m_port = port;
		m_url = url;
		m_method = m;
		m_pullUrl = pull_url;
		m_server = new Server();
		m_server.setServerPort(m_port);
		m_server.registerCallback(new ListenerCallback());
		m_receivedStrings = new ArrayDeque<String>();
		m_queueLock = new ReentrantLock();
	}

	public HttpDownstreamGateway setPushOnReceive(boolean b)
	{
		m_pushOnReceive = b;
		return this;
	}

	@Override
	public void start() throws ProcessorException
	{
		super.start();
		try
		{
			m_server.startServer();
		}
		catch (IOException e)
		{
			throw new ProcessorException(e);
		}
	}

	@Override
	public void stop() throws ProcessorException
	{
		super.stop();
		m_server.stopServer();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		boolean queue_empty = false;
		m_queueLock.lock();
		queue_empty = m_receivedStrings.isEmpty();
		m_queueLock.unlock();
		if (queue_empty)
		{
			// Nothing in the queue; can we pull from somewhere?
			if (m_pullUrl == null)
			{
				// No; quit
				return true;
			}
			try 
			{
				// Yes: send the request and gather the response
				String contents = HttpGateway.sendGet(m_pullUrl);
				if (contents != null)
				{
					outputs.add(new Object[]{contents.trim()});
					return true;
				}
			}
			catch (Exception e) 
			{
				throw new ProcessorException(e);
			}
			return true;
		}
		m_queueLock.lock();
		String s = m_receivedStrings.poll();
		m_queueLock.unlock();
		outputs.add(new Object[]{s});
		return true;
	}

	@Override
	public HttpDownstreamGateway clone()
	{
		return new HttpDownstreamGateway(m_port, m_url, m_method);
	}

	protected class ListenerCallback extends RequestCallback
	{
		public ListenerCallback()
		{
			super();
		}

		@Override
		public boolean fire(HttpExchange t)
		{
			String path = t.getRequestURI().getPath();
			return t.getRequestMethod().compareToIgnoreCase(m_method.toString()) == 0 && path.startsWith(m_url);
		}

		@Override
		public CallbackResponse process(HttpExchange t)
		{
			CallbackResponse cbr = new CallbackResponse(t);
			cbr.setCode(CallbackResponse.HTTP_OK);
			if (m_method == Method.GET)
			{
				Map<String,String> map = Server.uriToMap(t.getRequestURI(), Method.GET);
				for (Map.Entry<String,String> entry : map.entrySet())
				{
					String s = entry.getKey().trim();
					if (m_pushOnReceive)
					{
						Pushable p = getPushableOutput(0);
						p.push(s);
					}
					else
					{
						m_queueLock.lock();
						m_receivedStrings.add(s);
						m_queueLock.unlock();
					}
				}
			}
			else // POST
			{
				String s = Server.streamToString(t.getRequestBody()).trim();
				if (m_pushOnReceive)
				{
					Pushable p = getPushableOutput(0);
					p.push(s);
				}
				else
				{
					m_queueLock.lock();
					m_receivedStrings.add(s);
					m_queueLock.unlock();
				}
			}
			return cbr;
		}
	}
}
