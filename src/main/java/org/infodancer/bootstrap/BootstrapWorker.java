package org.infodancer.bootstrap;

public class BootstrapWorker extends Thread
{
	Class target;
	String[] args;
	
	public BootstrapWorker(Class target, String[] args)
	{
		this.target = target;
		this.args = args;
	}
	
	public void run()
	{
		
	}
}
