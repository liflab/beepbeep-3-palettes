package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;

public abstract class BooleanFirstOrderQuantifier extends FirstOrderQuantifier
{
	public BooleanFirstOrderQuantifier(String var_name, Function dom_function, Processor expression)
	{
		super(var_name, dom_function, expression, true);
	}
}
