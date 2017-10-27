package ca.uqac.lif.cep.ltl;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.QueueSink;

public class BooleanQuantifier extends SingleProcessor 
{
	/**
	 * The internal processor
	 */
	protected Processor m_spawn;

	/**
	 * The instances of the spawn processor
	 */
	protected List<Processor> m_instances;

	/**
	 * The input pushables of each instance
	 */
	protected List<Pushable> m_instancePushables;

	/**
	 * A sink for each instance
	 */
	protected List<QueueSink> m_sinks;

	/**
	 * A queue for each sink
	 */
	protected List<Queue<Object>> m_queues;	

	/**
	 * The function used to fetch the values of the quantified variable
	 * inside an input event
	 */
	protected Function m_domainFunction;

	/**
	 * The function used to combine the values of each instance of the inner
	 * processor (typically conjunction or disjunction).
	 */
	protected Function m_combineFunction;

	BooleanQuantifier()
	{
		super(1, 1);
		m_instances = new LinkedList<Processor>();
		m_instancePushables = new LinkedList<Pushable>();
		m_sinks = new LinkedList<QueueSink>();
		m_queues = new LinkedList<Queue<Object>>();
	}

	public BooleanQuantifier(Processor spawn)
	{
		this();
		m_spawn = spawn;
	}

	/*
	public BooleanQuantifier(String var_name, Function split_function, Processor spawn, Function combine_function, Object value_empty)
	{
		this();
		m_variableName = var_name;
		m_domainFunction = split_function;
		m_spawn = spawn;
	}*/

	@Override
	synchronized protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		Processor new_spawn = m_spawn.clone();
		new_spawn.setContext(m_context);
		m_instances.add(new_spawn);
		m_instancePushables.add(new_spawn.getPushableInput(0));
		QueueSink new_sink = new QueueSink(1);
		try 
		{
			Connector.connect(new_spawn, new_sink);
		} 
		catch (ConnectorException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_sinks.add(new_sink);
		m_queues.add(new_sink.getQueue(0));
		// Push event to all instances
		for (Pushable push : m_instancePushables)
		{
			push.pushFast(inputs[0]);
		}
		// Check if some instance reached a result
		int max_fetch_count = m_instances.size();
		for (int i = 0; i < m_instancePushables.size(); i++)
		{
			m_instancePushables.get(i).waitFor();
		}
		while (!m_instances.isEmpty() && max_fetch_count > 0)
		{
			Pushable i_pushable = m_instancePushables.get(0);
			i_pushable.waitFor();
			max_fetch_count--;
			Queue<Object> queue = m_queues.get(0);
			if (!queue.isEmpty())
			{
				// This instance reached a value: remove it
				Object value = queue.remove();
				Object[] v = new Object[1];
				v[0] = value;
				outputs.add(v);
				m_instances.remove(0);
				i_pushable.dispose();
				m_instancePushables.remove(0);
				m_sinks.remove(0);
				m_queues.remove(0);
			}
			else
			{
				// If this processor hasn't reached a verdict,
				// no use in processing the following
				//break;
			}
		}
		return true;
	}

	@Override
	synchronized public void setContext(Context context)
	{
		super.setContext(context);
		m_spawn.setContext(context);
	}

	@Override
	synchronized public void setContext(String key, Object value)
	{
		super.setContext(key, value);
		m_spawn.setContext(key, value);
	}

	@Override
	synchronized public BooleanQuantifier clone() 
	{
		Processor new_spawn = m_spawn.clone(); 
		new_spawn.setContext(m_context);
		BooleanQuantifier bq = new BooleanQuantifier(new_spawn);
		if (m_context != null)
		{
			bq.getContext().putAll(m_context);
		}
		return bq;
	}

	public static class FirstOrderSpawn extends Spawn
	{
		protected String m_variableName;
		
		public FirstOrderSpawn(String var_name, Function split_function, Processor p, Function combine_function, Object value_empty)
		{
			super(p, split_function, combine_function);
			m_variableName = var_name;
			m_valueIfEmptyDomain = value_empty;
			//m_domainFunction = domain;				
		}

		@Override
		public synchronized void addContextFromSlice(Processor p, Object slice) 
		{
			Object[] input = new Object[1];
			input[0] = slice;
			p.setContext(m_variableName, slice);
		}

		@Override
		public synchronized FirstOrderSpawn clone()
		{
			Processor new_p = m_processor.clone();
			FirstOrderSpawn fos = new FirstOrderSpawn(m_variableName, m_splitFunction.clone(m_context), new_p, m_combineProcessor.getFunction().clone(m_context), m_valueIfEmptyDomain);
			fos.setContext(m_context);
			return fos;
		}
	}

	@Override
	synchronized public void start() throws ProcessorException
	{
		super.start();
		for (Processor spawn : m_instances)
		{
			spawn.start();
		}
	}

	@Override
	synchronized public void stop() throws ProcessorException
	{
		super.stop();
		for (Processor spawn : m_instances)
		{
			spawn.stop();
		}
	}

}
