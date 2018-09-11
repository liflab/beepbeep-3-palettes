package ca.uqac.lif.cep.ltl;

import java.util.LinkedList;
import java.util.List;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.SinkLast;

public abstract class UnaryOperator extends UniformProcessor
{
	protected Processor m_processor;
	
	protected List<SinkLast> m_sinks;
	
	protected List<Pushable> m_pushables;
	
	protected List<Processor> m_processors;
	
	protected Troolean.Value m_lastValue = Value.INCONCLUSIVE;
	
	public UnaryOperator(Processor p)
	{
		super(1, 1);
		m_processor = p;
		m_sinks = new LinkedList<SinkLast>();
		m_processors = new LinkedList<Processor>();
		m_pushables = new LinkedList<Pushable>();
	}
	
	public UnaryOperator()
	{
		this(null);
	}
	
	public UnaryOperator setProcessor(Processor p)
	{
		m_processor = p;
		return this;
	}
	
	protected void spawn()
	{
		Processor new_p = m_processor.duplicate();
		new_p.setContext(m_context);
		Pushable p = new_p.getPushableInput();
		SinkLast sink = new SinkLast();
		Connector.connect(new_p, sink);
		m_sinks.add(sink);
		m_pushables.add(p);
		m_processors.add(new_p);
	}
	
	public void cloneInto(UnaryOperator st, boolean with_state) 
	{
		if (with_state)
		{
			st.m_lastValue = m_lastValue;
			for (int i = 0; i < m_processors.size(); i++)
			{
				Processor new_p = m_processors.get(i).duplicate(true);
				st.m_processors.add(new_p);
				st.m_pushables.add(new_p.getPushableInput());
				SinkLast sl = m_sinks.get(i).duplicate(true);
				Connector.connect(new_p, sl);
				st.m_sinks.add(sl);
			}
		}
	}
}
