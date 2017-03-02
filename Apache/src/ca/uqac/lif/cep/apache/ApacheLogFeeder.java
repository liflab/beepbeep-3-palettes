package ca.uqac.lif.cep.apache;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionProcessor;

public class ApacheLogFeeder extends FunctionProcessor 
{
	public ApacheLogFeeder()
	{
		super(ParseCommonLog.instance);
	}
	
	ApacheLogFeeder(Function f)
	{
		super(f);
	}

	@Override
	public ApacheLogFeeder clone() 
	{
		return new ApacheLogFeeder(getFunction().clone());
	}

}
