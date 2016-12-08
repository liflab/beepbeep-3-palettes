package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.BooleanQuantifier.FirstOrderSpawn;

public class ExistsSpawn extends FirstOrderSpawn 
{
	public ExistsSpawn(String variable_name, Processor p, Function split_function) 
	{
		super(variable_name, split_function, p, ArrayOr.instance, Troolean.Value.FALSE);
	}
	
	@Override
	public ExistsSpawn clone()
	{
		Processor new_proc = m_processor.clone();
		m_processor.setContext(m_context);
		ExistsSpawn new_spawn = new ExistsSpawn(m_variableName, new_proc, m_splitFunction.clone(m_context));
		new_spawn.setContext(m_context);
		return new_spawn;
	}

}
