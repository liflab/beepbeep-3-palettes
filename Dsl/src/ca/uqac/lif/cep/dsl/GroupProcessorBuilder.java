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
	
	protected Map<Integer,Fork> m_inputForks;
	
	public GroupProcessorBuilder()
	{
		super();
		m_processors = new HashSet<Processor>();
		m_inputForks = new HashMap<Integer,Fork>();
	}
	
	public void add(Processor p)
	{
		m_processors.add(p);
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
		for (Map.Entry<Integer,Fork> entry : m_inputForks.entrySet())
		{
			gp.associateInput(entry.getKey(), entry.getValue(), 0);
		}
		for (int i = 0; i < p.getOutputArity(); i++)
		{
			gp.associateOutput(i, p, i);
		}
		return gp;
	}

	public Passthrough forkInput(int n)
	{
		Fork f = null;
		if (m_inputForks.containsKey(n))
		{
			f = m_inputForks.get(n);
		}
		else
		{
			f = new Fork(1);
			m_inputForks.put(n, f);
		}
		int f_arity = f.getInputArity();
		f.extendOutputArity(f_arity + 1);
		Passthrough pt = new Passthrough();
		Connector.connect(f, f_arity, pt, 0);
		return pt;
	}
}
