package ca.uqac.lif.cep.concurrency;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.AbstractWindow;
import ca.uqac.lif.cep.tmf.SinkLast;

public class ParallelWindow extends AbstractWindow
{
	protected ArrayDeque<Processor> m_processors;
	
	protected ArrayDeque<Pushable> m_pushables;
	
	protected ArrayDeque<SinkLast> m_sinks;
	
	protected int m_eventCount = 0;
	
	public ParallelWindow(Processor in_processor, int width)
	{
		super(in_processor, width);
		m_processors = new ArrayDeque<Processor>(width);
		m_pushables = new ArrayDeque<Pushable>(width);
		m_sinks = new ArrayDeque<SinkLast>(width);
	}


	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		Processor p_copy = m_processor.duplicate();
		m_processors.add(p_copy);
		m_pushables.add(p_copy.getPushableInput(0));
		SinkLast as = new SinkLast(1);
		Connector.connect(p_copy, as);
		m_sinks.add(as);
		@SuppressWarnings("unchecked")
		Future<Pushable>[] futures = new Future[m_eventCount + 1];
		int i = 0;
		for (Pushable p : m_pushables)
		{
			futures[i] = p.pushFast(inputs[0]);
			i++;
		}
		for (Future<?> f : futures)
		{
			try 
			{
				f.get();
			} 
			catch (InterruptedException e) 
			{
				throw new ProcessorException(e);
			} 
			catch (ExecutionException e) 
			{
				throw new ProcessorException(e);
			}
		}
		if (m_eventCount <= m_width)
		{
			// No window is large enough yet
			m_eventCount++;
		}
		else
		{
			// The first processor in the list has received the
			// right number of events; remove it, and output its
			// output
			m_processors.removeFirst();
			m_pushables.removeFirst();
			SinkLast sink = m_sinks.removeFirst();
			outputs.add(sink.getLast());
		}
		return true;
	}


	@Override
	public ParallelWindow duplicate(boolean with_state)
	{
		ParallelWindow pw = new ParallelWindow(m_processor, m_width);
		if (with_state)
		{
		  pw.m_eventCount = m_eventCount;
		  Iterator<SinkLast> sink_it = m_sinks.iterator();
		  for (Processor p : m_processors)
		  {
		    Processor new_p = p.duplicate(true);
		    pw.m_processors.add(new_p);
		    Pushable new_p_p = new_p.getPushableInput();
		    pw.m_pushables.add(new_p_p);
		    SinkLast new_p_s = sink_it.next().duplicate(true);
		    pw.m_sinks.add(new_p_s);
		    Connector.connect(new_p, new_p_s);
		  }
		}
		return pw;
	}
	
	@Override
	public void reset()
	{
	  super.reset();
		m_eventCount = 0;
	}

}
