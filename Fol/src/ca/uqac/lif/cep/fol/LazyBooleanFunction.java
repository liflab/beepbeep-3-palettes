package ca.uqac.lif.cep.fol;

import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.Negation;
import ca.uqac.lif.cep.functions.UnaryFunction;


public class LazyBooleanFunction extends Function
{
	protected boolean m_stopValue;
	
	protected Function m_left;
	
	protected Function m_right;
	
	public LazyBooleanFunction(boolean stop_value, Function left, Function right)
	{
		super();
		m_left = left;
		m_right = right;
		m_stopValue = stop_value;
	}
	
	@Override
	public int getInputArity()
	{
		// 1, since we take as input an interpretation
		return 1;
	}
	
	@Override
	public int getOutputArity()
	{
		return 1;
	}
	
	@Override
	public void evaluate(Object[] inputs, Object[] out, Context context) throws FunctionException
	{
		// Evaluate LHS
		m_left.evaluate(inputs, out, context);
		if ((Boolean) out[0] == m_stopValue)
		{
			return;
		}
		// Evaluate RHS
		m_right.evaluate(inputs, out, context);
		if ((Boolean) out[0] == m_stopValue)
		{
			return;				
		}
		out[0] = !m_stopValue;
	}
	
	@Override
	public void evaluate(Object[] inputs, Object[] outputs) throws FunctionException
	{
		evaluate(inputs, outputs, null);
	}
	
	protected static class DummyInterpretationFunction extends UnaryFunction<Interpretation,Boolean>
	{
		public static final transient DummyInterpretationFunction instance = new DummyInterpretationFunction();
		
		DummyInterpretationFunction()
		{
			super(Interpretation.class, Boolean.class);
		}

		@Override
		public Boolean getValue(Interpretation x)
		{
			return true;
		}
		
		@Override
		public DummyInterpretationFunction duplicate()
		{
			return this;
		}
	}
	
	public static class And extends LazyBooleanFunction
	{
		public And(Function left, Function right)
		{
			super(false, left, right);
		}
		
		@Override
		public String toString()
		{
			return "(" + m_left + " and " + m_right + ")";
		}
		
		@Override
		public And duplicate(Context context)
		{
			return new And(m_left.duplicate(context), m_right.duplicate(context));
		}
		
		@Override
		public And duplicate()
		{
			return duplicate(null);
		}

	}
	
	public static class Or extends LazyBooleanFunction
	{
		public Or(Function left, Function right)
		{
			super(true, left, right);
		}
		
		@Override
		public String toString()
		{
			return "(" + m_left + " or " + m_right + ")";
		}
		
		@Override
		public Or duplicate(Context context)
		{
			return new Or(m_left.duplicate(context), m_right.duplicate(context));
		}
		
		@Override
		public Or duplicate()
		{
			return duplicate(null);
		}
	}
	
	public static class Implies extends LazyBooleanFunction
	{
		public Implies(Function left, Function right)
		{
			super(true, new FunctionTree(Negation.instance, left), right);
		}
		
		@Override
		public String toString()
		{
			return "(" + m_left + " implies " + m_right + ")";
		}
		
		@Override
		public Implies duplicate(Context context)
		{
			return new Implies(m_left.duplicate(context), m_right.duplicate(context));
		}
		
		@Override
		public Implies duplicate()
		{
			return duplicate(null);
		}

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Function duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getOutputTypeFor(int index) {
		// TODO Auto-generated method stub
		return null;
	}
}
