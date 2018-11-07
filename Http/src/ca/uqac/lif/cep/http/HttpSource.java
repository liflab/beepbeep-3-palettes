/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.Sink;
import ca.uqac.lif.jerrydog.CallbackResponse;
import ca.uqac.lif.jerrydog.CallbackResponse.ContentType;
import ca.uqac.lif.jerrydog.RequestCallback;
import ca.uqac.lif.jerrydog.Server;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Queue;

/**
 * HTTP server that pulls events from upstream when it receives a GET request.
 * @author Sylvain Hallé
 */
public class HttpSource extends Sink
{
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
   * The MIME type to declare when sending an HTTP response
   */
  protected ContentType m_mimeType;
  
  /**
   * A pullable to get events from
   */
  protected Pullable m_pullable;

  public HttpSource(String url, int port, ContentType type)
  {
    super(1);
    m_url = url;
    m_port = port;
    m_server = new Server();
    m_server.setServerPort(m_port);
    m_server.registerCallback(new ListenerCallback());
    m_mimeType = type;
  }

  @Override
  public void start()
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

  @Override
  public void stop()
  {
    m_server.stopServer();
  }
  
  @Override
  public void reset()
  {
    super.reset();
    m_server.stopServer();
    m_pullable = null;
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // It doesn't make much sense to clone a network processor
    throw new UnsupportedOperationException();
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
      return path.startsWith(m_url);
    }

    @Override
    public CallbackResponse process(HttpExchange t)
    {
      CallbackResponse cbr = new CallbackResponse(t);
      if (m_pullable == null)
      {
        m_pullable = getPullableInput(0);
      }
      if (!m_pullable.hasNext())
      {
        cbr.setCode(CallbackResponse.HTTP_NOT_FOUND);
        return cbr;
      }
      Object o = m_pullable.pull();
      cbr.setCode(CallbackResponse.HTTP_OK);
      cbr.setContentType(m_mimeType);
      if (o instanceof byte[])
      {
        cbr.setContents((byte[]) o);
      }
      if (o instanceof String)
      {
        cbr.setContents((String) o);
      }
      return cbr;
    }
  }
}
