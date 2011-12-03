package com.mpdeimos.ant.resourcecompiler;

/**
 * abstract base class for the resource manager.
 * 
 * @author mpdeimos
 */
public abstract class RBase
{
	/** the logger interface. */
	private static ILogger logger;
	
	static
	{
		logger = new ILogger()
		{
			public void log(String message)
			{
				// dummy
			}
		};
	}

	/** sets the logger for the resource manager. */
	protected static void setLogger(ILogger logger)
	{
		RBase.logger = logger;
	}
	
	/** @return the logger of the resource manager. */
	/* package */ static ILogger getLogger()
	{
		return logger;
	}
}
