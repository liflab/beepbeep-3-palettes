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
package ca.uqac.lif.cep.apache;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class ParseCommonLogTest 
{
	@Test
	public void test1()
	{
		String line = "::1 - - [01/Mar/2017:09:02:10 -0500] \"GET / HTTP/1.1\" 200 5693 \"-\" \"Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\"";
		ParseCommonLog pcl = ParseCommonLog.instance;
		Object[] out = new Object[1];
		pcl.evaluate(new Object[]{line}, out);
		assertNotNull(out[0]);
		assertTrue(out[0] instanceof HttpRequest);
		HttpRequest req = (HttpRequest) out[0];
		assertEquals(HttpRequest.Method.GET, req.getMethod());
		assertEquals("::1", req.getSourceIp());
		assertEquals("/", req.getPath());
		assertEquals(200, req.getResponseCode());
		assertEquals(5693, req.getResponseSize());
		assertEquals(0, req.getParameters().size());
	}
	
	@Test
	public void test2()
	{
		String line = "::1 - - [01/Mar/2017:09:02:10 -0500] \"POST /blabla.php HTTP/1.1\" 200 5693 \"-\" \"Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\"";
		ParseCommonLog pcl = ParseCommonLog.instance;
		Object[] out = new Object[1];
		pcl.evaluate(new Object[]{line}, out);
		assertNotNull(out[0]);
		assertTrue(out[0] instanceof HttpRequest);
		HttpRequest req = (HttpRequest) out[0];
		assertEquals(HttpRequest.Method.POST, req.getMethod());
		assertEquals("/blabla.php", req.getPath());
		assertEquals(200, req.getResponseCode());
		assertEquals(5693, req.getResponseSize());
	}
	
	@Test
	public void test3()
	{
		String line = "::1 - - [01/Mar/2017:09:02:10 -0500] \"GET /blabla.php?foo=bar HTTP/1.1\" 200 5693 \"-\" \"Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\"";
		ParseCommonLog pcl = ParseCommonLog.instance;
		Object[] out = new Object[1];
		pcl.evaluate(new Object[]{line}, out);
		assertNotNull(out[0]);
		assertTrue(out[0] instanceof HttpRequest);
		HttpRequest req = (HttpRequest) out[0];
		assertEquals(HttpRequest.Method.GET, req.getMethod());
		assertEquals("/blabla.php", req.getPath());
		assertEquals(200, req.getResponseCode());
		assertEquals(5693, req.getResponseSize());
		assertEquals("bar", req.get("foo"));
	}
	
	@Test
	public void testArguments1()
	{
		Map<String,String> args = ParseCommonLog.queryToMap("", HttpRequest.Method.GET);
		assertEquals(1, args.size());
		assertEquals("", args.get(""));
	}
	
	@Test
	public void testArguments2()
	{
		Map<String,String> args = ParseCommonLog.queryToMap("abc", HttpRequest.Method.GET);
		assertEquals(1, args.size());
		assertEquals("", args.get("abc"));
	}
	
	@Test
	public void testArguments3()
	{
		Map<String,String> args = ParseCommonLog.queryToMap("abc=def", HttpRequest.Method.GET);
		assertEquals(1, args.size());
		assertEquals("def", args.get("abc"));
	}
	
	@Test
	public void testArguments4()
	{
		Map<String,String> args = ParseCommonLog.queryToMap("abc=def&foo", HttpRequest.Method.GET);
		assertEquals(2, args.size());
		assertEquals("def", args.get("abc"));
		assertEquals("", args.get("foo"));
	}
	
	@Test
	public void testArguments5()
	{
		Map<String,String> args = ParseCommonLog.queryToMap("foo&abc=def", HttpRequest.Method.GET);
		assertEquals(2, args.size());
		assertEquals("def", args.get("abc"));
		assertEquals("", args.get("foo"));
	}
}
