package ca.uqac.lif.cep.concurrency;

public class Stopwatch
{
	protected long m_start = 0;
	
	protected long m_stop = 0;
	
	public void start()
	{
		m_start = System.currentTimeMillis();
	}
	
	public long stop()
	{
		m_stop = System.currentTimeMillis();
		return m_stop - m_start;
	}
	
	public long getDuration()
	{
		return m_stop - m_start;
	}
	
	public Stopwatch reset()
	{
		m_start = 0;
		m_stop = 0;
		return this;
	}
	
	public static void sleep(long duration)
	{
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long elapsed() 
	{
		return System.currentTimeMillis() - m_start;
	}
}
