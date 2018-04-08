package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean.Value;

public abstract class TrooleanGroundTerm extends UniformProcessor
{
	protected Value m_value = null;
	
	public TrooleanGroundTerm()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		if (m_value == null)
		{
			m_value = computeValue(inputs[0]);
		}
		outputs[0] = m_value;
		return true;
	}
	
	public void cloneInto(TrooleanGroundTerm tgt, boolean with_state)
	{
		if (with_state)
		{
			tgt.m_value = m_value;
		}
	}
	
	public abstract Value computeValue(Object o);
	
	@Override
	public void reset()
	{
		super.reset();
		m_value = null;
	}
	
	
}
