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

import static org.junit.Assert.*;

import java.util.List;
import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.FindPattern.PatternInstance;
import ca.uqac.lif.cep.provenance.IndexEventTracker;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

/**
 * Unit tests for {@link FindPattern}.
 */
public class FindPatternTest
{
	@Test
	public void test1()
	{
		FindPattern fp = new FindPattern(new BAfterA());
		Pushable p = fp.getPushableInput();
		QueueSink qs = new QueueSink();
		Connector.connect(fp, qs);
		IndexEventTracker tracker = new IndexEventTracker();
		tracker.setTo(fp);
		Queue<Object> q = qs.getQueue();
		p.push("a");
		assertTrue(q.isEmpty());
		p.push("b");
		assertFalse(q.isEmpty());
		assertEquals(1, q.size());
		ProvenanceNode root = fp.getEventTracker().getProvenanceTree(fp.getId(), 0, 0);
		assertNotNull(root);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test2()
	{
		FindPattern fp = new FindPattern(new BAfterA());
		Pushable p = fp.getPushableInput();
		QueueSink qs = new QueueSink();
		Connector.connect(fp, qs);
		Queue<Object> q = qs.getQueue();
		p.push("c");
		assertTrue(q.isEmpty());
		p.push("c");
		assertTrue(q.isEmpty());
		p.push("a");
		assertTrue(q.isEmpty());
		p.push("c");
		assertTrue(q.isEmpty());
		p.push("c");
		assertTrue(q.isEmpty());
		p.push("b");
		assertFalse(q.isEmpty());
		assertEquals(1, q.size());
		List<PatternInstance> lpi = (List<PatternInstance>) q.remove();
		PatternInstance pi = lpi.get(0);
		assertEquals(2, pi.getStartOffset());
		List<Integer> subseq = pi.getSubSequence();
		assertEquals(2, subseq.size());
	}
	
	protected static class BAfterA extends UniformProcessor implements Stateful
	{
		protected int m_state;
		
		protected Troolean.Value m_lastVerdict;
		
		public BAfterA()
		{
			super(1, 1);
			m_state = 0;
			m_lastVerdict = Troolean.Value.INCONCLUSIVE;
		}
		
		@Override
		public BAfterA duplicate(boolean with_state)
		{
			BAfterA bfa = new BAfterA();
			if (with_state)
			{
				bfa.m_state = m_state;
				bfa.m_lastVerdict = m_lastVerdict;
			}
			return bfa;
		}

		@Override
		public Object getState()
		{
			return m_state;
		}

		@Override
		protected boolean compute(Object[] inputs, Object[] outputs)
		{
			if (m_lastVerdict != Troolean.Value.INCONCLUSIVE)
			{
				outputs[0] = m_lastVerdict;
				return true;
			}
			String in = (String) inputs[0];
			if (in.compareTo("a") != 0 && in.compareTo("b") != 0)
			{
				outputs[0] = m_lastVerdict;
				return true;
			}
			if (in.compareTo("a") == 0)
			{
				if (m_state == 0)
				{
					m_state = 1;
				}
			}
			if (in.compareTo("b") == 0)
			{
				if (m_state == 1)
				{
					m_state = 2;
					m_lastVerdict = Troolean.Value.TRUE;
					if (m_eventTracker != null)
					{
						m_eventTracker.associateToInput(getId(), 0, m_inputCount, 0, m_outputCount);
					}
				}
			}
			outputs[0] = m_lastVerdict;
			m_inputCount++;
			m_outputCount++;
			return true;
		}
	}
}
