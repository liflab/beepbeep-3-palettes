package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.TrooleanQuantifier.ArrayTroolean;

public class ArrayOr extends ArrayTroolean
{
	public static final transient ArrayOr instance = new ArrayOr();
	
	@Override
	public void compute(Object[] inputs, Object[] out)
	{
		Object[] val_array = (Object[]) inputs[0];
		out[0] = Troolean.or(Troolean.trooleanValues(val_array));
	}

	@Override
	public Function clone()
	{
		return this;
	}
}