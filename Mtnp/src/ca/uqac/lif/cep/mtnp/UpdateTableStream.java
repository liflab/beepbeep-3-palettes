package ca.uqac.lif.cep.mtnp;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.mtnp.table.TableEntry;

public class UpdateTableStream extends UpdateTable
{
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
	public UpdateTableStream duplicate(boolean with_state)
	{
		UpdateTableStream uta = new UpdateTableStream(m_table.getColumnNames());
		if (with_state)
		{
			uta.m_table = m_table.getDataTable();
		}
		return uta;
	}
}
