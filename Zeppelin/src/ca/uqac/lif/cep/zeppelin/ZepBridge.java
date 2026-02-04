/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2026 Sylvain Hallé

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
package ca.uqac.lif.cep.zeppelin;

import java.io.OutputStream;

public final class ZepBridge
{
	/**
	 * Unreachable constructor.
	 */
	private ZepBridge()
	{
		super();
	}

	/**
	 * Gets the PrintStream corresponding to Zeppelin's output console. If not
	 * running in Zeppelin, returns System.out.
	 * @return The PrintStream
	 */
	public static OutputStream out()
	{
		try
		{
			Class<?> ctxClass = Class.forName("org.apache.zeppelin.interpreter.InterpreterContext");
			Object ctx = ctxClass.getMethod("get").invoke(null);
			if (ctx == null)
				return System.out;

			// ctx.out is a public field in Zeppelin
			Object ps = ctx.getClass().getField("out").get(ctx);
			return (OutputStream) ps;
		}
		catch (Throwable t)
		{
			return System.out;
		}
	}
	
	/**
	 * Determines whether the current code is running inside a Zeppelin
	 * interpreter. 
	 * @return true if running in Zeppelin, false otherwise
	 */
	public static boolean inZeppelin()
	{
		try
		{
			Class<?> ctxClass = Class.forName("org.apache.zeppelin.interpreter.InterpreterContext");
			return ctxClass.getMethod("get").invoke(null) != null;
		}
		catch (Throwable t)
		{
			return false;
		}
	}
}
