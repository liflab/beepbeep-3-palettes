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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ca.uqac.lif.bullwinkle.BnfParser;
import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.bullwinkle.ParseNode;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder;

public class GrammarObjectBuilder<T> extends ParseTreeObjectBuilder<T>
{
	/**
	 * The parser used to parse expressions
	 */
	protected BnfParser m_parser;
	
	public GrammarObjectBuilder()
	{
		super();		
	}
	
	public GrammarObjectBuilder<T> setGrammar(InputStream is) throws InvalidGrammarException
	{
		m_parser = new BnfParser(is);
		return this;
	}
	
	public GrammarObjectBuilder<T> setGrammar(String grammar) throws InvalidGrammarException
	{
		InputStream is = new ByteArrayInputStream(grammar.getBytes());
		return setGrammar(is);
	}
	
	/**
	 * Builds an object from an expression
	 * @param expression The expression to parse
	 * @return The returned object
	 * @throws BuildException
	 */
	public T build(String expression) throws BuildException
	{
		try
		{
			ParseNode node = m_parser.parse(expression);
			return super.build(node);
		}
		catch (ParseException e) 
		{
			throw new BuildException(e);
		}
	}
}
