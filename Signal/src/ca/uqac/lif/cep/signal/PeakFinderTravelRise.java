package ca.uqac.lif.cep.signal;

import java.util.Queue;

import ca.uqac.lif.cep.util.Numbers.NumberCast;

/**
 * Finds peaks in a sequence of numerical values using the "travel-rise"
 * technique. This works <a href="http://stackoverflow.com/a/44357">as follows</a>:
 * <p>
 * Between any two points in your data, (x(0),y(0)) and (x(n),y(n)),
 * add up y(i+1)-y(i) for 0 &lt;= i &lt; n and call this T ("travel") and set R 
 * ("rise") to y(n)- y(0) + k for suitably small k. T/R &gt; 1 indicates a 
 * peak. This works OK if large travel due to noise is unlikely or if noise 
 * distributes symmetrically around a base curve shape. 
 * 
 * @author Sylvain
 *
 */
public class PeakFinderTravelRise extends WindowProcessor
{
	
	protected static final float s_k = 1f;
	
	public PeakFinderTravelRise(int width)
	{
		super(width);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		m_values.addElement(NumberCast.getNumber(inputs[0]).floatValue());
		if (m_values.size() == m_windowWidth + 1)
		{
			m_values.remove(0);
		}
		if (m_values.size() == m_windowWidth)
		{
			float T = 0;
			boolean first = true;
			float last_value = 0;
			for (float f : m_values)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					T += (f - last_value);
				}
				last_value = f;
			}
			float R = m_values.lastElement() - m_values.firstElement() + s_k;
			if (T/R > 1)
			{
				// Declare a peak
				outputs.add(new Object[]{computeOutputValue()});
				return true;
			}
		}
		outputs.add(new Object[]{0});
		return true;
	}
	
	@Override
	public PeakFinderTravelRise duplicate(boolean with_state)
	{
		return new PeakFinderTravelRise(m_windowWidth);
	}

	@Override
	public Float computeOutputValue()
  {
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    int min_pos = 0;
    int max_pos = 0;
    int cnt = 0;
    for (float f : m_values)
    {
      if (f < min)
      {
        min = f;
        min_pos = cnt;
      }
      if (f > max)
      {
        max = f;
        max_pos = cnt;
      }
      cnt++;
    }
    if (min_pos < max_pos)
    {
      return max - min;
    }
    return min - max;
  }
}
