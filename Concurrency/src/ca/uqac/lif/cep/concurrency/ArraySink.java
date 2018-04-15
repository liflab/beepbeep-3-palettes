package ca.uqac.lif.cep.concurrency;

import java.util.ArrayDeque;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Sink;

public class ArraySink extends Sink
{
	protected Queue<Object[]> m_queue;
	
	public ArraySink(int arity)
	{
		super(arity);
		m_queue = new ArrayDeque<Object[]>();
	}
	
	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		m_queue.add(inputs);
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state) 
	{
		ArraySink as = new ArraySink(m_inputArity);
		if (with_state)
		{
			as.m_queue.addAll(m_queue);
		}
		return as;
	}

	public Queue<Object[]> getQueue()
	{
		return m_queue;
	}
}
