package ca.uqac.lif.cep.diagnostics;
/**
 * Timer that pushes the contents of <code>m_packedEvents</code> every
 * <code>m_outputInterval</code> milliseconds.
 */
public class Timer implements Runnable
{
	protected volatile boolean m_run = true;
	
	protected final long m_interval;
	
	protected DiagnosticsCallback m_callback;
	
	public Timer(DiagnosticsCallback callback, long interval)
	{
		super();
		m_callback = callback;
		m_interval = interval;
	}
	
	public void stop()
	{
		m_run = false;
	}
	
	@Override
	public void run() 
	{
		m_run = true;
		while (m_run)
		{
			try 
			{
				Thread.sleep(m_interval);
			}
			catch (InterruptedException e) 
			{
				// Do nothing
			}
			m_callback.notify(null);
		}
	}		
}