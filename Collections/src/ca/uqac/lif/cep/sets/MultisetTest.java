/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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
package ca.uqac.lif.cep.sets;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for the {@link Multiset} class
 */
public class MultisetTest 
{
	@Test
	public void testMultisetEmpty()
	{
		Multiset ms = new Multiset();
		assertEquals(0, ms.size());
		assertTrue(ms.isEmpty());
		assertFalse(ms.contains(0));
		assertEquals(0,  ms.get(0));
		assertNull(ms.getAnyElement());
	}
	
	@Test
	public void testAddAndRemove()
	{
		Multiset ms = new Multiset();
		ms.add("a");
		assertFalse(ms.isEmpty());
		assertEquals(1, ms.size());
		assertEquals(1, ms.get("a"));
		assertEquals("a", ms.getAnyElement());
		ms.add("a");
		assertFalse(ms.isEmpty());
		assertEquals(2, ms.size());
		assertEquals(2, ms.get("a"));
		assertEquals("a", ms.getAnyElement());
		ms.add("b");
		assertEquals(3, ms.size());
		assertEquals(2, ms.get("a"));
		assertEquals(1, ms.get("b"));
		String s = (String) ms.getAnyElement();
		assertTrue(s.compareTo("a") == 0 || s.compareTo("b") == 0);
		ms.remove("a");
		assertEquals(2, ms.size());
		assertEquals(1, ms.get("a"));
		assertEquals(1, ms.get("b"));
		ms.remove("a");
		assertEquals(1, ms.size());
		assertEquals(0, ms.get("a"));
		assertEquals(1, ms.get("b"));
		ms.remove("a");
		assertEquals(1, ms.size());
		assertEquals(0, ms.get("a"));
		assertEquals(1, ms.get("b"));
	}
}
