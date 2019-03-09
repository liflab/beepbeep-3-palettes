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
package ca.uqac.lif.cep.json;

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;

/**
 * Extracts a number value from a JSON element.
 * @author Sylvain Hallé
 */
public class NumberValue extends UnaryFunction<JsonElement,Number>
{
  public static final transient NumberValue instance = new NumberValue();
  
  protected NumberValue()
  {
    super(JsonElement.class, Number.class);
  }
  
  @Override
  public Number getValue(JsonElement x)
  {
    if (x instanceof JsonNumber)
    {
      return ((JsonNumber) x).numberValue();
    }
    return 0;
  }
}
