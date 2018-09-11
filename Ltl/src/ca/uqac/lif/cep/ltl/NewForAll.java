package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import java.util.List;

public class NewForAll extends NewQuantifier
{
  public NewForAll(String variable_name, Function domain_function, Processor expression)
  {
    super(variable_name, domain_function, expression);
  }

  @Override
  protected Object collectValues(List<?> values)
  {
    for (Object o : values)
    {
      if (o == null)
      {
        return null;
      }
      if (o instanceof Boolean && !(Boolean) o)
      {
        return false;
      }
    }
    return true;
  }
  
  @Override
  protected boolean canClean(Object o)
  {
    return o != null;
  }

  @Override
  public NewForAll duplicate(boolean with_state)
  {
    NewForAll nfa = new NewForAll(m_variableName, m_domainFunction, m_expression);
    nfa.setContext(m_context);
    return nfa;
  }
}
