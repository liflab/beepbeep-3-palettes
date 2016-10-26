/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
package ca.uqac.lif.cep.tuples;

import java.util.Stack;

class GetAttributeQualified extends GetAttribute
{
	public GetAttributeQualified(String attribute_name) 
	{
		super(attribute_name);
	}
	
	public GetAttributeQualified(String trace_name, String attribute_name) 
	{
		super(trace_name, attribute_name);
	}

	public static void build(Stack<Object> stack)
	{
		String n = (String) stack.pop();
		stack.pop(); // .
		String t = (String) stack.pop();
		GetAttribute ga = new GetAttribute(t, n);
		stack.push(ga);
	}
}
