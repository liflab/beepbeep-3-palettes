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
import java.net.ConnectException;
import java.util.Queue;

import com.sun.net.httpserver.HttpExchange;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.Sink;
import ca.uqac.lif.jerrydog.CallbackResponse;
import ca.uqac.lif.jerrydog.RequestCallback;
import ca.uqac.lif.jerrydog.RequestCallback.Method;
import ca.uqac.lif.jerrydog.Server;

/**
 * Sends character strings over the network through HTTP requests.
 * Graphically, this processor is represented as:
 * <p>
 * <a href="{@docRoot}/doc-files/HttpUpstreamGateway.png"><img
 *   src="{@docRoot}/doc-files/HttpUpstreamGateway.png"
 *   alt="Processor graph"></a>
 * @author Sylvain Hallé
 */
public class HttpUpstreamGateway extends Sink
{

	/**
	 * The URL that will be called when events are pushed
	 * to this gateway
	 */
	protected String m_pushUrl;

	/**
	 * The method (GET or POST) that will be used when calling the
	 * push URL
	 */
	protected Method m_pushMethod = Method.POST;

	/**
	 * The web server that will listen for downstream pull requests
	 */
	protected Server m_server = null;

	/**
	 * The URL that the server will listen to for pull requests
	 */
	protected String m_pullUrl = "/pull";

	/**
	 * The method (GET or POST) that will be used when listening to the
	 * pull URL
	 */
	protected Method m_pullMethod = Method.GET;
	
	/**
	 * The number of times the processor will retry when it fails to
	 * establish a connection to the other end
	 */
	protected static final int s_numRetries = 2;

	public HttpUpstreamGateway(String push_url, Method push_method, String pull_url, int pull_port)
	{
		super();
		m_pushUrl = push_url;
		m_pushMethod = push_method;
		m_pullUrl = pull_url;
		if (m_pullUrl != null)
		{
			m_server = new Server();
			m_server.setServerPort(pull_port);
			m_server.registerCallback(new PullCallback());
		}
	}

	public HttpUpstreamGateway(String push_url)
	{
		this(push_url, Method.POST, null, 0);
	}

	/**
	 * Sets the method (GET or POST) the gateway will respond to when
	 * listening to pull requests from downstream
	 * @param m The method
	 * @return This gateway
	 */
	public HttpUpstreamGateway setPullMethod(Method m)
	{
		m_pullMethod = m;
		return this;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		/* Since this processor is a sink, method compute can only be
		 * called as a result of a push. 
		 */
		String payload = (String) inputs[0];
		if (m_pushMethod == Method.POST)
		{
			for (int i = 0; i < s_numRetries; i++)
			{
				try 
				{
					HttpGateway.sendPost(m_pushUrl, payload);
				}
				catch (ConnectException ce)
				{
					try 
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e) 
					{
						// Do nothing
					}
				}
				catch (IOException e)
				{
					throw new ProcessorException(e);
				}
			}
		}
		else if (m_pushMethod == Method.GET)
		{
			try 
			{
				HttpGateway.sendGet(m_pushUrl + "?" + payload);
			} 
			catch (IOException e)
			{
				throw new ProcessorException(e);
			}
		}
		return true;
	}


	@Override
	public void start() throws ProcessorException
	{
		super.start();
		if (m_server != null)
		{
			try
			{
				m_server.startServer();
			}
			catch (IOException e)
			{
				throw new ProcessorException(e);
			}
		}
	}

	@Override
	public void stop() throws ProcessorException
	{
		super.stop();
		if (m_server != null)
		{
			m_server.stopServer();
		}
	}

	@Override
	public HttpUpstreamGateway duplicate(boolean with_state) 
	{
		// It doesn't make much sense to clone a network processor
		throw new UnsupportedOperationException();
	}

	/**
	 * Server callback that fires when the URL for this upstream
	 * gateway receives a request for events
	 */
	protected class PullCallback extends RequestCallback
	{

		@Override
		public boolean fire(HttpExchange t)
		{
			String path = t.getRequestURI().getPath();
			return t.getRequestMethod().compareToIgnoreCase(m_pullMethod.toString()) == 0 && path.startsWith(m_pullUrl);
		}

		@Override
		public CallbackResponse process(HttpExchange t)
		{
			CallbackResponse cbr = new CallbackResponse(t);
			cbr.setCode(CallbackResponse.HTTP_OK);
			Pullable p = getPullableInput(0);
			String s = (String) p.pull();
			cbr.setContents(s);
			return cbr;
		}
	}
}
