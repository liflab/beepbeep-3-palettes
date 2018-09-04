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
package ca.uqac.lif.cep.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.cep.tuples.TupleMap;

/**
 * Converts a query to a database into a trace of named tuples.
 */
public class JdbcSource extends Source 
{
  /**
   * The name of the database table to read from. Actually, this does not need
   * to be a table name, as any SQL expression that returns a table (e.g.
   * a <code>SELECT</code> statement) can do.
   */
  protected final String m_query;

  /**
   * The database connection object
   */
  protected Connection m_connection = null;

  /**
   * The statement object corresponding to the SQL query being executed 
   */
  protected Statement m_statement = null;

  /**
   * The query's result set, out of which tuples will be extracted one
   * by one
   */
  protected ResultSet m_resultSet = null; 

  /**
   * Whether the tuples of the underlying relation should be output
   * one by one on every call to {@link #compute(Object[], Queue)}, or
   * output all at once on the first call to that method.
   */
  protected boolean m_feedOneByOne;

  /**
   * Remembers whether the query has already been run on the database
   */
  protected boolean m_hasRun = false;

  /**
   * Builds a JDBC source.
   * @param conn An open connection on the database
   * @param tablename The name of the table to be read from. Actually, this
   *   does not need
   * to be a table name, as any SQL expression that returns a table (e.g.
   * a <code>SELECT</code> statement) can do.
   */
  public JdbcSource(Connection conn, String tablename)
  {
    super(1);
    m_connection = conn;
    m_query = tablename;
    m_feedOneByOne = true;
  }

  /**
   * Sets whether the tuples of the underlying relation should be output
   * one by one on every call to {@link #compute(Object[], Queue)}, or
   * output all at once on the first call to that method. While this
   * has no effect on the end result, it might have an impact on the
   * performance (e.g. if the source outputs a very large number of
   * tuples in the output queue, which must be stored in memory).
   * @param b Set to <code>true</code> to feed the tuples one by one,
   *   false otherwise
   */
  public void setFeedOneByOne(boolean b)
  {
    m_feedOneByOne = b;
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    if (m_hasRun)
    {
      // Done; no more events
      return false;
    }
    if (m_connection == null)
    {
      try 
      {
        m_statement = m_connection.createStatement();
        m_resultSet = m_statement.executeQuery(m_query);
      } 
      catch (SQLException e) 
      {
        e.printStackTrace();
      }
    }
    try 
    {
      ResultSetMetaData metadata = m_resultSet.getMetaData();
      List<String> col_names = new ArrayList<String>();
      int col_count = metadata.getColumnCount();
      for (int i = 0; i < col_count; i++)
      {
        col_names.add(metadata.getColumnName(i));
      }
      while (m_resultSet.next())
      {
        TupleMap nt = new TupleMap();
        for (int i = 1; i <= col_count; i++)
        {
          String name = col_names.get(i);
          Object value = m_resultSet.getObject(i);
          if (value instanceof String)
          {
            nt.put(name, (String) value);
          }
          else if (value instanceof Number)
          {
            nt.put(name, value);
          }
        }
        outputs.add(new Object[]{nt});
      }
      m_hasRun = true;
      return true;
    } 
    catch (SQLException e) 
    {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public JdbcSource duplicate(boolean with_state)
  {
    return new JdbcSource(m_connection, m_query);
  }

  @Override
  public void reset()
  {
    m_hasRun = false;
  }
}
