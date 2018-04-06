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
package ca.uqac.lif.cep.diagnostics;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SingleProcessor;

public class Derivation extends SingleProcessor
{
	protected Processor[] m_processors;
	
	protected Pushable[] m_pushables;
	
	protected long m_slowDown;

	/**
	 * Creates a new derivation
	 * @param slowdown Insert a pause between each processed event. This
	 *   can be used to slow down the execution of a processor chain.
	 * @param procs The processors connected to the derivation
	 */
	public Derivation(long slowdown, Processor ... procs)
	{
		super(1, 1);
		m_processors = procs;
		m_pushables = new Pushable[procs.length];
		for (int i = 0; i < procs.length; i++)
		{
			m_pushables[i] = procs[i].getPushableInput();
		}
	}
	
	/**
	 * Creates a new derivation
	 * @param procs The processors connected to the derivation
	 */
	public Derivation(Processor ... procs)
	{
		this(0, procs);
	}
	
	/**
	 * Reconnects p1 and p2 so as to place the current processor
	 * in-between. That is, the output of p1 is sent to the derivation,
	 * and the output of the derivation is sent as the input of p2.
	 * 
	 * @param p1 The first processor
	 * @param p2 The second processor
	 * @return This derivation
	 */
	public Derivation reconnect(Processor p1, Processor p2)
	{
		Connector.connect(p1, this);
		Connector.connect(this, p2);
		return this;
	}
	
	@Override
	public void start() throws ProcessorException
	{
		for (Processor p : m_processors)
		{
			p.start();
		}
	}
	
	@Override
	public void stop() throws ProcessorException
	{
		for (Processor p : m_processors)
		{
			p.stop();
		}
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		for (Pushable p : m_pushables)
		{
			p.push(inputs[0]);
		}
		outputs.add(inputs);
		if (m_slowDown > 0)
		{
			try 
			{
				Thread.sleep(m_slowDown);
			}
			catch (InterruptedException e) 
			{
				// Do nothing
			}
		}
		return true;
	}

	@Override
	public Derivation duplicate(boolean with_state)
	{
		Processor[] clones = new Processor[m_processors.length];
		for (int i = 0; i < m_processors.length; i++)
		{
			clones[i] = m_processors[i].duplicate(with_state);
		}
		return new Derivation(clones);
	}
	
	
}
