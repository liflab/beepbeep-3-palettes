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
package ca.uqac.lif.cep.dsl;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Passthrough;

public class GroupProcessorBuilder extends GrammarObjectBuilder<GroupProcessor>
{
	protected Set<Processor> m_processors;
	
	protected Map<Object,Fork> m_inputForks;
	
	public GroupProcessorBuilder()
	{
		super();
		m_processors = new HashSet<Processor>();
		m_inputForks = new HashMap<Object,Fork>();
	}
	
	public Processor add(Processor ... procs)
	{
		Processor last = null;
		for (Processor p : procs)
		{
			m_processors.add(p);
			last = p;
		}
		return last;
	}
	
	public void remove(Processor ... procs)
	{
		for (Processor p : procs)
		{
			m_processors.remove(p);
		}
	}
	
	@Override
	protected synchronized GroupProcessor postVisit(Deque<Object> stack)
	{
		if (stack.isEmpty())
		{
			return null;
		}
		Processor p = (Processor) stack.peek();
		GroupProcessor gp = new GroupProcessor(m_inputForks.size(), p.getOutputArity());
		for (Processor in_p : m_processors)
		{
			gp.addProcessor(in_p);
		}
		int index = 0;
		for (Map.Entry<Object,Fork> entry : m_inputForks.entrySet())
		{
			Fork f = entry.getValue();
			gp.addProcessor(f);
			if (f.getPullableInput(0) != null)
			{
				// This is an "internal" fork already connected to something inside
				continue;
			}
			gp.associateInput(index, f, 0);
			index++;
		}
		for (int i = 0; i < p.getOutputArity(); i++)
		{
			gp.associateOutput(i, p, i);
		}
		return gp;
	}
	
	public Fork getFork(Object name)
	{
		Fork f = null;
		if (m_inputForks.containsKey(name))
		{
			f = m_inputForks.get(name);
		}
		else
		{
			f = newFork();
			m_inputForks.put(name, f);
		}
		return f;
	}
	
	protected Fork newFork()
	{
		return new Fork(0);
	}

	public Passthrough forkInput(Object name)
	{
		Fork f = getFork(name);
		int f_arity = f.getOutputArity();
		f.extendOutputArity(f_arity + 1);
		Passthrough pt = new Passthrough();
		Connector.connect(f, f_arity, pt, 0);
		return pt;
	}
	
	/**
   * Encapsulates a processor into a group processor
   * @param p The processor to encapsulate
   * @return A {@link GroupProcessor} containing only p inside
   */
  protected static GroupProcessor encapsulate(Processor p)
  {
    if (p instanceof GroupProcessor)
    {
      return (GroupProcessor) p;
    }
    GroupProcessor gp = new GroupProcessor(p.getInputArity(), p.getOutputArity());
    gp.addProcessor(p);
    for (int i = 0; i < p.getInputArity(); i++)
      gp.associateInput(i, p, i);
    for (int i = 0; i < p.getOutputArity(); i++)
      gp.associateOutput(i, p, i);
    return gp;
  }
}
