/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.classes;

import java.io.Serializable;

/**
 *
 * @author Winner
 */
public class UrlSingle implements Cloneable{
    
    private String url;
    private int redniBroj;

    public UrlSingle(String url, int redniBroj) {
        this.url = url;
        this.redniBroj = redniBroj;
    }
    
    

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRedniBroj() {
        return redniBroj;
    }

    public void setRedniBroj(int redniBroj) {
        this.redniBroj = redniBroj;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
