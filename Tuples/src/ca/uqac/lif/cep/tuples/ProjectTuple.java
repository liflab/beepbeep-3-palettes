/*
    Processor chains for hyperconnected logistics
    Copyright (C) 2018-2019 Laboratoire d'informatique formelle

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.tuples;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tuples.FixedTupleBuilder;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.Context;
import java.util.Set;

/**
 * Creates an output tuple from input tuples by combining their values.
 */
public class ProjectTuple extends Function
{
  /**
   * The function's input arity
   */
  protected int m_inputArity = 1;
  
  /**
   * The list of associations between the output tuple's key and
   * its corresponding value
   */
  protected NameFunctionPair[] m_pairs;
  
  /**
   * The builder used to create the output tuples
   */
  protected FixedTupleBuilder m_builder;
  
  /**
   * Creates a new instance of the function
   * @param input_arity The function's input arity
   * @param pairs The list of associations between the output tuple's key and
   * its corresponding value
   */
  public ProjectTuple(int input_arity, NameFunctionPair ... pairs)
  {
    super();
    m_pairs = pairs;
    String[] names = new String[pairs.length];
    for (int i = 0; i < pairs.length; i++)
    {
      names[i] = pairs[i].m_name;
    }
    m_builder = new FixedTupleBuilder(names);
  }
  
  /**
   * Creates a new instance of the function
   * @param pairs The list of associations between the output tuple's key and
   * its corresponding value
   */
  public ProjectTuple(NameFunctionPair ... pairs)
  {
    this(1, pairs);
  }
  
  @Override
  public void evaluate(Object[] inputs, Object[] outputs, Context context)
  {
    Object[] values = new Object[m_pairs.length];
    for (int i = 0; i < m_pairs.length; i++)
    {
      Object[] out = new Object[1];
      m_pairs[i].m_value.evaluate(inputs, out, context);
      values[i] = out[0];
    }
    outputs[0] = m_builder.createTuple(values);
  }

  @Override
  public int getInputArity()
  {
    return m_inputArity;
  }

  @Override
  public int getOutputArity()
  {
    return 1;
  }

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int index)
  {
    classes.add(Tuple.class);
  }

  @Override
  public Class<?> getOutputTypeFor(int index)
  {
    return Tuple.class;
  }

  @Override
  public Function duplicate(boolean with_state)
  {
    return this;
  }

  /**
   * Associates a tuple's key to a function to be evaluated on input tuples
   */
  public static class NameFunctionPair
  {
    protected String m_name;
    
    protected Function m_value;
    
    public NameFunctionPair(String name, Function value)
    {
      super();
      m_name = name;
      m_value = value;
    }
  }
}
