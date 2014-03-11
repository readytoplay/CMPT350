package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Card extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 71;
	public static final int HEIGHT = 96;
	
	public static enum Value
	{
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT,
		NINE, TEN, JACK, QUEEN, KING
	}
	
	public static enum Suit
	{
		CLUB, HEART, SPADE, DIAMOND
	}
	
	public static enum Display
	{
		FACEUP, FACEDOWN
	}

	Value value;
	Suit suit;
	Display display;
	
	BufferedImage cardFace;
	BufferedImage cardBack;
	
	public Card(Value value, Suit suit, Display display)
	{
		super();
		
		this.value = value;
		this.suit = suit;
		this.display = display;
				
		cardFace = null;
		cardBack = null;
		try 
		{
			//Filepath's originate at the root folder of the project itself
			//to get the image, step up one level to CMPT350 folder with '../'
			//then drill down into 'Common' and find the resource needed
			String imagePath;
			imagePath = "../Common/resources/cards/" + suit + "/" + value + ".png";
			cardFace = ImageIO.read(new File(imagePath));
			
			imagePath = "../Common/resources/cards/BACK.png";
			cardBack = ImageIO.read(new File(imagePath));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(getPreferredSize());
		this.setMinimumSize(getPreferredSize());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		if(this.display.equals(Card.Display.FACEUP))
		{
			if(cardFace == null)
			{
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(2));
				g2.drawRect(0, 0, WIDTH, HEIGHT);
			}
			else
			{
				g2.drawImage(cardFace, 0, 0, null);
			}
		}
		else if(this.display.equals(Card.Display.FACEDOWN))
		{
			if(cardBack == null)
			{
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(2));
				g2.drawRect(0, 0, WIDTH, HEIGHT);
			}
			else
			{
				g.drawImage(cardBack, 0, 0, null);
			}
		}
	}
}