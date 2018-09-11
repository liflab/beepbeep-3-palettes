package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.SinkLast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public abstract class NewQuantifier extends SynchronousProcessor
{
  protected String m_variableName;

  protected Processor m_expression;

  protected Function m_domainFunction;

  protected List<Pushable> m_pushables;

  protected List<SinkLast> m_sinks;

  public NewQuantifier(String variable_name, Function domain_function, Processor expression)
  {
    super(1, 1);
    m_variableName = variable_name;
    m_domainFunction = domain_function;
    m_expression = expression;
    m_pushables = new ArrayList<Pushable>();
    m_sinks = new ArrayList<SinkLast>();
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Object[] dom_out = new Object[1];
    m_domainFunction.evaluate(inputs, dom_out);
    for (Object v : (Collection<?>) dom_out[0])
    {
      Processor new_exp = m_expression.duplicate();
      new_exp.setContext(m_context);
      new_exp.setContext(m_variableName, v);
      Pushable p = new_exp.getPushableInput();
      m_pushables.add(p);
      SinkLast s = new SinkLast();
      Connector.connect(new_exp, s);
      m_sinks.add(s);
    }
    for (Pushable p : m_pushables)
    {
      p.push(inputs[0]);
    }
    List<Object> out_vals = new ArrayList<Object>(m_sinks.size());
    for (int i = m_sinks.size() - 1; i >= 0; i--)
    {
      SinkLast s = m_sinks.get(i);
      Object[] oa = s.getLast();
      if (oa != null)
      {
        out_vals.add(oa[0]);
        if (canClean(oa[0]))
        {
          m_sinks.remove(i);
          m_pushables.remove(i);
        }
      }
    }
    Object o = collectValues(out_vals);
    if (o != null)
    {
      outputs.add(new Object[] {o});
    }
    return true;
  }

  protected abstract Object collectValues(List<?> values);
  
  protected abstract boolean canClean(Object o);
}
