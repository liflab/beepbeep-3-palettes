package ca.uqac.lif.cep.concurrency;

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.tmf.QueueSink;

public class PushPipelineTest 
{
	@Test
	public void pushTest1()
	{
		Stopwatch sw = new Stopwatch();
		ExecutorService ex = Executors.newCachedThreadPool();
		SlowPassthrough spt = new SlowPassthrough(1, 200);
		PushPipeline pp = new PushPipeline(spt, ex);
		QueueSink qs = new QueueSink();
		Queue<Object> queue = qs.getQueue();
		Pushable p = pp.getPushableInput();
		Connector.connect(pp, qs);
		sw.start();
		for (int i = 0; i < 5; i++)
		{
			p.push(i);
		}
		sw.stop();
		// Time to push 5 events smaller than a single blocking
		// call to push
		assertTrue(sw.getDuration() < 200);
		Stopwatch.sleep(300);
		// After more than 200 ms, all 5 instances are done, proof that
		// they all waited in parallel
		assertQueueSize(5, 5, queue);
		assertEquals(0, queue.remove());
		assertEquals(1, queue.remove());
		assertEquals(2, queue.remove());
		assertEquals(3, queue.remove());
		assertEquals(4, queue.remove());
	}
	
	@Test
	public void pushTest2()
	{
		// In this test, the 5 copies of the processor launched by the pipeline
		// compute their event in reverse order: the last to be launched will
		// finish first. This is to check that the pipeline still outputs the
		// events in the correct order.
		Stopwatch sw = new Stopwatch();
		ExecutorService ex = Executors.newCachedThreadPool();
		UnpredictableSlowPassthrough spt = new UnpredictableSlowPassthrough(1, 200);
		PushPipeline pp = new PushPipeline(spt, ex);
		QueueSink qs = new QueueSink();
		Queue<Object> queue = qs.getQueue();
		Pushable p = pp.getPushableInput();
		Connector.connect(pp, qs);
		sw.start();
		for (int i = 0; i < 5; i++)
		{
			p.push(i);
		}
		sw.stop();
		// Time to push 5 events smaller than a single blocking
		// call to push
		assertTrue(sw.getDuration() < 200);
		Stopwatch.sleep(300);
		// After more than 200 ms, all 5 instances are done, proof that
		// they all waited in parallel
		assertQueueSize(5, 5, queue);
		assertEquals(0, queue.remove());
		assertEquals(1, queue.remove());
		assertEquals(2, queue.remove());
		assertEquals(3, queue.remove());
		assertEquals(4, queue.remove());
	}
	
	@Test
	public void pushTest3()
	{
		// Checks the correct behaviour of the pipeline in push mode, for
		// a processor of arity greater than 1
		Stopwatch sw = new Stopwatch();
		ExecutorService ex = Executors.newCachedThreadPool();
		SlowPassthrough spt = new SlowPassthrough(2, 100);
		PushPipeline pp = new PushPipeline(spt, ex);
		QueueSink qs = new QueueSink(2);
		Queue<Object> queue = qs.getQueue(0);
		Pushable p0 = pp.getPushableInput(0);
		Pushable p1 = pp.getPushableInput(1);
		Connector.connect(pp, qs);
		sw.start();
		for (int i = 0; i < 5; i++)
		{
			p0.push(i);
			p1.push(2 * i);
		}
		sw.stop();
		// Time to push 5 events smaller than a single blocking
		// call to push
		assertTrue(sw.getDuration() < 200);
		Stopwatch.sleep(400);
		// After more than 200 ms, all 5 instances are done, proof that
		// they all waited in parallel
		assertQueueSize(5, 5, queue);
		assertEquals(0, queue.remove());
		assertEquals(1, queue.remove());
		assertEquals(2, queue.remove());
		assertEquals(3, queue.remove());
		assertEquals(4, queue.remove());
	}
	
	@Test
	public void getThreshold()
	{
		long delay = 2000;
		int num = 50;
		Stopwatch sw = new Stopwatch();
		ExecutorService ex = Executors.newCachedThreadPool();
		LoopPassthrough spt = new LoopPassthrough(1, delay);
		PushPipeline pp = new PushPipeline(spt, ex);
		QueueSink qs = new QueueSink(1);
		Queue<Object> queue = qs.getQueue();
		Pushable p = pp.getPushableInput();
		Connector.connect(pp, qs);
		sw.start();
		for (int i = 0; i < num; i++)
		{
			p.push(0);
		}
		while (queue.size() < num)
		{
			
		}
		sw.stop();
		System.out.println("Expected: " + num * delay + "; acutal: " + sw.getDuration());
	}
	
	public static void assertQueueSize(int lower, int higher, Queue<?> q)
	{
		int size = q.size();
		if (size < lower)
		{
			fail("Queue size must be at least " + lower + "; was " + size);
		}
		if (size > higher)
		{
			fail("Queue size must be at most " + higher + "; was " + size);
		}
	}
	
	public static class SlowPassthrough extends Passthrough
	{
		protected long m_duration;

		public SlowPassthrough(int arity, long delay)
		{
			super(arity);
			m_duration = delay;
		}

		@Override
		public boolean compute(Object[] inputs, Object[] outputs)
		{
			Stopwatch.sleep(m_duration);
			//System.out.println("COMPUTE " + this);
			return super.compute(inputs, outputs);
		}

		@Override
		public SlowPassthrough duplicate(boolean with_state)
		{
			return new SlowPassthrough(getInputArity(), m_duration);
		}
	}
	
	public static class UnpredictableSlowPassthrough extends Passthrough
	{
		protected long m_duration;

		protected static int m_computeCount = 0;

		public UnpredictableSlowPassthrough(int arity, long delay)
		{
			super(arity);
			m_duration = delay;
		}

		@Override
		public boolean compute(Object[] inputs, Object[] outputs)
		{
			m_computeCount++;
			m_duration *= (float) (6 - m_computeCount) / 5;
			Stopwatch.sleep(m_duration);
			System.out.println("COMPUTE " + m_duration + " " + inputs[0]);
			return super.compute(inputs, outputs);
		}

		@Override
		public UnpredictableSlowPassthrough duplicate(boolean with_state)
		{
			return new UnpredictableSlowPassthrough(getInputArity(), m_duration);
		}
	}
	
	public static class LoopPassthrough extends Passthrough
	{
		protected long m_duration;

		public LoopPassthrough(int arity, long delay)
		{
			super(arity);
			m_duration = delay;
		}

		@Override
		public boolean compute(Object[] inputs, Object[] outputs)
		{
			Stopwatch sw = new Stopwatch();
			sw.start();
			@SuppressWarnings("unused")
			long i = 0;
			while (sw.elapsed() < m_duration)
			{
				i += Math.random() * 1000;
			}
			return super.compute(inputs, outputs);
		}

		@Override
		public LoopPassthrough duplicate(boolean with_state)
		{
			return new LoopPassthrough(getInputArity(), m_duration);
		}
	}
}
