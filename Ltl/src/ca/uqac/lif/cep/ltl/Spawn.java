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
package ca.uqac.lif.cep.ltl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.NextStatus;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.ToCollection.ToArray;

class Spawn extends Processor
{	
	/**
	 * The internal processor to evaluate the quantifier on
	 */
	protected Processor m_processor;

	/**
	 * The function to evaluate to create each instance of the quantifier.
	 * This function must return a <code>Collection</code> of objects;
	 * one instance of the internal processor will be created for each
	 * element of the collection.
	 */
	protected Function m_splitFunction;

	/**
	 * Each instance of the processor spawned by the evaluation of the
	 * quantifier
	 */
	protected Processor[] m_instances;

	/**
	 * The fork used to split the input to the multiple instances of the
	 * processor
	 */
	protected Fork m_fork;

	/**
	 * The passthrough synchronizing the output of each processor instance
	 */
	protected Processor m_joinProcessor;

	/**
	 * The function processor used to combine the values of each
	 * instance 
	 */
	protected ApplyFunction m_combineProcessor;

	/**
	 * The pushable that will detect when the first event comes
	 */
	protected SentinelPushable m_inputPushable;

	/**
	 * The pullable that will detect when the first event is requested
	 */
	protected SentinelPullable m_outputPullable;

	/**
	 * Whether the split function generated any values
	 */
	protected boolean m_emptyDomain = false;

	/**
	 * The value to output if the spawn ranges over the empty set
	 */
	protected Object m_valueIfEmptyDomain = null;

	private Spawn()
	{
		super(1, 1);
	}

	public Spawn(Processor p, Function split_function, Function combine_function)
	{
		super(1, 1);
		m_processor = p;
		m_splitFunction = split_function;
		m_combineProcessor = new ApplyFunction(combine_function);
		m_combineProcessor.setContext(m_context);
		m_instances = null;
		m_fork = null;
		m_inputPushable = new SentinelPushable();
		m_outputPullable = new SentinelPullable();
		//m_processor.setPullableInput(0, m_outputPullable);
	}

	/**
	 * Sets the value to output if the spawn ranges over the empty set
	 * @param value The value
	 */
	public synchronized void setValueIfEmptyDomain(Object value)
	{
		m_valueIfEmptyDomain = value;
	}

	@Override
	public Pushable getPushableInput(int index)
	{
		if (index != 0)
		{
			return null;
		}
		return m_inputPushable;
	}

	@Override
	public synchronized void setContext(Context context)
	{
		super.setContext(context);
		m_combineProcessor.setContext(context);
		if (m_instances != null)
		{
			for (Processor p : m_instances)
			{
				p.setContext(context);
			}
		}
	}

	@Override
	public synchronized void setContext(String key, Object value)
	{
		super.setContext(key, value);
		m_combineProcessor.setContext(key, value);
		if (m_instances != null)
		{
			for (Processor p : m_instances)
			{
				p.setContext(key, value);
			}
		}
	}

	@Override
	public synchronized void setPullableInput(int index, Pullable p)
	{
		m_inputPushable.setPullable(p);
	}

	@Override
	public Pullable getPullableOutput(int index)
	{
		if (index != 0)
		{
			return null;
		}
		return m_outputPullable;
	}

	@Override
	public synchronized void setPushableOutput(int index, Pushable p)
	{
		//m_outputPullable.setPushable(p);
		m_outputPullable.setPushable(p);
	}

	protected class SentinelPushable implements Pushable
	{
		protected Pushable m_pushable = null;

		protected Pullable m_pullable = null;

		public SentinelPushable()
		{
			super();
		}

		synchronized public void setPushable(Pushable p)
		{
			m_pushable = p;
			if (m_fork != null && m_pullable != null)
			{
				m_fork.setPullableInput(0, m_pullable);
			}
		}

		@Override
		public Pushable push(Object o)
		{
			if (m_pushable == null)
			{
				try
				{
					spawn(o);
				}
				catch (ProcessorException e)
				{
					throw new PushableException(e);
				}
			}
			return m_pushable.push(o);
		}

		@Override
		public Pushable pushFast(Object o)
		{
			if (m_pushable == null)
			{
				try
				{
					spawn(o);
				}
				catch (ProcessorException e)
				{
					throw new PushableException(e);
				}
			}
			return m_pushable.pushFast(o);
		}

		@Override
		public Processor getProcessor() 
		{
			return Spawn.this;
		}

		@Override
		public int getPosition() 
		{
			return 0;
		}

		public void setPullable(Pullable p)
		{
			m_pullable = p;
			if (m_fork != null)
			{
				m_fork.setPullableInput(0, m_pullable);
			}
		}

		public Pullable getPullable()
		{
			return m_pullable;
		}

		@Override
		public void waitFor() 
		{
			m_pushable.waitFor();
		}

		@Override
		public void dispose() 
		{
			if (m_pullable != null)
			{
				m_pullable.dispose();
			}
			if (m_pushable != null)
			{
				m_pushable.dispose();
			}
		}
	}

	protected class SentinelPullable implements Pullable
	{
		protected Pullable m_pullable = null;

		protected Pushable m_pushable = null;

		public SentinelPullable()
		{
			super();
		}

		@Override
		synchronized public Object pullSoft()
		{
			if (m_pullable == null)
			{
				Object o = m_inputPushable.getPullable().pull();
				try
				{
					spawn(o);
				}
				catch (ProcessorException e)
				{
					throw new PullableException(e);
				}
				// Re-put o in fork's queue so that it can process it
				m_fork.putInQueue(o);
				//m_fork.getPushableInput(0).push(o);
			}
			return m_pullable.pullSoft();
		}

		@Override
		synchronized public Object pull()
		{
			if (m_pullable == null)
			{
				Object o = m_inputPushable.getPullable().pull();
				//System.out.println("Getting " + o);
				try
				{
					spawn(o);
				}
				catch (ProcessorException e)
				{
					throw new PullableException(e);
				}
				// Re-put o in fork's queue so that it can process it
				m_fork.putInQueue(o);
				//m_fork.getPushableInput(0).push(o);
			}
			return m_pullable.pull();
		}

		@Override
		synchronized public final Object next()
		{
			return pull();
		}

		@Override
		synchronized public NextStatus hasNextSoft()
		{
			if (m_pullable == null)
			{
				Object o = m_inputPushable.getPullable().pull();
				try
				{
					spawn(o);
				}
				catch (ProcessorException e)
				{
					throw new PullableException(e);
				}
				// Re-put o in fork's queue so that it can process it
				m_fork.putInQueue(o);
				//m_fork.getPushableInput(0).push(o);
			}
			return m_pullable.hasNextSoft();
		}

		@Override
		synchronized public boolean hasNext()
		{
			if (m_pullable == null)
			{
				Object o = m_inputPushable.getPullable().pull();
				try
				{
					spawn(o);
				}
				catch (ProcessorException e)
				{
					throw new PullableException(e);
				}
				// Re-put o in fork's queue so that it can process it
				m_fork.putInQueue(o);
				//m_fork.getPushableInput(0).push(o);
			}
			return m_pullable.hasNext();
		}

		@Override
		synchronized public Processor getProcessor()
		{
			return Spawn.this;
		}

		@Override
		synchronized public int getPosition()
		{
			return 0;
		}

		synchronized public void setPullable(Pullable p)
		{
			m_pullable = p;
			if (m_combineProcessor != null && m_pushable != null)
			{
				m_combineProcessor.setPushableOutput(0, m_pushable);
			}
		}

		synchronized public void setPushable(Pushable p)
		{
			m_pushable = p;
			if (m_combineProcessor != null)
			{
				m_combineProcessor.setPushableOutput(0, m_pushable);
			}
		}

		synchronized public Pushable getPushable()
		{
			return m_pushable;
		}

		@Override
		synchronized public Iterator<Object> iterator() 
		{
			return this;
		}

		@Override
		synchronized public void remove() 
		{
			// Cannot remove an event on a pullable
			throw new UnsupportedOperationException();
		}

		@Override
		synchronized public void start() 
		{
			// TODO Auto-generated method stub

		}

		@Override
		synchronized public void stop() 
		{
			m_pullable.stop();
		}

		@Override
		synchronized public void dispose() 
		{
			if (m_pullable != null)
			{
				m_pullable.dispose();
			}
			if (m_pushable != null)
			{
				m_pushable.dispose();
			}
		}
	}

	synchronized protected boolean spawn(Object o) throws ProcessorException
	{
		try 
		{
			Collection<?> values = getDomain(o);
			int size = values.size();
			if (size == 0)
			{
				// Domain is empty: processor returns a fixed value
				ApplyFunction mutator = new ApplyFunction(new Constant(m_valueIfEmptyDomain));
				m_inputPushable.setPushable(mutator.getPushableInput(0));
				mutator.setPullableInput(0, m_inputPushable.getPullable());
				m_outputPullable.setPullable(mutator.getPullableOutput(0));
				mutator.setPushableOutput(0, m_outputPullable.getPushable());
			}
			else
			{
				// Create a fork for as many values in the domain
				m_fork = new Fork(values.size());
				m_inputPushable.setPushable(m_fork.getPushableInput(0));
				m_instances = new Processor[size];
				// Create a join to collate the output of each spawned instance
				m_joinProcessor = new ApplyFunction(new ToArray(size));
				m_joinProcessor.setContext(m_context);
				// Spawn one new internal processor per value
				int i = 0;
				for (Object slice : values)
				{
					Processor new_p = m_processor.duplicate();
					new_p.setContext(m_context);
					addContextFromSlice(new_p, slice);
					m_instances[i] = new_p;
					// Connect its input to the fork
					Connector.connect(m_fork, i, new_p, 0);
					// Connect its output to the join
					Connector.connect(new_p, 0, m_joinProcessor, i);
					i++;
				}
				Connector.connect(m_joinProcessor, 0, m_combineProcessor, 0);
				m_outputPullable.setPullable(m_combineProcessor.getPullableOutput(0));
				for (i = 0; i < size; i++)
				{
					m_instances[i].start();
				}
			}
		}
		catch (FunctionException e)
		{
			throw new ProcessorException(e);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected synchronized /*@NotNull*/ Collection<Object> getDomain(Object o) 
	{
		/* TODO: there are *lots* of null checks in this method, just to
		 * fend off whatever the split function returns. A couple of these
		 * checks could probably be removed, given the proper preconditions.
		 */
		Object[] inputs = new Object[1];
		inputs[0] = o;
		Object[] function_value = new Object[1];
		m_splitFunction.evaluate(inputs, function_value, m_context);
		Collection<Object> values = new HashSet<Object>();
		Object value = function_value[0];
		if (value == null)
		{
			return values;
		}
		if (value instanceof Collection)
		{
			Collection<Object> coll = (Collection<Object>) value;
			for (Object element : coll)
			{
				if (element != null)
				{
					values.add(element);
				}
			}
		}
		else
		{
			values.add(function_value[0]);
		}
		return values;
	}

	public void addContextFromSlice(Processor p, Object slice)
	{
		// Do nothing
	}

	@Override
	public synchronized Spawn duplicate()
	{
		Processor new_p = m_processor.duplicate();
		new_p.setContext(m_context);
		Spawn out = new Spawn(new_p, m_splitFunction.duplicate(), m_combineProcessor.getFunction().duplicate());
		out.setContext(m_context);
		out.m_valueIfEmptyDomain = m_valueIfEmptyDomain;
		return out;
	}

	@Override
	public synchronized void start() throws ProcessorException
	{
		super.start();
		if (m_instances == null)
		{
			return;
		}
		for (Processor p : m_instances)
		{
			p.start();
		}
	}

	@Override
	public synchronized void stop() throws ProcessorException
	{
		super.stop();
		if (m_instances == null)
		{
			return;
		}
		for (Processor p : m_instances)
		{
			p.stop();
		}
	}
}
