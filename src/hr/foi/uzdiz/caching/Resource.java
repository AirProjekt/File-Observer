/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.caching;

import org.jsoup.nodes.Document;

/**
 *
 * @author Winner
 */
public class Resource {
    private Document doc;
    private String id;

    public Resource(Document doc, String id) {
        this.doc = doc;
        this.id = id;
    }
    
    

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}
