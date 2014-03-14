import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import controller.GenericController;
import view.GenericView;
import model.GenericModel;


public abstract class GenericClientApplet extends JApplet 
{
	private static final long serialVersionUID = 1L;
	
	protected GenericModel model;
	protected GenericView view;
	protected GenericController controller;
	
	public void init()
	{
		try 
        {
            SwingUtilities.invokeAndWait(new Runnable() 
            {
                public void run() 
                {
                	initializeModel();
                	initializeView();
                	initializeController();
                }
            });
        }
        catch (Exception e) 
        {
            System.err.println("createGUI didn't successfully complete");
            e.printStackTrace();
        } 		
	}
	
	protected abstract void initializeModel();
	protected abstract void initializeView();
	protected abstract void initializeController();
}
