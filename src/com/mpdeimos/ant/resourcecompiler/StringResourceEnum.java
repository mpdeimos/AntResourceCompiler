package com.mpdeimos.ant.resourcecompiler;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Enumeration interface for string resources loaded from a property file.
 * 
 * @author mpdeimos
 * 
 */
public interface StringResourceEnum
{
	/** @return the string represented by this resource identifier. */
	public String string();

	/**
	 * Static class with capabilities for loading string resources from a
	 * properties file.
	 */
	public static class StringResourceEnumResolver
	{
		/** default string if resource not found */
		public static final String NOT_FOUND = "## ERROR ##"; //$NON-NLS-1$

		/** the package name for the string bundle. */
		private static final String bundlePrefix = "string."; //$NON-NLS-1$

		/** resource bundle HashMap for strings */
		private static final HashMap<String, ResourceBundle> resourceBundles = new HashMap<String, ResourceBundle>();

		/** @return the string represented by a StringResourceEnum constant. */
		public static String string(StringResourceEnum stringEnum)
		{
			String name = ((Enum<?>) stringEnum).name();
			String className = stringEnum.getClass().getSimpleName().toLowerCase();

			ResourceBundle resourceBundle = resourceBundles.get(className);
			if (resourceBundle == null)
			{
				try
				{
					resourceBundle = ResourceBundle.getBundle(bundlePrefix
							+ className);
					resourceBundles.put(className, resourceBundle);
				}
				catch (Exception e)
				{
					if (RBase.getLogger() != null)
						RBase.getLogger().log(e.getMessage());
					return NOT_FOUND;
				}
			}

			String str = null;
			try
			{
				str = resourceBundle.getString(name.toLowerCase());
			}
			catch (Exception e)
			{
				// Swallow, will be handled in finally
			}
			finally
			{
				if (str == null)
				{
					if (RBase.getLogger() != null)
						RBase.getLogger().log("String not found: " + name); //$NON-NLS-1$
					str = NOT_FOUND;
				}
			}
			return str;
		}
	}
}
