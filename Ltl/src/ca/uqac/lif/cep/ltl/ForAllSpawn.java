package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.BooleanQuantifier.FirstOrderSpawn;

public class ForAllSpawn extends FirstOrderSpawn 
{
	public ForAllSpawn(String variable_name, Processor p, Function split_function) 
	{
		super(variable_name, split_function, p, ArrayAnd.instance, Troolean.Value.TRUE);
	}
	
	@Override
	public ForAllSpawn clone()
	{
		Processor new_proc = m_processor.clone();
		m_processor.setContext(m_context);
		ForAllSpawn new_spawn = new ForAllSpawn(m_variableName, new_proc, m_splitFunction.clone(m_context));
		if (m_context != null)
		{
			new_spawn.getContext().putAll(m_context);
		}
		return new_spawn;
	}

}
