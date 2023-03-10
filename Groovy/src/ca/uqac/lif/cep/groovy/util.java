package ca.uqac.lif.cep.groovy;

import java.util.List;

import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.QueueSource;

public class util
{
	public static QueueSource QueueSource(List<? extends Object> queue)
	{
		return new QueueSource(queue);
	}
	
	public static CountDecimate CountDecimate(int interval)
	{
		return new CountDecimate(interval);
	}
	
	public static ApplyFunction ApplyFunction(Function f)
	{
		return new ApplyFunction(f);
	}
}
