package org.infodancer.bootstrap;

/**
 * Marks a class that can be used as the target of a bootstrap classloader configuration via this package.
 * @author matthew
 */
public interface Bootstrapable extends Runnable
{
	/** 
	 * Set the arguments provided via the command line, without the arguments used by bootstrap itself.
	 */
	public void setArguments(String[] args);
	
	/**
	 * Retrieve the arguments provided.
	 */
	public String[] getArguments();
}
