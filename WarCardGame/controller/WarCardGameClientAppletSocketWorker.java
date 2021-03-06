package controller;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.GenericCardGameCard;
import model.GenericCardGamePlayer;
import model.GenericMVCModel;
import model.WarCardGameModel;
import model.WarCardGamePlayer;
/**
 * contains:
 * 
 * public methods:
 * WarCardGameClientAppletSocketWorker(Socket, WarCardGameModel)
 * run()
 * modelChanged()
 * 
 * protected methods:
 * updateModel(GenericMVCModel)
 * 
 */
public class WarCardGameClientAppletSocketWorker extends GenericCardGameSocketWorker
{	
	/**
	 * takes in a socket and a model and sets up a players socket
	 * 
	 * @param socket the socket to be added to the player
	 * @param model the model to be sent to the player
	 * @throws general exception, if anything goes wrong print out the output
	 */
	public WarCardGameClientAppletSocketWorker(Socket socket, WarCardGameModel model)
	{
		super(socket, model , null);
		
		try 
		{
			this.connectionNumber = ((int) this.recieveObject());
			System.out.println("I AM PLAYER " + this.connectionNumber);
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, "Communication with server lost!");
		}
	}

	/**
	 * waits for a new model to be sent, updates the current model when a new one is received
	 */
	@Override
	public void run() 
	{
		//Wait for object on stream
		while(true)
		{
			Object object = null;
			
			try 
			{
				object = this.recieveObject();
			}
			catch (Exception e) 
			{
				System.err.println("SOCKET ERROR - CLOSING CONNECTION");
				
				//e.printStackTrace();
				
				JOptionPane.showMessageDialog(null, "Lost communication to server!");
				
				System.exit(-1);
			}
			
			if(object instanceof WarCardGameModel)
			{
				System.out.println("OLD MODEL: \n" + this.model.toString());
				
				//Update local model with received model, NOTE updateModel does NOT call notifySubscribers!
				synchronized(this.model)
				{
					updateModel((WarCardGameModel)object);
					
					//Notify everyone but this socket worker
					this.model.removeModelSubscriber(this);
					this.model.notifyModelSubscribers();
					this.model.addModelSubscriber(this);
				}
				
				System.out.println("NEW MODEL: \n" + this.model.toString());
			}
			else if(object instanceof Integer)
			{
				this.connectionNumber = (Integer) object;
				System.out.println("I AM PLAYER " + this.connectionNumber);
			}
			else
			{
				System.err.println("Recieved object not type WarCardGameModel!");
			}
		}
	}

	/**
	 * send this user's updated model to all users
	 */
	@Override
	public void modelChanged() 
	{
		//Transmit model to other side
		synchronized(this.model)
		{
			try 
			{
				this.sendObject(model);
			}
			catch (IOException e) 
			{
				System.err.println("SOCKET ERROR - CLOSING CONNECTION");
				//TODO close connection and delete player
				e.printStackTrace();
			}
		}
	}

	/**
	 * takes in data from the new model, compares it to the current model, updates the current model
	 */
	@Override
	protected void updateModel(GenericMVCModel newModel)
	{
		System.out.println("WarCardGameGeneralWorker: UpdateModel");
	
		// Compare interesting parts of the model and update local model
		
		if(((WarCardGameModel)this.model).getPlayers().size() != ((WarCardGameModel)newModel).getPlayers().size())
		{
			((WarCardGameModel)this.model).getPlayers().clear();
			while(!((WarCardGameModel)newModel).getPlayers().isEmpty())
			{
				((WarCardGameModel)this.model).getPlayers().add(((WarCardGameModel)newModel).getPlayers().remove(0));
			}
		}
		else
		{
			for(int i = 0; i < ((WarCardGameModel)this.model).getPlayers().size(); i++)
			{
				WarCardGamePlayer player = ((WarCardGamePlayer)((WarCardGameModel)this.model).getPlayers().get(i));
				WarCardGamePlayer newPlayer = ((WarCardGamePlayer)((WarCardGameModel)newModel).getPlayers().get(i));
				
				if(!player.winPile.equals(newPlayer.winPile))
				{
					player.winPile.clear();
					while(!newPlayer.winPile.isEmpty())
					{
						player.winPile.add(loadCardImages(newPlayer.winPile.remove(0)));
					}
				}
				
				if(!player.flipDeck.equals(newPlayer.flipDeck))
				{
					player.flipDeck.clear();
					while(!newPlayer.flipDeck.isEmpty())
					{
						player.flipDeck.add(loadCardImages(newPlayer.flipDeck.remove(0)));
					}
				}
				
				if(player.cardPlayed != newPlayer.cardPlayed)
				{
					player.cardPlayed = loadCardImages(newPlayer.cardPlayed);
				}
			}
			//For the Chat Box
			if(!((WarCardGameModel)this.model).chatArea.getText().equals(((WarCardGameModel)newModel).chatArea.getText()))
			{
				((WarCardGameModel)this.model).chatArea.setText(((WarCardGameModel)newModel).chatArea.getText());
			}
		}
	
	
		//Load images of cards on client side if not loaded already, by loading client side we significantly reduce transmit size
		String cardPath = "/resources/cards";
		for(GenericCardGamePlayer player : ((WarCardGameModel)this.model).getPlayers())
		{
			String imagePath;
			//Below method works both in Eclipse IDE and JAR's when BACK.png image is in the src folder
			//Toolkit.getDefaultToolkit().getImage(getClass().getResource("/BACK.png"));
			
			//Initially, (on first load and updateModel) all cards are in flip deck so load the flipdeck images
			if(((WarCardGamePlayer) player).flipDeck != null)
			{
				for(GenericCardGameCard card : ((WarCardGamePlayer) player).flipDeck)
				{
					if(card.getCardFace() == null)
					{
						//Load face
						imagePath = cardPath + "/" + card.getSuit() + "/" + card.getValue().toString() + ".png";
						System.err.println("LOAD IMAGE: " + imagePath);
						card.setCardFace(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagePath))));
					}
					
					
					if(card.getCardBack() == null)
					{
						//Load back
						imagePath = cardPath + "/BACK.png";
						System.err.println("LOAD IMAGE: " + imagePath);
						card.setCardBack(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagePath))));				
					}
				}
			}
		}
	}
		
	private GenericCardGameCard loadCardImages(GenericCardGameCard card)
	{
		if(card == null)
		{
			return null;
		}
		
		String cardPath = "/resources/cards";
		String imagePath;

		//Below method works both in Eclipse IDE and JAR's when BACK.png image is in the src folder ie src/*
		//Toolkit.getDefaultToolkit().getImage(getClass().getResource("/BACK.png"));

		if(card.getCardFace() == null)
		{
			//Load face
			imagePath = cardPath + "/" + card.getSuit() + "/" + card.getValue().toString() + ".png";
			//System.err.println("LOAD IMAGE: " + imagePath);
			card.setCardFace(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagePath))));
		}

		if(card.getCardBack() == null)
		{
			//Load back
			imagePath = cardPath + "/BACK.png";
			//System.err.println("LOAD IMAGE: " + imagePath);
			card.setCardBack(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagePath))));				
		}
		
		return card;
	}
}
