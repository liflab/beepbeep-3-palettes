package ca.uqac.lif.cep.tuples;

import ca.uqac.lif.cep.functions.UnaryFunction;

public class NamedMapPlaceholder extends UnaryFunction<Object,Object>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6733058391827585996L;

	protected int m_lastIndex = -1;

	protected String m_name;

	public NamedMapPlaceholder(String name)
	{
		super(Object.class, Object.class);
		m_name = name;
	}

	@Override
	public Object getValue(Object in)
	{
		@SuppressWarnings("unchecked")
		CacheMap<Object> map = (CacheMap<Object>) in;
		Object o;
		if (m_lastIndex == -1)
		{
			m_lastIndex = map.getIndexOf(m_name);
		}
		o = map.getValue(m_lastIndex);
		return o;
	}
}
