/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.cep.Connector.Variant;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;

/**
 * Function that applies a serializer to its input argument.
 * An {@link ca.uqac.lif.cep.functions.ApplyFunction ApplyFunction}
 * that applies a serialization function will be depicted graphically by
 * this pictogram:
 * <p>
 * <a href="{@docRoot}/doc-files/Serialization.png"><img
 *   src="{@docRoot}/doc-files/Serialization.png"
 *   alt="Processor graph"></a>
 * <p>
 * The picture represents an event that is "packed" into a box with a
 * barcode, representing its serialized form.
 * @author Sylvain Hallé
 *
 * @param <T> The type of the serialized objects
 */
public class SerializeEvents<T> extends Function 
{
	/**
	 * The serializer
	 */
	protected transient ObjectPrinter<T> m_serializer;
	
	/**
	 * A reference to the class of the serialized objects. This
	 * is used in {@link #getOutputTypeFor(int)} to return the output
	 * type for this processor.
	 */
	protected final transient Class<T> m_outputType;
	
	/**
	 * Creates a new instance of the function.
	 * @param s The serializer used to serialize the function's
	 *   arguments
	 * @param output_type A reference to the class of the
	 *   serialized objects 
	 */
	public SerializeEvents(ObjectPrinter<T> s, Class<T> output_type)
	{
		super();
		m_serializer = s;
		m_outputType = output_type;
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context)  
	{
		evaluate(inputs, outputs);
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context, EventTracker tracker) 
	{
		try 
		{
			T serialized = m_serializer.print(inputs[0]);
			outputs[0] = serialized;
		} 
		catch (PrintException e)
		{
			throw new FunctionException(e);
		}
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
	public SerializeEvents<T> duplicate(boolean with_state)
	{
		return new SerializeEvents<T>(m_serializer, m_outputType);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index) 
	{
		classes.add(Variant.class);
	}

	@Override
	public Class<?> getOutputTypeFor(int index) 
	{
		return m_outputType;
	}

}
