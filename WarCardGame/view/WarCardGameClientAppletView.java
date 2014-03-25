package view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.WarCardGameModel;
import model.WarCardGamePlayer;

public class WarCardGameClientAppletView extends GenericCardGameView
{
	private static final long serialVersionUID = 1L;

	public WarCardGameClientAppletView(WarCardGameModel model, final int playerNumber) 
	{
		super(model, playerNumber);
	}

	@Override
	protected void buildPanel() 
	{			
		this.setLayout(new GridBagLayout());
		
		this.setPreferredSize(new Dimension(1000,1000));
		this.setMaximumSize(getPreferredSize());
		this.setMinimumSize(getPreferredSize());
		
		this.setBackground(Color.YELLOW);
		
		this.playerStatus = new JLabel("Waiting for " + (((WarCardGameModel)this.model).getRequiredNumberOfPlayers() - ((WarCardGameModel)model).getPlayers().size()) + " more players...");
		this.add(playerStatus);
	}


	@Override
	public void modelChanged() 
	{	
		if(((WarCardGameModel) model).getPlayers().size() < ((WarCardGameModel) model).getRequiredNumberOfPlayers())
		{
			//REMAKE THE VIEW TO WAIT FOR PLAYERS
			this.removeAll();
			
			this.setBackground(Color.YELLOW);

			this.playerStatus = new JLabel("Waiting for " + (((WarCardGameModel)this.model).getRequiredNumberOfPlayers() - ((WarCardGameModel)model).getPlayers().size()) + " more players...");
			this.add(playerStatus);
		}
		else if(((WarCardGameModel) model).getPlayers().size() == ((WarCardGameModel) model).getRequiredNumberOfPlayers())
		{
			//REMAKE THE VIEW TO DISPLAY GAME
			this.removeAll();

			//Green pepper from http://www.december.com/html/spec/color2.html
			this.setBackground(new Color(0x39, 0x7D, 0x02));

			//Build panel to display game
			createWarCardGameBoardView();
		}
		else
		{
			new Exception("Impossible state, more players than required").printStackTrace();
		}
		
		this.revalidate();
		this.repaint();
	}

	private GenericCardGameCardListView p1Deck;
	private GenericCardGameCardListView p2Deck;
	private GenericCardGameCardListView p1Winpile;
	private GenericCardGameCardListView p2Winpile;

	private void createWarCardGameBoardView() 
	{
		/*
		 * 			x0		x1		x2
		 * 	 	_______________________
		 * 		|p2		|		|p2		|
		 *y0	|deck	|		|winpile|
		 * 		|		|		|		|
		 * 		|_______|_______|_______|
		 * 		|		|p2		|		|
		 *y1	|		|card	|		|
		 * 		|		|played	|		|
		 * 		|_______|_______|_______|
		 * 		|		|p1		|		|
		 *y2 	|		|card	|		|
		 * 		|		|played	|		|
		 * 		|_______|_______|_______|
		 * 		|p1		|		|p1		|
		 *y3 	|deck	|		|winpile|
		 *	 	|		|		|		|
		 *	 	|_______|_______|_______|
		 */


		// We create a JPanel with the GridLayout.
		//this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		this.setPreferredSize(new Dimension(400,400));
		this.setMaximumSize(getPreferredSize());
		this.setMinimumSize(getPreferredSize());
		gbc.fill= GridBagConstraints.BOTH;
		gbc.weightx = 0.33;
		gbc.weighty = 0.25;

		//TODO  Make all the approriate fields in the player object
		p1Deck = new GenericCardGameCardListView(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).flipDeck);
		p2Deck = new GenericCardGameCardListView(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).flipDeck);
		p1Winpile = new GenericCardGameCardListView(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).winPile);
		p2Winpile = new GenericCardGameCardListView(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).winPile);

		JButton p1flip = new JButton("Player 1 Flip!");
		JButton p2flip = new JButton("Player 2 Flip!");

		p1flip.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//Remove top card from flip deck and make it the played card
				synchronized(model)
				{
					if(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).cardPlayed == null)
					{
						((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).cardPlayed = ((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).flipDeck.remove(0);

						model.notifyModelSubscribers();
					}
				}
			}

		});

		p2flip.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//Remove top card from flip deck and make it the played card
				synchronized(model)
				{
					if(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).cardPlayed == null)
					{
						((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).cardPlayed = ((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).flipDeck.remove(0);

						model.notifyModelSubscribers();
					}
				}
			}
		});



		gbc.ipady=50;
		gbc.gridx=0;
		gbc.gridy=0;
		this.add(p2Deck, gbc);

		gbc.gridx=2;
		gbc.gridy=0;
		this.add(p2Winpile, gbc);

		if(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).cardPlayed != null)
		{
			gbc.gridx=1;
			gbc.gridy=1;
			this.add(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(1)).cardPlayed, gbc);
		}

		if(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).cardPlayed != null)
		{
			gbc.gridx=1;
			gbc.gridy=2;
			this.add(((WarCardGamePlayer)((WarCardGameModel)model).getPlayers().get(0)).cardPlayed, gbc);
		}

		gbc.gridx=0;
		gbc.gridy=3;
		this.add(p1Deck, gbc);

		gbc.gridx=2;
		gbc.gridy=3;
		this.add(p1Winpile, gbc);

		gbc.fill = GridBagConstraints.NONE;

		if(this.playerNumber == 2)
		{ 
			gbc.gridx = 1;
			gbc.gridy = 0;
			this.add(p2flip, gbc);
		}

		if(this.playerNumber == 1)
		{
			gbc.gridx = 1;
			gbc.gridy = 3;
			this.add(p1flip, gbc);
		}

		this.setOpaque(true);	
	}
}
