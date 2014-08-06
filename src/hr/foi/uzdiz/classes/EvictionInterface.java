/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.classes;

import hr.foi.uzdiz.caching.Cache;

/**
 *
 * @author Winner
 */
public interface EvictionInterface {
    
    public boolean isEvictable();
    public void beforeEviction(Cache cache);

}
