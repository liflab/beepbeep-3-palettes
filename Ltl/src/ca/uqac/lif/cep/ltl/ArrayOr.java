package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.ltl.TrooleanQuantifier.ArrayTroolean;

public class ArrayOr extends ArrayTroolean
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6482501708683581617L;
	public static final transient ArrayOr instance = new ArrayOr();
	
	@Override
	public void compute(Object[] inputs, Object[] out)
	{
		Object[] val_array = (Object[]) inputs[0];
		out[0] = Troolean.or(Troolean.trooleanValues(val_array));
	}

	@Override
	public Function duplicate()
	{
		return this;
	}
}