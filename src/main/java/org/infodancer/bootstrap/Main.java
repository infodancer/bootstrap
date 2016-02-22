package org.infodancer.bootstrap;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A generic bootstrap main class.  When invoked from the command line, this class
 * will treat the first argument as the name of a Runnable class containing the 
 * application startup code.  All other arguments are treated as classpath elements;
 * directories are searched for jar files and those jar files are included in the classpath,
 * while jar files specified directly are also loaded.  Once a classloader has been 
 * defined that includes all specified elements, the class named by the first 
 * argument is instantiated and passed to a new thread.
 * 
 * The new thread has the context class loader set properly.
 * @author matthew
 */
public class Main 
{
	private static final String ARG_PREFIX = "--load";
	
	/**
	 * Creates a ClassLoader from the specified library path, and sets it to be the 
	 * current thread's class loader.
	 * @throws Exception on unexpected errors
	 */
	public static ClassLoader createClassLoader(List<File> jars) throws Exception
	{
		java.util.ArrayList<java.net.URL> urls = new java.util.ArrayList<java.net.URL>();
		for (java.io.File jarFile : jars)
		{
			System.out.println("Adding " + jarFile + " to classloader.");
			urls.add(jarFile.toURI().toURL());
		}

		URLClassLoader loader = new URLClassLoader(urls.toArray(new java.net.URL[urls.size()]));
		Thread.currentThread().setContextClassLoader(loader);
		return loader;
	}
	
	/**
	 * Checks whether a specified file is a jar file by checking the filename extension.
	 */
	public static boolean isJarFile(File file)
	{
		String name = file.getName();
		if (name.endsWith(".jar")) return true;
		else if (name.endsWith(".JAR")) return true;
		else return false;
	}
	
	public static final void main(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				System.out.println("[USAGE] java -jar bootstrap.jar <classname> --load=<classpath element> <args>");
				System.exit(1);
			}
			else
			{
				List<String> newargs = new LinkedList<String>();
				List<File> jars = new ArrayList<File>();
				String className = args[0];
				for (int i = 1; i < args.length; i++)
				{
					if (args[i].startsWith(ARG_PREFIX))
					{
						String filename = args[i].substring(ARG_PREFIX.length() + 1);
						File file = new File(filename);
						if (file.exists())
						{
							if (file.isDirectory())
							{
								File[] files = file.listFiles();
								for (int ii = 0; ii < files.length; ii++)
								{
									if (isJarFile(files[ii])) jars.add(files[ii]);
								}
							}
							else
							{
								if (isJarFile(file)) jars.add(file);
							}
						}
					}
					else newargs.add(args[i]);
				}

				ClassLoader loader = createClassLoader(jars);
				Bootstrapable config = (Bootstrapable) loader.loadClass(className).newInstance();
				config.setArguments(newargs.toArray(new String[newargs.size()]));
				Thread configThread = new Thread(config);
				configThread.setContextClassLoader(loader);
				configThread.start();
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
