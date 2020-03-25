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
import ca.uqac.lif.cep.Processor;

/**
 * A {@link Hibernate} processor that saves its internal processor into
 * a local file. The name of the file is made of a fixed path, followed
 * by the hibernated processor's unique ID.
 * @author Sylvain Hallé
 */
public class FileHibernate extends Hibernate
{
  /**
   * The printer that is used to print to a file
   */
  protected transient ObjectPrinter<String> m_printer;
  
  /**
   * The reader that is used to read from the file
   */
  protected transient ObjectReader<String> m_reader;
  
  /**
   * Creates a new file hibernate processor.
   * @param p The processor to be hibernated
   * @param path The path where to save the files. Must end with a trailing slash.
   * @param printer The printer that is used to print to a file
   * @param reader The reader that is used to read from the file
   */
  public FileHibernate(Processor p, String path, ObjectPrinter<String> printer, ObjectReader<String> reader)
  {
    super(p, new FileFridge(printer, reader, getFilename(p, path)));
    m_printer = printer;
    m_reader = reader;
  }
  
  /**
   * Creates a filename from a pattern expression and the processor's
   * current context.
   * <p>
   * The method is declared as <tt>public</tt> on purpose, so that it
   * can be tested by external scripts. 
   * @param p The processor for which to create a filename
   * @param path The path to prefix to the filename
   * @return The filename
   */
  public static String getFilename(Processor p, String path)
  {
    return path + p.getId();
  }
}
