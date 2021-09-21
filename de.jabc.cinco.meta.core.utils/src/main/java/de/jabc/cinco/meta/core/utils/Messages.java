package de.jabc.cinco.meta.core.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class which helps with managing messages by means of separate
 * properties file.
 */
public class Messages {

	private static final String RESOURCE_BUNDLE_SUFFIX = ".messages";
	
	private static HashMap<?, Messages> cache = new HashMap<>();
    private ResourceBundle resourceBundle;

    /*
     * private constructor as this class is to be used as a singleton
     */
    private Messages(String resourceBundle) {
    		this.resourceBundle = ResourceBundle.getBundle(resourceBundle);
    }
    
	/**
	 * Returns a new instance of this utility class according to the specified
	 * bundle name.
	 * 
	 * @param resourceBundle
	 *            the bundle name
	 * @return the instance of this utility class
	 */
    public static Messages resp(String resourceBundle) {
    		return new Messages(resourceBundle);
    }
    
	/**
	 * Returns an instance of this utility class using the package name of
	 * the specified class as bundle name by adding the default suffix '.messages'.
	 * 
	 * @param cls
	 *            the class
	 * @return the instance of this utility class
	 */
	public static Messages resp(Class<?> cls) {
		Messages instance = cache.get(cls);
		return (instance != null)
			   ? instance
			   : new Messages(cls.getPackage().getName() + RESOURCE_BUNDLE_SUFFIX);
	}
    
	/**
	 * Returns an instance of this utility class using the package name of
	 * the class of the specified object as bundle name by adding the default
	 * suffix '.messages'.
	 * 
	 * @param object
	 *            the object
	 * @return the instance of this utility class
	 */
    public static Messages resp(Object object) {
    		Messages instance = cache.get(object.getClass());
		return (instance != null)
			   ? instance
			   : new Messages(object.getClass().getPackage().getName() + RESOURCE_BUNDLE_SUFFIX);
    }

	/**
	 * Returns the formatted message for the given key in the resource bundle.
	 *
	 * @param key
	 *            the resource name
	 * @param args
	 *            the message arguments
	 * @return the string
	 */
    public String format(String key, Object[] args) {
        return MessageFormat.format(get(key), args);
    }

	/**
	 * Returns the resource object with the given key in the resource bundle. If
	 * there isn't any value under the given key, the key is returned,
	 * surrounded by '!'s.
	 *
	 * @param key
	 *            the resource name
	 * @return the string
	 */
    public String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }
}