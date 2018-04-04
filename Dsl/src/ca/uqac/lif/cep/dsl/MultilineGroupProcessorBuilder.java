/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.dsl;

import java.util.Deque;
import java.util.Scanner;

import ca.uqac.lif.cep.GroupProcessor;

public abstract class MultilineGroupProcessorBuilder extends GroupProcessorBuilder
{
	/**
	 * A character used to mark comment lines
	 */
	protected String m_commentChar = "#";
	
	@Override
	public GroupProcessor build(String expression) throws BuildException 
	{
		Scanner scanner = new Scanner(expression);
		while (scanner.hasNextLine()) 
		{
			String line = scanner.nextLine().trim();
			if (line.isEmpty() || (!m_commentChar.isEmpty() && line.startsWith(m_commentChar)))
				continue;
			buildLine(line);
		}
		scanner.close();
		return endOfFileVisit();
	}
	
	/**
	 * Sets the character used to mark comment lines
	 * @param cc The character. Set it to the empty string to disable
	 * comment characters
	 */
	public void setCommentChar(String cc)
	{
		m_commentChar = cc;
	}
	
	public void buildLine(String line) throws BuildException
	{
		super.build(line);
	}
	
	@Override
	protected synchronized GroupProcessor postVisit(Deque<Object> stack) 
	{
		return null;
	}
	
	public abstract GroupProcessor endOfFileVisit();
}
