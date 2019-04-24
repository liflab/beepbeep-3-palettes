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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for the {@link FileHibernate} processor.
 * @author Sylvain Hallé
 */
public class FileHibernateTest
{
  @Test
  public void testFilename1()
  {
    FileHibernate fh = new FileHibernate("my-test-{$x}.xml", null, null, 1, 1);
    fh.setContext("x", "foo");
    assertEquals("my-test-foo.xml", fh.getFilename());
  }
}
