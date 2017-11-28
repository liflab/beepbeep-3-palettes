package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.functions.BinaryFunction;

public class BooleanFunction
{
	public static final transient And AND_FUNCTION = new And();
	public static final transient Or OR_FUNCTION = new Or();
	public static final transient Implies IMPLIES_FUNCTION = new Implies();
	
	public static class And extends BinaryFunction<Boolean,Boolean,Boolean>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8470141062728908938L;

		And()
		{
			super(Boolean.class, Boolean.class, Boolean.class);
		}

		@Override
		public Boolean getValue(Boolean x, Boolean y)
		{
			return x.booleanValue() && y.booleanValue();
		}
		
		@Override
		public String toString()
		{
			return "and";
		}
	}
	
	public static class Or extends BinaryFunction<Boolean,Boolean,Boolean>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1223847601828113416L;

		Or()
		{
			super(Boolean.class, Boolean.class, Boolean.class);
		}

		@Override
		public Boolean getValue(Boolean x, Boolean y)
		{
			return x.booleanValue() || y.booleanValue();
		}
		
		@Override
		public String toString()
		{
			return "or";
		}
	}
	
	public static class Implies extends BinaryFunction<Boolean,Boolean,Boolean>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -852550864158965530L;

		Implies()
		{
			super(Boolean.class, Boolean.class, Boolean.class);
		}

		@Override
		public Boolean getValue(Boolean x, Boolean y)
		{
			return !x.booleanValue() || y.booleanValue();
		}
		
		@Override
		public String toString()
		{
			return "implies";
		}
	}

}
