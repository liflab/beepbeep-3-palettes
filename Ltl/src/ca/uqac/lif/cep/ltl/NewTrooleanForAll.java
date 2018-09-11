package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import java.util.List;

public class NewTrooleanForAll extends NewQuantifier
{
  public NewTrooleanForAll(String variable_name, Function domain_function, Processor expression)
  {
    super(variable_name, domain_function, expression);
  }
  
  @Override
  protected boolean canClean(Object o)
  {
    return o instanceof Troolean.Value 
        && ((Troolean.Value) o) != Troolean.Value.INCONCLUSIVE;
  }

  @Override
  protected Object collectValues(List<?> values)
  {
    boolean seen_false = false;
    boolean seen_inconclusive = false;
    for (Object o : values)
    {
      if (o == null)
      {
        return null;
      }
      if (o instanceof Troolean.Value)
      {
        Troolean.Value t = (Troolean.Value) o;
        if (t == Troolean.Value.INCONCLUSIVE)
        {
          seen_inconclusive = true;
        }
        if (t == Troolean.Value.FALSE)
        {
          seen_false = true;
        }
      }
    }
    if (seen_false)
    {
      return Troolean.Value.FALSE;
    }
    if (seen_inconclusive || values.isEmpty())
    {
      return Troolean.Value.INCONCLUSIVE;
    }
    return Troolean.Value.TRUE;
  }

  @Override
  public NewTrooleanForAll duplicate(boolean with_state)
  {
    NewTrooleanForAll nfa = new NewTrooleanForAll(m_variableName, m_domainFunction, m_expression);
    nfa.setContext(m_context);
    return nfa;
  }
}
