/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hall�

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
package ca.uqac.lif.cep.http;

import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.SingleProcessor;

/**
 * Unpacks a list of objects by outputting its contents as separate events.
 * This processor is represented graphically as follows:
 * <p>
 * <a href="{@docRoot}/doc-files/ListUnpacker.png"><img
 *   src="{@docRoot}/doc-files/ListUnpacker.png"
 *   alt="Processor graph"></a>
 * 
 * @author Sylvain Hallé
 */
public class ListUnpacker extends SingleProcessor 
{
	/**
	 * Creates a new list unpacker
	 */
	public ListUnpacker()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) inputs[0];
		for (Object o : list)
		{
			outputs.add(new Object[]{o});
		}
		return true;
	}

	@Override
	public ListUnpacker duplicate() 
	{
		return new ListUnpacker();
	}
	
	
}
