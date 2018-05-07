package ca.uqac.lif.cep.gnuplot;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Deprecated
public class Multiset implements Set<Object>
{
  protected final Map<Object,Integer> m_map;
  
  public Multiset()
  {
    super();
    m_map = new HashMap<Object,Integer>();
  }
  
  @Override
  public int size()
  {
    int size = 0;
    for (Integer n : m_map.values())
    {
      size += n;
    }
    return size;
  }

  @Override
  public boolean isEmpty()
  {
    return m_map.isEmpty();
  }

  @Override
  public boolean contains(Object o)
  {
    return m_map.containsKey(o);
  }

  @Override
  public Iterator<Object> iterator()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object[] toArray()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean add(Object e)
  {
    return add(e, 1);
  }
  
  public boolean add(Object e, int times)
  {
    int n = 0;
    if (!m_map.containsKey(e))
    {
      m_map.put(e, 0);
    }
    else
    {
      n = m_map.get(e);
    }
    n += times;
    m_map.put(e, n);
    return true;
  }

  @Override
  public boolean remove(Object o)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Object> c)
  {
    if (c instanceof Multiset)
    {
      Multiset ms = (Multiset) c;
      for (Map.Entry<Object,Integer> entry : ms.m_map.entrySet())
      {
        add(entry.getKey(), entry.getValue());
      }
    }
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void clear()
  {
    m_map.clear();
  }
  
  public Set<Object> keySet()
  {
    return m_map.keySet();
  }

  /**
   * Gets the number of occurrences of an element in the multiset
   * @param key The element
   * @return The number of times this element is present in the multiset
   */
  public int get(Object key)
  {
    if (!m_map.containsKey(key))
    {
      return 0;
    }
    return m_map.get(key);
  }

  /**
   * Fetches any element for the multiset 
   * @return An arbitrary element from the set, or {@code null} if
   * the multiset is empty
   */
  public Object getAnyElement()
  {
    Object o = null;
    for (Object o2 : m_map.keySet())
    {
      o = o2;
      break;
    }
    return o;
  }
  
  public static class PutInto extends UniformProcessor
  {
    protected Multiset m_set;
    
    public PutInto()
    {
      super(1, 1);
      m_set = new Multiset();
    }

    @Override
    protected boolean compute(Object[] inputs, Object[] outputs)
    {
      m_set.add(inputs[0]);
      outputs[0] = m_set;
      return true;
    }

    @Override
    public Processor duplicate(boolean with_state)
    {
      PutInto pi = new PutInto();
      if (with_state)
      {
        pi.m_set.addAll(m_set);
      }
      return pi;
    }
  }
  
  @Override
  public String toString()
  {
    return m_map.toString();
  }
}
