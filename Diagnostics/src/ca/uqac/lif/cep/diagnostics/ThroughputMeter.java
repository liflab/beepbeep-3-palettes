package ca.uqac.lif.cep.diagnostics;

import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Sink;

public class ThroughputMeter extends Sink
{
	protected DiagnosticsCallback m_callback;
	
	protected long m_refreshInterval;
	
	protected Timer m_timer;
	
	protected long m_eventsReceived;
	
	public ThroughputMeter(DiagnosticsCallback callback, long refresh_interval)
	{
		super(1);
		m_callback = callback;
		m_refreshInterval = refresh_interval;
		m_eventsReceived = 0;
	}
	
	@Override
	public void start() throws ProcessorException
	{
		m_timer = new Timer(new ThroughputCallback(), m_refreshInterval);
		Thread t = new Thread(m_timer);
		t.start();
	}
	
	@Override
	public void stop() throws ProcessorException
	{
		m_timer.stop();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		m_eventsReceived++;
		return true;
	}

	@Override
	public Processor clone() 
	{
		return new ThroughputMeter(m_callback, m_refreshInterval);
	}
	
	protected class ThroughputCallback extends DiagnosticsCallback
	{
		@Override
		public void notify(Object f)
		{
			float throughput = 0;
			if (m_refreshInterval != 0)
			{
				throughput = ((float) m_eventsReceived) * 1000f / (float) m_refreshInterval;
				m_eventsReceived = 0;
				m_callback.notify(throughput);
			}
		}
	}

}
