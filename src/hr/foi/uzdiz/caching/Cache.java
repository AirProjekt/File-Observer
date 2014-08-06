/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.caching;



/**
 *
 * @author Winner
 */
public interface Cache {
    
    public void release (Resource resource);
    public Resource acquire (String id);
    public void removeFromCache(String id);
}
