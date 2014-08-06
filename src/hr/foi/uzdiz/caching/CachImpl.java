/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.caching;

import java.util.HashMap;



/**
 *
 * @author Winner
 */
public class CachImpl implements Cache{
    
    private HashMap map_;
    
    public CachImpl(){
        map_ = new HashMap();
    }
    
    @Override
    public void release(Resource resource) {
        String id = resource.getId();
        map_.put (id, resource);
    }

    @Override
    public Resource acquire(String id) {
        Resource resource = (Resource) map_.get(id);
        if (resource == null) {
            return resource;
        }
        return resource;

    }

    @Override
    public void removeFromCache(String id) {
        map_.remove(id);
    }
    
}
