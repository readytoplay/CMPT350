package view;

import javax.swing.JPanel;

import model.GenericModel;

public abstract class GenericView extends JPanel
{
	private static final long serialVersionUID = 1L;

	protected GenericModel model;

	protected GenericView(GenericModel model)
	{
		this.model = model;
		this.add(generatePanel());
	}

	protected abstract JPanel generatePanel();
}

