package ca.uqac.lif.cep.concurrency;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pullable.PullNotSupported;
import ca.uqac.lif.cep.Pushable;

public class PushPipeline extends Processor
{
	protected transient ExecutorService m_service;

	protected Processor m_processor;

	protected transient Pushable[] m_inputPushables;

	protected transient Pullable[] m_outputPullables;

	protected Lock[] m_inputQueuesLocks;

	protected Lock m_runnablesLock;

	protected transient ArrayDeque<PushRunnable> m_runnables;
	
	private int m_finished = 0;
	
	public int getFinished()
	{
		return m_finished;
	}

	public PushPipeline(Processor p, ExecutorService service)
	{
		super(p.getInputArity(), p.getOutputArity());
		int in_arity = p.getInputArity();
		int out_arity = p.getOutputArity();
		m_inputPushables = new Pushable[in_arity];
		m_outputPullables = new Pullable[out_arity];
		m_processor = p;
		m_service = service;
		m_inputQueuesLocks = new Lock[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_inputQueuesLocks[i] = new ReentrantLock();
		}
		m_runnables = new ArrayDeque<PushRunnable>();
		m_runnablesLock = new ReentrantLock();
	}

	@Override
	public synchronized Pushable getPushableInput(int index)
	{
		if (m_inputPushables[index] == null)
		{
			m_inputPushables[index] = new PipelinePushable(index);
		}
		return m_inputPushables[index];
	}

	@Override
	public synchronized Pullable getPullableOutput(int index) 
	{
		if (m_outputPullables[index] == null)
		{
			m_outputPullables[index] = new PipelinePullable(index);
		}
		return m_outputPullables[index];
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		return new PushPipeline(m_processor, m_service);
	}

	protected class PipelinePushable implements Pushable
	{
		protected int m_index;

		Object[] m_front;

		public PipelinePushable(int index)
		{
			super();
			m_index = index;
			m_front = new Object[PushPipeline.this.getInputArity()];
		}

		@Override
		public Pushable push(Object o) 
		{
			m_inputQueuesLocks[m_index].lock();
			m_inputQueues[m_index].add(o);
			m_inputQueuesLocks[m_index].unlock();
			boolean front_ready = true;
			for (int i = 0; i < PushPipeline.this.getInputArity(); i++)
			{
				m_inputQueuesLocks[i].lock();
			}
			while (front_ready)
			{				
				for (int i = 0; i < PushPipeline.this.getInputArity(); i++)
				{
					if (m_inputQueues[i].isEmpty())
					{
						front_ready = false;
						break;
					}
				}
				if (front_ready)
				{
					for (int i = 0; i < PushPipeline.this.getInputArity(); i++)
					{
						m_front[i] = m_inputQueues[i].remove();
					}
					PushRunnable pr = new PushRunnable(m_front);
					m_front = new Object[PushPipeline.this.getInputArity()];
					m_runnablesLock.lock();
					m_runnables.add(pr);
					m_runnablesLock.unlock();
					m_service.execute(pr);
				}
			}
			for (int i = 0; i < PushPipeline.this.getInputArity(); i++)
			{
				m_inputQueuesLocks[i].unlock();
			}
			return this;
		}

		@Override
		public void notifyEndOfTrace() throws PushableException
		{
			// Do nothing
		}

		@Override
		public Processor getProcessor() 
		{
			return PushPipeline.this;
		}

		@Override
		public int getPosition() 
		{
			return m_index;
		}

		@Override
		public Future<Pushable> pushFast(Object o) 
		{
			push(o);
			return Pushable.NULL_FUTURE;
		}
	}

	public class PushRunnable implements Runnable
	{
		protected transient Object[] m_events;

		protected transient ArraySink m_sink;

		protected boolean m_running = true;

		public PushRunnable(Object ... events)
		{
			super();
			m_events = events;
			m_sink = new ArraySink(m_processor.getOutputArity());
		}

		public boolean isRunning()
		{
			return m_running;
		}

		@Override
		public void run() 
		{
			m_running = true;
			Processor p = m_processor.duplicate();
			Connector.connect(p, m_sink);
			for (int i = 0; i < p.getInputArity(); i++)
			{
				if (m_events[i] == null)
				{
					System.out.println("Event is null");
				}
				p.getPushableInput(i).push(m_events[i]);
			}
			m_running = false;
			cleanUp();
		}		
	}

	protected synchronized void cleanUp()
	{
		m_runnablesLock.lock();
		while (!m_runnables.isEmpty())
		{
			PushRunnable r = m_runnables.getFirst();
			if (!r.isRunning())
			{
				m_finished++;
				Queue<Object[]> queue = r.m_sink.getQueue();
				for (Object[] front : queue)
				{
					for (int i = 0; i < front.length; i++)
					{
						m_outputPushables[i].push(front[i]);
					}
				}
				m_runnables.removeFirst();				
			}
			else
			{
				break;
			}
		}
		m_runnablesLock.unlock();
	}

	/**
	 * The {@link PushPipeline} processor currently does not support pull
	 * mode.
	 */
	protected class PipelinePullable extends PullNotSupported
	{
		public PipelinePullable(int index)
		{
			super(PushPipeline.this, index);
		}
	}
}
