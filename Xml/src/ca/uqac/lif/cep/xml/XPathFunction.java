package ca.uqac.lif.cep.xml;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.xml.Equality;
import ca.uqac.lif.xml.Predicate;
import ca.uqac.lif.xml.Segment;
import ca.uqac.lif.xml.TextElement;
import ca.uqac.lif.xml.TextSegment;
import ca.uqac.lif.xml.XPathExpression;
import ca.uqac.lif.xml.XmlElement;
import ca.uqac.lif.xml.XPathExpression.XPathParseException;

/**
 * Function that converts a string into an XML element
 */
public class XPathFunction extends Function 
{
	/**
	 * The XPath expression this function evaluates
	 */
	protected final XPathExpression m_expression;
	
	public XPathFunction(String exp)
	{
		super();
		m_expression = parseExpression(exp);
	}
	
	/**
	 * Creates a new XPath function
	 * @param exp The XPath expression to evaluate
	 */
	public XPathFunction(XPathExpression exp)
	{
		super();
		m_expression = exp;
	}
	
	/**
	 * Parses an XPath expression from a string
	 * @param s The string to parse
	 * @return An expression, or <code>null</code> if the parsing failed
	 */
	public static XPathExpression parseExpression(String s)
	{
		XPathExpression out =  null;
		try 
		{
			out = XPathExpression.parse(s);
		} 
		catch (XPathParseException e) 
		{
			// Silently fail
		}
		return out;
	}
	
	@Override
	public String toString()
	{
		return m_expression.toString();
	}
	
	@Override
	public XPathFunction duplicate(boolean with_state)
	{
		return new XPathFunction(m_expression.duplicate());
	}
	
	/**
	 * Replaces all occurrences of placeholders in an XPath expression by
	 * concrete values fetched from some context. Placeholders are
	 * currently only supported in binary predicates, and are identified
	 * by a "$" symbol followed by some name
	 * @param expression The original expression
	 * @param context The context
	 * @return The new expression where placeholders have been replaced
	 */
	protected static XPathExpression evaluatePlaceholders(XPathExpression expression, Context context)
	{
		if (context == null)
		{
			return expression;
		}
		List<Segment> segments = expression.getSegments();
		List<Segment> new_segments = new LinkedList<Segment>();
		for (Segment seg : segments)
		{
			if (seg instanceof TextSegment)
			{
				new_segments.add(seg);
				continue;
			}
			String seg_name = seg.getElementName();
			List<Predicate> new_predicates = new LinkedList<Predicate>();
			Collection<Predicate> preds = seg.getPredicates();
			if (preds != null)
			{
				for (Predicate pred : seg.getPredicates())
				{
					if (pred instanceof Equality)
					{
						Equality eq = (Equality) pred;
						String right = eq.getRight();
						String new_right = right;
						if (right.startsWith("$"))
						{
							// This is a placeholder
							String placeholder_name = right.substring(1);
							if (context.containsKey(placeholder_name))
							{
								Object value = context.get(placeholder_name);
								if (value instanceof String)
								{
									new_right = (String) value;
								}
								else if (value instanceof TextElement)
								{
									new_right = ((TextElement) value).getText();
								}
								else
								{
								  new_right = value.toString();
								}
							}
						}
						Equality new_eq = new Equality(eq.getLeft(), new_right);
						new_predicates.add(new_eq);
					}
					else
					{
						new_predicates.add(pred);
					}
				}			
			}
			Segment new_seg = new Segment(seg_name, new_predicates);
			new_segments.add(new_seg);	
		}
		XPathExpression exp = new XPathExpression(new_segments);
		return exp;
	}

  @Override
  public void evaluate(Object[] inputs, Object[] outputs, Context context, EventTracker tracker)
  {
    XPathExpression n_exp;
    if (context != null)
    {
      n_exp = evaluatePlaceholders(m_expression, context);
    }
    else
    {
      n_exp = m_expression;
    }
    Collection<XmlElement> col = n_exp.evaluate((XmlElement) inputs[0]);
    outputs[0] = postProcess(col);
  }
  
  @Override
  public void evaluate(Object[] inputs, Object[] outputs)
  {
    evaluate(inputs, outputs, null);
  }
  
  @SuppressWarnings("unchecked")
  public Collection<XmlElement> getValue(XmlElement x)
  {
    Object[] out = new Object[1];
    evaluate(new Object[] {x}, out);
    return (Collection<XmlElement>) out[0];
  }

  @Override
  public int getInputArity()
  {
    return 1;
  }

  @Override
  public int getOutputArity()
  {
    return 1;
  }

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int index)
  {
    if (index == 0)
    {
      classes.add(XmlElement.class);
    }
  }

  @Override
  public Class<?> getOutputTypeFor(int index)
  {
    if (index == 0)
    {
      /* The double cast below is a bit of trickery to pass the
       * runtime type of the collection. It was found here:
       * http://stackoverflow.com/a/30754982
       */
      @SuppressWarnings("unchecked")
      Class<Collection<XmlElement>> class1 = (Class<Collection<XmlElement>>) (Object) Collection.class;
      return class1;
    }
    return null;
  }
  
  protected Object postProcess(Collection<XmlElement> col)
  {
    return col;
  }
}