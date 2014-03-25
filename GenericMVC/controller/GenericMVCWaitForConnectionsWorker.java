package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import model.GenericMVCModel;

public abstract class GenericMVCWaitForConnectionsWorker implements Runnable
{
	protected final int port;
	protected ServerSocket serverSocket;
	protected GenericMVCModel model;
	protected final int maxConnections;
	protected int currentConnections;
	
	protected GenericMVCWaitForConnectionsWorker(final int port, final int maxConnections, GenericMVCModel model) throws IOException
	{
		//http://www.oracle.com/technetwork/java/socket-140484.html
		this.model = model;
		
		this.port = port;
		this.serverSocket = new ServerSocket(port);
		this.maxConnections = maxConnections;
		if(this.serverSocket != null)
		{
			System.out.println("Server listening on port: " + port);
		}
	}
	
	@Override
	public void run()
	{		
		while(currentConnections < maxConnections)
		{
			GenericMVCSocketWorker worker;
			try
			{
				//server.accept returns a client connection
				System.out.println("WAITING FOR CONNECTION");

				Socket socket = serverSocket.accept();
				System.out.println("CONNECTION ACCEPTED");

				worker = createServerSocketWorker(socket, model);
				System.out.println("SOCKET WORKER CREATED");

				(new Thread(worker)).start();
				System.out.println("THREAD STARTED");

				model.notifyModelSubscribers();
				System.out.println("SUBSCRIBERS NOTIFIED");

				currentConnections++;
				System.out.println(currentConnections + " CONNECTIONS");
			} 
			catch (IOException e) 
			{
				System.err.println("Accept failed: " + port);
				System.exit(-1);
			}
		}

		System.out.println("STOPPED ACCEPTING CONNECTIONS");
	}
	protected abstract GenericMVCSocketWorker createServerSocketWorker(Socket socket, GenericMVCModel model);
}
