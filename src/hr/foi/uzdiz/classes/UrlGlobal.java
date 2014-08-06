/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.classes;

import hr.foi.uzdiz.caching.Cache;
import java.io.Serializable;

/**
 *
 * @author Winner
 */
public class UrlGlobal implements Serializable,EvictionInterface{
    
    private String url;
    private String nazivDatoteke;
    private Integer brojKorištenja;
    private Long vrijemeDodavanja;
    private long vrijemeIzbacivanja;
    private long zadnjeKorištenje;
    private double kbSize;

    public UrlGlobal(String url, String nazivDatoteke, double kbSize) {
        this.url = url;
        this.nazivDatoteke = nazivDatoteke;
        vrijemeDodavanja = System.currentTimeMillis();
        brojKorištenja = 0;
        this.kbSize = kbSize;
    }
    
    

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNazivDatoteke() {
        return nazivDatoteke;
    }

    public void setNazivDatoteke(String nazivDatoteke) {
        this.nazivDatoteke = nazivDatoteke;
    }

    public Integer getBrojKorištenja() {
        return brojKorištenja;
    }

    public void setBrojKorištenja(int brojKorištenja) {
        this.brojKorištenja = brojKorištenja;
    }

    public Long getVrijemeDodavanja() {
        return vrijemeDodavanja;
    }

    public void setVrijemeDodavanja(long vrijemeDodavanja) {
        this.vrijemeDodavanja = vrijemeDodavanja;
    }

    public long getVrijemeIzbacivanja() {
        return vrijemeIzbacivanja;
    }

    public void setVrijemeIzbacivanja(int vrijemeIzbacivanja) {
        this.vrijemeIzbacivanja = vrijemeIzbacivanja;
    }

    public double getKbSize() {
        return kbSize;
    }

    public void setKbSize(double kbSize) {
        this.kbSize = kbSize;
    }

    public long getZadnjeKorištenje() {
        return zadnjeKorištenje;
    }

    public void setZadnjeKorištenje(long zadnjeKorištenje) {
        this.zadnjeKorištenje = zadnjeKorištenje;
    }
    
    

    @Override
    public boolean isEvictable() {
        return true;
    }



    @Override
    public void beforeEviction(Cache cache) {
        cache.removeFromCache(url);
        vrijemeIzbacivanja = System.currentTimeMillis();
    }
    
    
    
}
