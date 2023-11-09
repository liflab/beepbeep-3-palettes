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

import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.PushUnit;

/**
 * Creates complex events out of a range of simple events. The processor is
 * parameterized by the following elements:
 * <ul>
 * <li>A processor &pi;<sub><i>R</i></sub> outputting a stream of Boolean
 * values, called the <em>range processor</em>. This processor signals the
 * range of contiguous events of the input stream that should be taken into
 * account in the construction of the complex event. Its expected behavior
 * is to continually return &bot; until the first event of the range is
 * observed, then to output &top; for each event to be included in the range,
 * and then to resume returning &bot; forever once the range is over.</li>
 * <li>An array of processors &pi;<sub>1</sub>, &hellip;
 * &pi;<sub><i>n</i></sub> called the <em>data processors</em>. Each of
 * these processors is fed the range of events identified by
 * &pi;<sub><i>R</i></sub>, and only those events. The calculation done by
 * the data processors is arbitrary.</li>
 * <li>A function <i>f</i> called the <em>complex event function</em>. This
 * function must be of arity <i>n</i>:1; it is called exactly once when the
 * end of the range is reached. Its input arguments are the last event produced
 * by each of the &pi;<sub><i>i</i></sub> on the event range; its output is the
 * complex event created out of those values.</li>
 * </ul>
 * In addition, the processor may optionally allow restarts 
 * (using {@link #allowRestarts(boolean)}). In this case, when a complex event
 * is produced, &pi;<sub><i>R</i></sub> and all of the &pi;<sub><i>i</i></sub>
 * are reset to their initial state, which makes it possible for a new
 * complex event to be produced with the events received from this point on. 
 * @author Sylvain Hallé
 */
public class RangeCep extends SynchronousProcessor
{
	/**
	 * The current state of the range, which is updated upon receiving each
	 * input event.
	 */
	protected static enum State {
		/**
		 * The event received lies before the start of the range.
		 */
		BEFORE,
		
		/**
		 * The event received is included within the range.
		 */
		ONGOING,
		
		/**
		 * The event received is after the end of the range.
		 */
		AFTER}
	
	/**
	 * The current state of the range.
	 */
	/*@ non_null @*/ protected State m_currentState;
	
	/**
	 * A push unit that evaluates the range processor.
	 */
	/*@ non_null @*/ protected final PushUnit m_range;
	
	/**
	 * An array of push units that evaluate the data processors.
	 */
	/*@ non_null @*/ protected final PushUnit[] m_dataProcessors;
	
	/**
	 * The function that is called when the end of the range is reached, and
	 * which is used to create the complex output event.
	 */
	/*@ non_null @*/ protected final Function m_complexEventFunction;
	
	/**
	 * A flag that determines if more than one complex event can be produced
	 * (default: {@code false}).
	 */
	protected boolean m_allowRestarts = false;
	
	/**
	 * A flag that determines if the event that determines the end boundary of
	 * the range should be included (i.e. pushed) a part of the complex event
	 * (default: {@code false}).
	 */
	protected boolean m_includesLast = false;
	
	/**
	 * A flag that determines if the false event that marks the end of a
	 * range should count as the first event of a new range (i.e. complex events
	 * are contiguous).
	 */
	protected boolean m_contiguous = false;
	
	/**
	 * Creates a new range complex event processor.
	 * @param range_processor The processor used to determine the range of events
	 * to include in the complex event
	 * @param data_processors An array of processors evaluated on the range of
	 * events
	 * @param ce_function The function used to create the complex output event
	 * out of the values produced by each data processor
	 */
	public RangeCep(Processor range_processor, Processor[] data_processors, Function ce_function)
	{
		super(1, 1);
		m_range = new PushUnit(range_processor);
		m_complexEventFunction = ce_function;
		m_dataProcessors = new PushUnit[data_processors.length];
		for (int i = 0; i < m_dataProcessors.length; i++)
		{
			m_dataProcessors[i] = new PushUnit(data_processors[i]);
		}
		m_currentState = State.BEFORE;
	}
	
	/**
	 * Creates a new range complex event processor. This constructor is only
	 * called internally by {@link #duplicate(boolean)}.
	 * @param range A push unit that evaluates the range processor
	 * @param data An array of push units that evaluate the data processors
	 * @param ce_function The function used to create the complex output event
	 * out of the values produced by each data processor
	 */
	protected RangeCep(PushUnit range, PushUnit[] data, Function ce_function)
	{
		super(1, 1);
		m_range = range;
		m_complexEventFunction = ce_function;
		m_dataProcessors = data;
		m_currentState = State.BEFORE;
	}
	
	/**
	 * Sets whether the processor is allowed to produce multiple successive
	 * complex events.
	 * @param b Set to {@code true} to allow restarts, {@code false} otherwise
	 * @return This processor
	 */
	/*@ non_null @*/ public RangeCep allowRestarts(boolean b)
	{
		m_allowRestarts = b;
		return this;
	}
	
	/**
	 * Sets whether the event that determines the end boundary of the range
	 * should be included (i.e. pushed) a part of the complex event.
	 * @param b Set to {@code true} to include this last event, {@code false}
	 * otherwise
	 * @return This processor
	 */
	/*@ non_null @*/ public RangeCep includesLast(boolean b)
	{
		m_includesLast = b;
		return this;
	}
	
	/**
	 * Sets whether the false event that marks the end of a range should count as
	 * the first event of a new range (i.e. complex events are contiguous). 
	 * @param b Set to {@code true} to make complex events contiguous, {@code
	 * false} otherwise
	 * @return This processor
	 */
	/*@ non_null @*/ public RangeCep isContiguous(boolean b)
	{
		m_contiguous = b;
		return this;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (!m_allowRestarts && m_currentState == State.AFTER)
		{
			return false;
		}
		m_range.push(inputs[0]);
		Object a_verdict = m_range.getLast();
		boolean verdict = false;
		if (a_verdict != null)
		{
			verdict = Boolean.TRUE.equals(a_verdict);
		}
		// Update state based on range processor
		switch (m_currentState)
		{
		case ONGOING:
			if (verdict == false)
			{
				// Done: produce complex event
				if (m_includesLast)
				{
					pushFront(inputs);
				}
				Object o = produceComplexEvent();
				outputs.add(new Object[] {o});
				if (m_allowRestarts)
				{
					m_range.reset();
					for (PushUnit pu : m_dataProcessors)
					{
						pu.reset();
					}
					m_complexEventFunction.reset();
					if (m_contiguous)
					{
						m_currentState = State.ONGOING;
						pushFront(inputs);
					}
					else
					{
						m_currentState = State.BEFORE;
					}
				}
				else
				{
					m_currentState = State.AFTER;
				}
				return m_allowRestarts;
			}
			break;
		case AFTER:
			// Won't output anything
			return m_allowRestarts;
		case BEFORE:
			if (verdict == false)
			{
				// Won't output anything yet
				return true;
			}
			else
			{
				// Switch to true
				m_currentState = State.ONGOING;
			}
			break;
		}
		if (m_currentState == State.ONGOING)
		{
			// Push event to all data processors
			pushFront(inputs);
		}
		return true;
	}
	
	/**
	 * Pushes an event front to all data processors.
	 * @param inputs The event front
	 */
	protected void pushFront(Object[] inputs)
	{
		for (PushUnit p : m_dataProcessors)
		{
			p.push(inputs[0]);
		}
	}
	
	/**
	 * Produces the complex output event out of the last event output by each
	 * data processor. This is done by fetching all these events in an array and
	 * then calling the complex event function.
	 * @return The complex output event
	 */
	/*@ null @*/ protected Object produceComplexEvent()
	{
		Object[] inputs = new Object[m_dataProcessors.length];
		for (int i = 0; i < inputs.length; i++)
		{
			inputs[i] = m_dataProcessors[i].getLast();
		}
		Object[] out = new Object[1];
		m_complexEventFunction.evaluate(inputs, out, getContext());
		return out[0];
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_range.reset();
		for (int i = 0; i < m_dataProcessors.length; i++)
		{
			m_dataProcessors[i].reset();
		}
		m_currentState = State.BEFORE;
		m_complexEventFunction.reset();
	}

	@Override
	public RangeCep duplicate(boolean with_state)
	{
		if (with_state)
		{
			throw new UnsupportedOperationException("Stateful duplication not implemented for this processor");
		}
		PushUnit[] p_dups = new PushUnit[m_dataProcessors.length];
		for (int i = 0; i < m_dataProcessors.length; i++)
		{
			p_dups[i] = m_dataProcessors[i].duplicate(false);
		}
		RangeCep sc = new RangeCep(m_range.duplicate(false), p_dups, m_complexEventFunction.duplicate(false));
		sc.allowRestarts(m_allowRestarts);
		sc.includesLast(m_includesLast);
		return sc;
	}
}
