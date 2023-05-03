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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.util.Bags.ToList;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Unit tests for {@link RangeCep}.
 */
public class RangeCepTest
{
	@Test
	public void test1()
	{
		RangeCep sc = new RangeCep(new DummyRange(), new Processor[] {new Cumulate(new CumulativeFunction<Number>(Numbers.addition))}, new IdentityFunction(1));
		QueueSink sink = new QueueSink();
		Connector.connect(sc, sink);
		Queue<?> q = sink.getQueue();
		Pushable p = sc.getPushableInput();
		p.push(1);
		p.push(2);
		p.push(3);
		p.push("x");
		assertEquals(1, q.size());
		Object o = q.remove();
		assertEquals(6, ((Number) o).intValue());
	}
	
	@Test
	public void test2()
	{
		RangeCep sc = new RangeCep(new DummyRange(), new Processor[] {
				new Cumulate(new CumulativeFunction<Number>(Numbers.addition)),
				new GroupProcessor(1, 1) {
					{
						TurnInto one = new TurnInto(1);
						Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
						Connector.connect(one, sum);
						addProcessors(one, sum).associateInput(one).associateOutput(sum);
					}
				}},
				new ToList(Number.class, Number.class));
		QueueSink sink = new QueueSink();
		Connector.connect(sc, sink);
		Queue<?> q = sink.getQueue();
		Pushable p = sc.getPushableInput();
		p.push(1);
		p.push(2);
		p.push(3);
		p.push("x");
		assertEquals(1, q.size());
		List<?> list = (List<?>) q.remove();
		assertEquals(6, ((Number) list.get(0)).intValue());
		assertEquals(3, ((Number) list.get(1)).intValue());
	}
	
	protected static class DummyRange extends UniformProcessor
	{
		public DummyRange()
		{
			super(1, 1);
		}

		@Override
		protected boolean compute(Object[] inputs, Object[] outputs)
		{
			Object o = inputs[0];
			if ("x".equals(o))
			{
				outputs[0] = false;
			}
			else
			{
				outputs[0] = true;
			}
			return true;
		}

		@Override
		public Processor duplicate(boolean with_state)
		{
			// Don't care
			return null;
		}
	}
}
