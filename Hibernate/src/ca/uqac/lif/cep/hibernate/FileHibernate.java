/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

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
package ca.uqac.lif.cep.hibernate;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.fridge.FileFridge;
import ca.uqac.lif.azrael.fridge.Fridge;
import ca.uqac.lif.cep.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Hibernate} processor that saves its internal processor into
 * a local file. The name of the file can make references to the processor's
 * context variables. Consider the following piece of code:
 * <pre>
 * FileHibernate h = new FileHibernate("my-file-{$x}.xml", printer, reader, 1, 1);
 * </pre>
 * The pattern <tt>{$x}</tt> refers to the value of variable "x" in the processor's
 * <tt>Context</tt> object.
 * @author Sylvain Hallé
 */
public class FileHibernate extends Hibernate
{
  protected ObjectPrinter<String> m_printer;
  
  protected ObjectReader<String> m_reader;
  
  protected String m_filenamePattern;
  
  protected static Pattern m_pattern = Pattern.compile("\\{\\$(.*?)\\}");
  
  public FileHibernate(String filename_pattern, ObjectPrinter<String> printer, ObjectReader<String> reader, int in_arity, int out_arity)
  {
    super(null, in_arity, out_arity);
    m_filenamePattern = filename_pattern;
    m_printer = printer;
    m_reader = reader;
  }
  
  @Override
  protected Fridge getFridge()
  {
    if (m_fridge == null)
    {
      String filename = getFilename();
      m_fridge = new FileFridge(m_printer, m_reader, filename);
    }
    return m_fridge;
  }
  
  /**
   * Creates a filename from a pattern expression and the processor's
   * current context.
   * <p>
   * The method is declared as <tt>public</tt> on purpose, so that it
   * can be tested by external scripts. 
   * @return The filename
   */
  public String getFilename()
  {
    Context con = getContext();
    String filename_pattern = m_filenamePattern;
    while (filename_pattern.contains("$"))
    {
      Matcher mat = m_pattern.matcher(filename_pattern);
      if (!mat.find())
      {
        break;
      }
      String var_name = mat.group(1);
      if (!con.containsKey(var_name))
      {
        break;
      }
      String value = con.get(var_name).toString();
      filename_pattern = filename_pattern.replaceAll("\\{\\$" + var_name + "\\}", value);
    }
    return filename_pattern;
  }

}
