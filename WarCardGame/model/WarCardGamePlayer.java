package model;

import controller.GenericCardGameSocketWorker;


public class WarCardGamePlayer extends GenericCardGamePlayer
{	
	private static final long serialVersionUID = 1L;
	
	public GenericCardGameCard cardPlayed;
	public String name;
	
	public static enum Destination
	{
		HAND, WINPILE
	}

	public WarCardGamePlayer(GenericCardGameSocketWorker socketWorker) 
	{
		super(socketWorker);
		cardPlayed = null;
		name = null;
	}
}