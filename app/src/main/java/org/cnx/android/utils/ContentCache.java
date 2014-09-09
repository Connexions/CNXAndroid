/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.utils;

import java.util.HashMap;

/**
 * @author Ed Woodward
 * 
 * Cache for objects passed between Activities.  Content is removed from the cache
 * once it is retrieved to prevent lingering references.
 *
 */
public class ContentCache
{
    /**
     * HashMap to hold objects
     */
    private static HashMap<String,Object> objMap = new HashMap<String,Object>();
    
    /**
     * Access item for the given key.
     * Removes item from map if it is found.
     * @param key - String key for object
     * @return Object
     */
    public static Object getObject(String key)
    {
        Object o = objMap.get(key);
        objMap.remove(key);
        return o;
    }
    
    /**
     * Set item using the given key
     * @param key - String key for object
     * @param obj - te object to cache
     */
    public static void setObject(String key, Object obj)
    {
        objMap.put(key, obj);
    }
    
    /**
     * Removes object with the given key if the key exists
     * @param key String - the key of the object to remove
     */
    public static void removeObject(String key)
    {
        if(objMap.containsKey(key))
        {
            objMap.remove(key);
        }
    }
    

}
