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
			if (f.getPullableInput(0).getProcessor() == null)
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
			f = new Fork(0);
			m_inputForks.put(name, f);
		}
		return f;
	}

	public Passthrough forkInput(Object name)
	{
		Fork f = getFork(name);
		int f_arity = f.getInputArity();
		f.extendOutputArity(f_arity + 1);
		Passthrough pt = new Passthrough();
		Connector.connect(f, f_arity, pt, 0);
		return pt;
	}
}
