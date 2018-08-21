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
package ca.uqac.lif.cep.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;

public class NonBlockingPush extends Processor 
{
	protected transient ExecutorService m_service;
	
	protected Pushable[] m_inputPushables;
	
	protected Pullable[] m_outputPullables;
	
	protected Processor m_processor;
	
	protected ArraySink m_sink;
	
	public NonBlockingPush(Processor p, ExecutorService service) 
	{
		super(p.getInputArity(), p.getOutputArity());
		int in_arity = p.getInputArity();
		int out_arity = p.getOutputArity();
		m_service = service;
		m_processor = p;
		m_inputPushables = new Pushable[in_arity];
		m_outputPullables = new Pullable[out_arity];
		m_sink = new ArraySink(out_arity);
		Connector.connect(m_processor, m_sink);
	}

	@Override
	public Pushable getPushableInput(int index) 
	{
		if (this.m_inputPushables[index] == null)
		{
			m_inputPushables[index] = new NonBlockingPushable(index);
		}
		return m_inputPushables[index];
	}

	@Override
	public Pullable getPullableOutput(int index) 
	{
		if (this.m_outputPullables[index] == null)
		{
			m_outputPullables[index] = new Pullable.PullNotSupported(this, index);
		}
		return m_outputPullables[index];
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		NonBlockingPush nbp = new NonBlockingPush(m_processor.duplicate(), m_service);
		return nbp;
	}
	
	protected class NonBlockingPushable implements Pushable
	{
		protected int m_index;
		
		public NonBlockingPushable(int index)
		{
			super();
			m_index = index;
		}
		
		@Override
		public Pushable push(Object o) 
		{
			pushFast(o);
			return this;
		}

		@Override
		public Future<Pushable> pushFast(Object o)
		{
			PushRunnable pr = new PushRunnable(m_processor.getPushableInput(m_index), o);
			return m_service.submit(pr);
		}

		@Override
		public void notifyEndOfTrace() throws PushableException
		{
			// TODO
		}

		@Override
		public Processor getProcessor() 
		{
			return NonBlockingPush.this;
		}

		@Override
		public int getPosition() 
		{
			return m_index;
		}
	}
	
	protected class PushRunnable implements Callable<Pushable>
	{
		protected Pushable m_pushable;
		
		protected Object m_event;
		
		public PushRunnable(Pushable p, Object o)
		{
			super();
			m_pushable = p;
			m_event = o;
		}
		
		@Override
		public Pushable call()
		{
			m_pushable.push(m_event);
			while (!m_sink.m_queue.isEmpty())
			{
				Object[] front = m_sink.m_queue.remove();
				for (int i = 0; i < front.length; i++)
				{
					m_outputPushables[i].push(front[i]);
				}
			}
			return m_pushable;
		}
	}
}
