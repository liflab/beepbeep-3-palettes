package ca.uqac.lif.cep.concurrency;

/**
 * Runnable object that is allowed to throw an exception during its
 * execution.
 * @author Sylvain Hallé
 *
 */
public abstract class ExceptionRunnable implements Runnable
{
	private Exception m_exception = null;
	
	public final boolean hasException()
	{
		return m_exception != null;
	}
	
	public Exception getException()
	{
		return m_exception;
	}
	
	@Override
	public final void run()
	{
		try
		{
			tryToRun();
		}
		catch (Exception e)
		{
			m_exception = e;
		}
	}
	
	public abstract void tryToRun() throws Exception;
}
