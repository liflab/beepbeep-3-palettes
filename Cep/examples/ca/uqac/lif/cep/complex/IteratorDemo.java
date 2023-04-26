/*
    A BeepBeep palette to create complex events out of simple events
    Copyright (C) 2008-2023 Sylvain Hallé

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
package ca.uqac.lif.cep.complex;

import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print.Println;
import ca.uqac.lif.cep.tmf.SliceLast;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Creates complex events out of low-level events occurring to Java iterators.
 * Each basic event represents a method call done on a Java iterator. It is
 * an array made of two elements:
 * <ul>
 * <li>The object's hashcode (an integer)</li>
 * <li>The name of the method being called on this object (a String)</li>
 * </ul>
 * The goal is to produce a high-level stream where each event summarizes the
 * "lifecycle" of an iterator, from its instantiation to its disposal. When an
 * iterator is destroyed, a "complex" {@link IteratorLifecycle} event should be
 * produced, summarizing the operations done on this iterator during its
 * lifespan: its hashcode, and the number of times some methods have been
 * called on it.
 * 
 */
public class IteratorDemo
{
	public static void main(String[] args)
	{
		/* Create a RangeCep processor that will aggregate information about a
		 * single instance of iterator. */
		RangeCep cep = new RangeCep(
				/* The range processor returns true until the "dispose" event is seen,
				 * triggering the creation of the complex output event. */
				new ApplyFunction(new FunctionTree(Booleans.not, new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("dispose")))),
				/* Four processors respectively output... */ 
				new Processor[] {
						/* The iterator's hashcode (same value in each event) */
						new ApplyFunction(new NthElement(0)),
						/* The cumulative number of "next" calls seen so far */
						new CountIf("next"),
						/* The cumulative number of "hasNext" calls seen so far */
						new CountIf("hasNext"),
						/* The cumulative number of "remove" calls seen so far */
						new CountIf("remove")},
				/* The function called to create the complex event when the end of the
				 * range (i.e. the "dispose" event) is reached. */
				new CreateIteratorLifecycle());
		
		/* Create a slice processor associating one instance of the RangeCep
		 * processor to each individual instance of iterator in circulation. */
		SliceLast slice = new SliceLast(new NthElement(0), cep);
		
		/* Connect to a printer and push some events. */
		Connector.connect(slice, new Println());
		Pushable p = slice.getPushableInput();
		Object i1 = new Object();
		Object i2 = new Object();
		p.push(new Object[]{i1, "next"});
		p.push(new Object[]{i2, "hasNext"});
		p.push(new Object[]{i1, "hasNext"});
		p.push(new Object[]{i2, "dispose"});
		p.push(new Object[]{i1, "dispose"});
	}
	
	/**
	 * Processor that accumulates the number of events where the method name
	 * (i.e. second element of the array) is equal to a specific string.
	 */
	protected static class CountIf extends GroupProcessor
	{
		public CountIf(String name)
		{
			super(1, 1);
			ApplyFunction nth = new ApplyFunction(new NthElement(1));
			ApplyFunction ite = new ApplyFunction(
					new FunctionTree(IfThenElse.instance, 
							new FunctionTree(Equals.instance, StreamVariable.X, new Constant(name)), 
							new Constant(1), 
							new Constant(0)));
			Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(nth, ite, sum);
			addProcessors(nth, ite, sum).associateInput(nth).associateOutput(sum);
		}
	}
	
	/**
	 * Definition of the complex event "iterator lifecycle". For each iterator
	 * reaching its disposal state, the complex event contains the following
	 * elements:
	 * <ul>
	 * <li>Hash code of the iterator in question</li>
	 * <li>Number of calls to {@code next()} on this iterator</li>
	 * <li>Number of calls to {@code hasNext()} on this iterator</li>
	 * <li>Number of calls to {@code remove()} on this iterator</li>
	 * </ul> 
	 */
	protected static class IteratorLifecycle
	{
		/**
		 * Hash code of the iterator.
		 */
		protected final int m_hash;
		
		/**
		 * Number of calls to {@code next()} on this iterator.
		 */
		protected final int m_numNext;
		
		/**
		 * Number of calls to {@code hasNext()} on this iterator.
		 */
		protected final int m_numHasNext;
		
		/**
		 * Number of calls to {@code remove()} on this iterator.
		 */
		protected final int m_numRemove;
		
		/**
		 * Creates a new iterator lifecycle complex event.
		 * @param hash Hash code of the iterator
		 * @param num_next Number of calls to {@code next()} on this iterator
		 * @param num_hasNext Number of calls to {@code hasNext()} on this iterator
		 * @param num_remove Number of calls to {@code remove()} on this iterator
		 */
		public IteratorLifecycle(int hash, int num_next, int num_hasNext, int num_remove)
		{
			super();
			m_hash = hash;
			m_numNext = num_next;
			m_numHasNext = num_hasNext;
			m_numRemove = num_remove;
		}
		
		@Override
		public String toString()
		{
			StringBuilder out = new StringBuilder();
			out.append("subject: ").append(m_hash).append(",");
			out.append("next: ").append(m_numNext).append(",");
			out.append("hasNext: ").append(m_numHasNext).append(",");
			out.append("remove: ").append(m_numRemove);
			return out.toString();
		}
	}
	
	/**
	 * Function that creates an {@link IteratorLifecycle} complex event out of
	 * four input arguments.
	 */
	protected static class CreateIteratorLifecycle extends Function
	{
		@Override
		public void evaluate(Object[] inputs, Object[] outputs, Context context, EventTracker tracker)
		{
			outputs[0] = new IteratorLifecycle(inputs[0].hashCode(), ((Number) inputs[1]).intValue(), ((Number) inputs[2]).intValue(), ((Number) inputs[3]).intValue());
		}

		@Override
		public int getInputArity()
		{
			return 4;
		}

		@Override
		public int getOutputArity()
		{
			return 1;
		}

		@Override
		public void getInputTypesFor(Set<Class<?>> classes, int index)
		{
			classes.add(Number.class);
		}

		@Override
		public Class<?> getOutputTypeFor(int index)
		{
			return Number.class;
		}

		@Override
		public Function duplicate(boolean with_state)
		{
			return this;
		}
	}
}
