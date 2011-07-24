package com.mpdeimos.ant.resourcecompiler;

/**
 * Enumeration interface for string resources loaded from a property file.
 * 
 * @author mpdeimos
 * 
 */
public interface DrawableResourceEnum
{
	/** @return the url represented by this resource identifier. */
	public java.net.URL url();

	/**
	 * Static class with capabilities for loading string resources from a
	 * properties file.
	 */
	public static class DrawableResourceEnumResolver
	{
		/** @return the url represented by a StringResourceEnum constant. */
		public static java.net.URL url(DrawableResourceEnum stringEnum)
		{
			String name = ((Enum<?>) stringEnum).name();
			String className = stringEnum.getClass().getSimpleName().toLowerCase();

			java.net.URL url = DrawableResourceEnumResolver.class.getResource(String.format(
					"/%s/%s.png", className, name.toLowerCase())); //$NON-NLS-1$
			if (url == null)
			{
				if (RBase.getLogger() != null)
					RBase.getLogger().log("Drawable not found: " + name); //$NON-NLS-1$
				url = DrawableResourceEnumResolver.class.getResource(String.format(
						"/%s/default.png", className)); //$NON-NLS-1$
			}

			return url;
		}
	}
}
