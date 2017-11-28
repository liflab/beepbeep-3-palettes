package ca.uqac.lif.cep.mtnp;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.mtnp.table.TableEntry;

public class UpdateTableStream extends UpdateTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5638730474856725000L;

	public UpdateTableStream(String ... column_names)
	{
		super(column_names.length, column_names);
	}
	
	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException
	{
		TableEntry e = createEntry(inputs);
		m_table.add(e);
		outputs[0] = m_table;
		return true;
	}
	
	@Override
	public UpdateTableStream duplicate()
	{
		return new UpdateTableStream(m_table.getColumnNames());
	}
}
