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

public class HttpListener extends Source
{
	protected Queue<String> m_receivedStrings;

	protected Lock m_queueLock;

	protected int m_port;

	protected Server m_server;

	protected String m_url;

	protected Method m_method;

	public boolean m_pushOnReceive = false;

	public HttpListener(int port, String url, Method m)
	{
		super(1);
		m_port = port;
		m_url = url;
		m_method = m;
		m_server = new Server();
		m_server.setServerPort(m_port);
		m_server.registerCallback(new ListenerCallback());
		m_receivedStrings = new ArrayDeque<String>();
		m_queueLock = new ReentrantLock();
	}

	public HttpListener setPushOnReceive(boolean b)
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
			// Nothing to input
			return true;
		}
		m_queueLock.lock();
		String s = m_receivedStrings.poll();
		m_queueLock.unlock();
		outputs.add(new Object[]{s});
		return true;
	}

	@Override
	public HttpListener clone()
	{
		return new HttpListener(m_port, m_url, m_method);
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
					String s = entry.getKey();
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
				String s = Server.streamToString(t.getRequestBody());
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
