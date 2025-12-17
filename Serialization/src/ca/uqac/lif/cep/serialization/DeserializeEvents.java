/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2025 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.serialization;

import java.util.Set;

import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;

/**
 * Function that applies a deserializer to its input argument.
 * A {@link ca.uqac.lif.cep.functions.ApplyFunction FunctionProcessor}
 * that applies a serialization function will be depicted graphically by
 * this pictogram:
 * <p>
 * <a href="{@docRoot}/doc-files/Deserialization.png"><img
 *   src="{@docRoot}/doc-files/Deserialization.png"
 *   alt="Processor graph"></a>
 * <p>
 * The picture represents an event that is "unpacked" from a box with a
 * barcode, representing its serialized form.
 * @author Sylvain Hallé
 *
 * @param <T> The type of the deserialized objects
 */
public class DeserializeEvents<T,U> extends Function 
{
	/**
	 * The serializer
	 */
	protected transient ObjectReader<T> m_serializer;
	
	/**
	 * A reference to the class of the serialized objects. This
	 * is used in {@link #getInputTypesFor(Set, int)} to define the input
	 * type for this processor.
	 */
	protected final transient Class<T> m_inputType;
	
	/**
	 * A reference to the class of the serialized objects. This
	 * is used in {@link #getOutputTypeFor(int)} to return the output
	 * type for this processor.
	 */
	protected final transient Class<U> m_outputType;
	
	/**
	 * Creates a new instance of the function.
	 * @param s The object reader used to deserialize the function's
	 *   arguments
	 * @param input_type A reference to the class of the
	 *   serialized objects given as an argument
	 * @param output_type A reference to the class of the
	 *   deserialized objects 
	 */
	public DeserializeEvents(ObjectReader<T> s, Class<T> input_type, Class<U> output_type)
	{
		super();
		m_serializer = s;
		m_inputType = input_type;
		m_outputType = output_type;
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context)  
	{
	  try 
    {
      @SuppressWarnings("unchecked")
      Object deserialized = m_serializer.read((T) inputs[0]);
      outputs[0] = deserialized;
    } 
    catch (ReadException e)
    {
      throw new FunctionException(e);
    }
		
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs) 
	{
	  evaluate(inputs, outputs, null);
	}

	@Override
	public int getInputArity()
	{
		return 1;
	}

	@Override
	public int getOutputArity()
	{
		return 1;
	}

	@Override
	public void reset()
	{
		super.reset();
	}

	@Override
	public DeserializeEvents<T,U> duplicate(boolean with_state)
	{
		return new DeserializeEvents<T,U>(m_serializer, m_inputType, m_outputType);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index) 
	{
		classes.add(m_inputType);
	}

	@Override
	public Class<?> getOutputTypeFor(int index) 
	{
		return m_outputType;
	}

}
