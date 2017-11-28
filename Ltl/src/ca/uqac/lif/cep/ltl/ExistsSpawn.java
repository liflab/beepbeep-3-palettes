package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.BooleanQuantifier.FirstOrderSpawn;

public class ExistsSpawn extends FirstOrderSpawn 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -390584934164081505L;

	public ExistsSpawn(String variable_name, Processor p, Function split_function) 
	{
		super(variable_name, split_function, p, ArrayOr.instance, Troolean.Value.FALSE);
	}
	
	@Override
	public synchronized ExistsSpawn duplicate()
	{
		Processor new_proc = m_processor.duplicate();
		new_proc.setContext(m_context);
		ExistsSpawn new_spawn = new ExistsSpawn(m_variableName, new_proc, m_splitFunction.duplicate(m_context));
		new_spawn.setContext(m_context);
		return new_spawn;
	}

}
