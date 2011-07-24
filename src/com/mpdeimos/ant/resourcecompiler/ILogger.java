package com.mpdeimos.ant.resourcecompiler;

/**
 * Interface for receiving logging messages.
 * 
 * @author mpdeimos
 *
 */
public interface ILogger {
	/** called on log event. */
	public void log(String message);
}
