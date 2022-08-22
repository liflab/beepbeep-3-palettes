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
package ca.uqac.lif.cep.ltl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.provenance.EventFunction;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class FindPattern extends SynchronousProcessor
{
	/**
	 * The processor acting as a monitor to detect pattern instances.
	 */
	/*@ non_null @*/ protected Processor m_pattern;

	/**
	 * The instances of the pattern currently being monitored over various
	 * suffixes of the input trace.
	 */
	protected List<PatternInstance> m_instances;

	/**
	 * Creates a new instance of the FindPattern processor.
	 * @param pattern The processor acting as a monitor to detect pattern
	 * instances
	 */
	public FindPattern(/*@ non_null @*/ Processor pattern)
	{
		super(1, 1);
		m_pattern = pattern;
		m_instances = new ArrayList<PatternInstance>();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		Processor m_dup = m_pattern.duplicate();
		m_dup.setEventTracker(m_eventTracker);
		PatternInstance new_pi = new PatternInstance(m_dup, m_inputCount++);
		m_instances.add(new_pi);
		Iterator<PatternInstance> it = m_instances.iterator();
		List<PatternInstance> matches = new ArrayList<PatternInstance>();
		while (it.hasNext())
		{
			PatternInstance pi = it.next();
			boolean moved = pi.push(inputs[0]);
			if (pi == new_pi && !moved)
			{
				it.remove();
				continue;
			}
			Troolean.Value verdict = pi.getVerdict();
			switch (verdict)
			{
			case TRUE:
				// Match: add to output and remove
				matches.add(pi);
				it.remove();
				break;
			case FALSE:
				// Non-match: delete this instance
				it.remove();
				break;
			default:
				break;
			}
		}
		if (!matches.isEmpty())
		{
			outputs.add(new Object[] {true});
			if (m_eventTracker != null)
			{
				for (PatternInstance m : matches)
				{
					List<Integer> subseq = m.getSubSequence();
					for (int pos : subseq)
					{
						m_eventTracker.associateToInput(getId(), 0, pos, 0, m_outputCount);
					}
				}
			}
			m_outputCount++;
		}
		return true;
	}

	@Override
	public FindPattern duplicate(boolean with_state)
	{
		FindPattern fp = new FindPattern(m_pattern);
		if (with_state)
		{
			fp.m_inputCount = m_inputCount;
			fp.m_outputCount = m_outputCount;
			for (PatternInstance pi : m_instances)
			{
				fp.m_instances.add(pi.duplicate(with_state));
			}
		}
		return fp;
	}

	protected static class PatternInstance
	{
		/**
		 * The processor acting as a monitor to detect pattern instances.
		 */
		/*@ non_null @*/ protected Processor m_monitor;

		/**
		 * The pushable instance to send events to the monitor.
		 */
		/*@ non_null @*/ protected Pushable m_pushable;

		/**
		 * A sink keeping the last event produced by the monitor so far.
		 */
		/*@ non_null @*/ protected SinkLast m_sink;
		
		/**
		 * The index of the event in the global input stream corresponding to index
		 * 0 of this instance's input stream. 
		 */
		protected int m_startOffset;

		/**
		 * The index of the current event in the input stream. This index
		 * is relative to the start offset.
		 */
		protected int m_currentIndex;

		/**
		 * The current internal state of the monitor.
		 */
		/*@ non_null @*/ protected Object m_currentState;

		/**
		 * The current progressing sub-sequence of this monitor.
		 */
		/*@ non_null @*/ protected List<Integer> m_subSequence;

		/**
		 * The sequence of states associated to the progressing sub-sequence.
		 */
		/*@ non_null @*/ protected List<Object> m_stateSequence;

		/**
		 * A map keeping track of the index in the state sequence where each state
		 * occurs.
		 */
		/*@ non_null @*/ protected Map<Object,Integer> m_seen;

		public PatternInstance(/*@ non_null @*/ Processor monitor, int start_index)
		{
			super();
			m_monitor = monitor;
			m_pushable = m_monitor.getPushableInput();
			m_startOffset = start_index;
			m_currentIndex = 0;
			m_sink = new SinkLast();
			Connector.connect(m_monitor, m_sink);
			m_currentState = getMonitorState();
			m_subSequence = new ArrayList<Integer>();
			m_stateSequence = new ArrayList<Object>();
			m_seen = new HashMap<Object,Integer>();
		}

		/**
		 * Pushes a new event into the monitor.
		 * @param o The event to push
		 * @return <tt>true</tt> if the monitor moved to a new state after
		 * reading this event, <tt>false</tt> otherwise
		 */
		public boolean push(Object o)
		{
			m_pushable.push(o);
			Object new_state = getMonitorState();
			boolean moved = !new_state.equals(m_currentState);
			if (moved)
			{
				append(o, new_state);
			}
			m_currentState = new_state;
			return moved;
		}

		/**
		 * Gets the index of the first event of the suffix evaluated by this
		 * pattern instance.
		 * @return The index
		 */
		/*@ pure @*/ public int getStartOffset()
		{
			return m_startOffset;
		}

		/**
		 * Gets the verdict computed by the monitor for the trace received so far.
		 * @return The verdict
		 */
		/*@ pure non_null @*/ public Troolean.Value getVerdict()
		{
			Object[] objs = m_sink.getLast();
			if (objs == null || objs.length == 0 || !(objs[0] instanceof Troolean.Value))
			{
				return Troolean.Value.INCONCLUSIVE;
			}
			return (Troolean.Value) objs[0];
		}

		/**
		 * Gets the progressing sub-sequence associated to this monitor.
		 * @return The sub-sequence
		 */
		/*@ pure non_null @*/ public List<Integer> getSubSequence()
		{
			/*List<Integer> offset_indices = new ArrayList<Integer>();
			EventTracker et = m_monitor.getEventTracker();
			if (et == null)
			{
				return offset_indices;
			}
			int mon_id = m_monitor.getId();
			for (int out_index : m_subSequence)
			{
				List<Integer> in_indices = getInputIndices(mon_id, et.getProvenanceTree(mon_id, 0, out_index));
				for (int i : in_indices)
				{
					offset_indices.add(i + m_startOffset);
				}
			}
			return offset_indices;*/
			return m_subSequence;
		}

		/**
		 * Creates a copy of the pattern instance.
		 * @param with_state Set to <tt>true</tt> for a stateful copy
		 * @return The copy
		 */
		public PatternInstance duplicate(boolean with_state)
		{
			PatternInstance pi = new PatternInstance(m_monitor.duplicate(with_state), m_currentIndex);
			if (with_state)
			{
				pi.m_currentState = m_currentState;
				pi.m_subSequence.addAll(m_subSequence);
				pi.m_stateSequence.addAll(m_stateSequence);
				pi.m_sink.getPushableInput().push(getVerdict());
				pi.m_seen.putAll(m_seen);
			}
			return pi;
		}

		/**
		 * Gets the current internal state of the monitor.
		 * @return The monitor's state, or 0 if the monitor does not implement the
		 * {@link Stateful} interface
		 */
		/*@ non_null @*/ protected Object getMonitorState()
		{
			if (!(m_monitor instanceof Stateful))
			{
				return 0;
			}
			return ((Stateful) m_monitor).getState();
		}

		/**
		 * Appends a new event to the progressing sub-sequence, and optionally
		 * cleans it up if this new event introduces a loop.
		 * @param event The event to add
		 * @param new_state The new state of the monitor
		 */
		protected void append(Object event, Object new_state)
		{
			if (m_seen.containsKey(new_state))
			{
				// Back to a previously visited state: first eliminate loop
				int loop_start = m_seen.get(new_state);
				int loop_len = m_stateSequence.size() - loop_start;
				for (int i = loop_start; i < m_stateSequence.size(); i++)
				{
					m_seen.remove(m_stateSequence.get(i));
				}
				for (int i = 0; i < loop_len; i++)
				{
					m_stateSequence.remove(loop_start);
					m_subSequence.remove(loop_start);
				}
			}
			m_subSequence.add(m_currentIndex);
			m_currentIndex++;
			m_stateSequence.add(new_state);
			m_seen.put(new_state, m_stateSequence.size() - 1);
		}
		
		/*@ pure non_null @*/ protected List<Integer> getInputIndices(int proc_id, ProvenanceNode tree)
		{
			List<Integer> indices = new ArrayList<Integer>();
			getInputIndices(proc_id, tree, indices);
			return indices;
		}
		
		/*@ pure @*/ protected void getInputIndices(int proc_id, ProvenanceNode n, List<Integer> list)
		{
			NodeFunction nf = n.getNodeFunction();
			if (nf instanceof EventFunction)
			{
				EventFunction ef = (EventFunction) nf;
				if (ef.getProcessorId() == proc_id && ef.getStreamIndex() == 0)
				{
					list.add(ef.getStreamPosition());
				}
			}
		}
	}
}