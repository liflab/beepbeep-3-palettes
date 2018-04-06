/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé and friends

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
package ca.uqac.lif.cep.graphviz;

import java.io.IOException;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.io.CommandRunner;

/**
 * Calls Graphviz on an input string and returns the resulting image
 * as a byte array. 
 * @author Sylvain Hallé
 */
public class CallGraphviz extends UniformProcessor 
{
	/**
	 * Boolean flag indicating if Graphviz is present in the environment
	 */
	private static final transient boolean s_graphvizPresent = checkGraphviz();
	
	public CallGraphviz() 
	{
		super(1, 1);
	}

	@Override
	public CallGraphviz duplicate(boolean with_state) 
	{
		return new CallGraphviz();
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) 
	{
		if (!s_graphvizPresent)
		{
			throw new ProcessorException("Graphviz could not be found in the path");
		}
		byte[] ins = ((String) inputs[0]).getBytes();
		try 
		{
			byte[] image = CommandRunner.runAndGet(new String[]{"neato", "-Tpng"}, ins);
			outputs[0] = image;
		}
		catch (IOException e) 
		{
			throw new ProcessorException(e);
		}
		return true;
	}
	
	/**
	 * Checks if Graphviz is present in the environment by attempting to run
	 * it from the command line
	 * @return {@code true} if Graphviz could be run, {@code false} otherwise
	 */
	private static boolean checkGraphviz()
	{
		return true;
	}

}
