package ca.uqac.lif.cep.concurrency;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.concurrency.PushPipelineTest.SlowPassthrough;
import ca.uqac.lif.cep.tmf.QueueSink;

public class NonBlockingPushTest
{
	@Test(timeout=10000)
	public void test1() throws InterruptedException
	{
		int num_instances = 100, delay = 300;
		ExecutorService es = Executors.newCachedThreadPool();
		@SuppressWarnings("unused")
		List<Future<Pushable>> futures = new ArrayList<Future<Pushable>>(num_instances);
		List<Pushable> pushables = new ArrayList<Pushable>(num_instances);
		List<QueueSink> sinks = new ArrayList<QueueSink>(num_instances);
		// Create 100 instances
		for (int i = 0; i < num_instances; i++)
		{
			SlowPassthrough spt = new SlowPassthrough(1, delay);
			NonBlockingPush nbp = new NonBlockingPush(spt, es);
			QueueSink sink = new QueueSink();
			Connector.connect(nbp, sink);
			sinks.add(sink);
			Pushable p = nbp.getPushableInput();
			pushables.add(p);
		}
		// Push an event in rapid-fire fashion to each instance
		Stopwatch sw = new Stopwatch();
		sw.start();
		int i = 0;
		for (Pushable p : pushables)
		{
			p.push(i++);
		}
		sw.stop();
		// It took much less than num_instances * delay seconds to
		// push all these events, meaning that the calls were non-blocking
		assertTrue(sw.getDuration() < (num_instances * delay) * 0.2);
		// Wait a little more than delay
		es.awaitTermination((long) (delay * 1.2), TimeUnit.MILLISECONDS);
		// All sinks received a value, meaning that all calls to push were
		// successfully completed
		i = 0;
		for (QueueSink qs : sinks)
		{
			Object o = qs.getQueue().remove();
			assertEquals(i, o);
			i++;
		}
	}
	
	@Test(timeout=10000)
	public void test2() throws InterruptedException
	{
		// Same as test1, but for a binary processor
		int num_instances = 100, delay = 300;
		ExecutorService es = Executors.newCachedThreadPool();
		@SuppressWarnings("unused")
		List<Future<Pushable>> futures = new ArrayList<Future<Pushable>>(num_instances);
		List<Pushable> pushables0 = new ArrayList<Pushable>(num_instances);
		List<Pushable> pushables1 = new ArrayList<Pushable>(num_instances);
		List<QueueSink> sinks = new ArrayList<QueueSink>(num_instances);
		// Create 100 instances
		for (int i = 0; i < num_instances; i++)
		{
			SlowPassthrough spt = new SlowPassthrough(2, delay);
			NonBlockingPush nbp = new NonBlockingPush(spt, es);
			QueueSink sink = new QueueSink(2);
			Connector.connect(nbp, sink);
			sinks.add(sink);
			pushables0.add(nbp.getPushableInput(0));
			pushables1.add(nbp.getPushableInput(1));
		}
		// Push an event in rapid-fire fashion to each instance
		Stopwatch sw = new Stopwatch();
		sw.start();
		for (int i = 0; i < num_instances; i++)
		{
			pushables0.get(i).push(i);
			pushables1.get(i).push(2 * i);
		}
		sw.stop();
		// It took much less than num_instances * delay seconds to
		// push all these events, meaning that the calls were non-blocking
		assertTrue(sw.getDuration() < (num_instances * delay) * 0.2);
		// Wait a little more than delay
		es.awaitTermination((long) (delay * 1.2), TimeUnit.MILLISECONDS);
		// All sinks received a value, meaning that all calls to push were
		// successfully completed
		for (int i = 0; i < num_instances; i++)
		{
			QueueSink qs = sinks.get(i);
			Object o0 = qs.getQueue(0).remove();
			assertEquals(i, o0);
			Object o1 = qs.getQueue(1).remove();
			assertEquals(2 * i, o1);
		}
	}
}
