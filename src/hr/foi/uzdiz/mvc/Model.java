/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.mvc;

import hr.foi.uzdiz.Strategy.ConcreteStrategyBE;
import hr.foi.uzdiz.Strategy.ConcreteStrategyKB;
import hr.foi.uzdiz.Strategy.Context;
import hr.foi.uzdiz.caching.CachImpl;
import hr.foi.uzdiz.caching.Cache;
import hr.foi.uzdiz.caching.Resource;
import hr.foi.uzdiz.classes.UrlGlobal;
import hr.foi.uzdiz.classes.UrlSingle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Winner
 */
public class Model extends java.util.Observable{
    
    private String currentUrl;
    private String currentName;
    private String putanjaSpremista;
    private List<UrlSingle> lista;
    private List<UrlGlobal> listaWebStranica;
    private long vrijemePocetak;
    private long vrijemeKraj;
    private String[] argumenti;
    private String vrstaKapacitet;
    private String strategijaIzbacivanja;
    private boolean clean;
    private double kapacitet;
    private Cache cache;

    public Model(String[] args) {
        cache = new CachImpl();
        this.argumenti = args;
        postaviArgumente();
        vrijemePocetak = System.currentTimeMillis();
    }
    
    private void postaviArgumente(){
        String args = Arrays.toString(argumenti);
        args = args.substring(1, args.length()-1).replaceAll(",", "");
        String regex = "([\\S]+) (\\S*) (\\d*) (\\d*)( KB)?( NS| NK)?( clean)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(args);
        if (m.matches()) {
            this.putanjaSpremista = m.group(2);
            this.kapacitet = Double.parseDouble(m.group(4));
            if (m.group(5) == null) {
                this.vrstaKapacitet = "BE";
            }
            else{
                this.vrstaKapacitet = m.group(5).trim();
            }
            if (m.group(6) == null) {
                this.strategijaIzbacivanja = "NS";
            }
            else{
                this.strategijaIzbacivanja = m.group(6).trim();
            }
            if (m.group(7) == null) {
                this.clean = false;
            }
            else{
                this.clean = true;
            }
            if (!Files.exists(Paths.get(putanjaSpremista))) {
                try {
                    Files.createDirectory(Paths.get(putanjaSpremista));
                } catch (IOException ex) {
                    setChanged();
                    notifyObservers("Nije bilo moguće kreirate direktorij");
                }
            }
            ucitajStranicu(m.group(1));
        }
        else{
            setChanged();
            notifyObservers("Niste dobro unijeli argumente sa komandne linije! Pokušajte ponovno");
            System.exit(1);
        }
    }
    

    public void ucitajStranicu(String urlParam){
        if (clean) {
            ocistiDirektorij(new File(putanjaSpremista));
            clean = false;
        }
        this.currentUrl = urlParam;
        try {
            if (provjeriSpremiste()) {
                //Caching
                Resource res;
                Document doc;
                if (cache == null) {
                    cache = new CachImpl();
                }
                if (cache.acquire(currentUrl) == null) {
                    File input = new File(putanjaSpremista + "\\"+currentName);
                    doc = Jsoup.parse(input, "UTF-8", currentUrl);
                    res = new Resource(doc, currentUrl);
                    cache.release(res);
                }else{
                    res = cache.acquire(currentUrl);
                    doc = res.getDoc();
                }
                Document docVanjski = Jsoup.connect(currentUrl).get();
                if(!doc.outerHtml().equals(docVanjski.outerHtml())){
                    FileOutputStream fos = new FileOutputStream(putanjaSpremista + "\\"+currentName);
                    fos.write(docVanjski.outerHtml().getBytes());
                    fos.close();
                    cache.removeFromCache(currentUrl);
                    File input = new File(putanjaSpremista + "\\"+currentName);
                    doc = Jsoup.parse(input, "UTF-8", currentUrl);
                    res = new Resource(doc, currentUrl);
                    cache.release(res);
                }
                int i = 1;
                Elements links = doc.select("a[href]");
                this.lista = new ArrayList<>();
                for (Element link : links) {
                    UrlSingle url = new UrlSingle(link.attr("abs:href"), i);
                    lista.add(url);
                    i++;
                }
            } else {
                Document doc = Jsoup.connect(currentUrl).get();
                double kbSize = doc.outerHtml().getBytes().length / 1024.0;
                if (this.kapacitet < vratiVelicinuSpremnika(kbSize)) {                   
                    UrlGlobal urlEvict = null;
                    if (this.strategijaIzbacivanja.equals("NK")) {
                        Comparator c = new Comparator<UrlGlobal>() {

                            @Override
                            public int compare(UrlGlobal o1, UrlGlobal o2) {
                                return o1.getBrojKorištenja().compareTo(o2.getBrojKorištenja());
                            }
                           
                        };
                        urlEvict = Collections.max(listaWebStranica, c);
                        
                    }
                    else{
                        Comparator c = new Comparator<UrlGlobal>() {

                            @Override
                            public int compare(UrlGlobal o1, UrlGlobal o2) {
                                return o1.getVrijemeDodavanja().compareTo(o2.getVrijemeDodavanja());
                            }
                           
                        };
                        urlEvict = Collections.min(listaWebStranica, c);
                    }
                    if (urlEvict.isEvictable()) {
                            urlEvict.beforeEviction(cache);
                            File file = new File(this.putanjaSpremista+"\\"+urlEvict.getNazivDatoteke());
                            file.delete();
                            dodajUDnevnikRadaIzbaci(urlEvict);
                            listaWebStranica.remove(urlEvict);
                    }
                    setChanged();
                    notifyObservers("Spremnik je popunjen! Izbačena je datoteka pod imenom "+urlEvict.getNazivDatoteke());
                }
                int i = 1;
                this.lista = new ArrayList<>();
                String fileName = currentUrl.replace("http://", "");
                //micanje nedozvoljenih znakova za naziv datoteke
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                FileOutputStream fo = new FileOutputStream(new File(putanjaSpremista + "\\" + fileName));
                fo.write(doc.outerHtml().getBytes());
                fo.close();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    UrlSingle url = new UrlSingle(link.attr("abs:href"), i);
                    lista.add(url);
                    i++;
                }
                currentName = fileName;
                UrlGlobal urlGlobal = new UrlGlobal(currentUrl, fileName,kbSize);
                urlGlobal.setVrijemeDodavanja(System.currentTimeMillis());
                dodajUDnevnikRadaUbaci(urlGlobal);
                listaWebStranica.add(urlGlobal);
            }
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public boolean provjeriSpremiste(){
        File file = new File(putanjaSpremista+"\\Dnevnik.ser");
        try {
            FileInputStream fileIn = null;
            ObjectInputStream in = null;
            if (listaWebStranica == null) {
                if (file.exists()) {
                    fileIn = new FileInputStream(file);
                    in = new ObjectInputStream(fileIn);
                    listaWebStranica = (ArrayList<UrlGlobal>) in.readObject();
                    fileIn.close();
                    in.close();
                    for (UrlGlobal url : listaWebStranica) {
                        if (url.getUrl().equals(currentUrl)) {
                            url.setBrojKorištenja(url.getBrojKorištenja() + 1);
                            url.setZadnjeKorištenje(System.currentTimeMillis());
                            currentName = url.getNazivDatoteke();
                            return true;
                        }
                    }
                }else{
                    listaWebStranica = new ArrayList<UrlGlobal>();
                }
            }
            else{
                for (UrlGlobal url : listaWebStranica) {
                    if (url.getUrl().equals(currentUrl)) {
                        url.setBrojKorištenja(url.getBrojKorištenja() + 1);
                        url.setZadnjeKorištenje(System.currentTimeMillis());
                        currentName = url.getNazivDatoteke();
                        return true;
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private double vratiVelicinuSpremnika(double kbSize){
        Context c;
        double rezultat;
        if (this.vrstaKapacitet.equals("KB")) {
            c = new Context(new ConcreteStrategyKB());
            rezultat = c.executeStrategy(listaWebStranica);
            return rezultat+kbSize;
        }
        else{
            c = new Context(new ConcreteStrategyBE());
            rezultat = c.executeStrategy(listaWebStranica);
            return rezultat+1;
        }
    }
    
    public List<UrlSingle> getLista(){
        return lista;
    }
    
    public String getCurrentUrl(){
        return currentUrl;
    }
    
    public void dodajUDnevnikRadaUbaci(UrlGlobal urlGlobal){
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------\r\n");
        sb.append("Dodana je datoteka pod imenom "+urlGlobal.getNazivDatoteke()+" koja je povučena sa url-a "+urlGlobal.getUrl()+"\r\n");
        long diff = urlGlobal.getVrijemeDodavanja();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        String vrijeme = diffHours+":"+diffMinutes+":"+diffSeconds;
        sb.append("Datoteka je dodana u "+vrijeme+"\r\n");
        sb.append("---------------------------------------------\r\n");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(putanjaSpremista + "\\" + "dnevnik.txt"), true /* append = true */));
            pw.append(sb.toString());
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dodajUDnevnikRadaIzbaci(UrlGlobal urlGlobal){
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------\r\n");
        sb.append("Izbačena je datoteka pod imenom "+urlGlobal.getNazivDatoteke()+" koja je povučena sa url-a "+urlGlobal.getUrl()+"\r\n");
        
        long diff = urlGlobal.getVrijemeIzbacivanja()-urlGlobal.getVrijemeDodavanja();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        String vrijeme = diffHours+":"+diffMinutes+":"+diffSeconds;
        sb.append("Datoteka je bila korištena "+urlGlobal.getBrojKorištenja()+" puta\r\n");
        sb.append("Datoteka je bila u spremištu "+vrijeme+" vremena\r\n");
        sb.append("---------------------------------------------\r\n");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(putanjaSpremista + "\\" + "dnevnik.txt"), true /* append = true */));
            pw.append(sb.toString());
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<UrlGlobal> vratiListuWebStranica(){
        return listaWebStranica;
    }
    
    public void SerijalizirajListu(){
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(putanjaSpremista+"\\Dnevnik.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(listaWebStranica);
            out.close();
            fileOut.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public String vratiUrlStranice(int broj){
        for (UrlSingle urlSingle : lista) {
            if (urlSingle.getRedniBroj() == broj) {
                return urlSingle.getUrl();
            }
        }
        return null;
    }
    
    public void ocistiDirektorij(File folder){
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File tmpF = list[i];
                    if (tmpF.isDirectory()) {
                        ocistiDirektorij(tmpF);
                    }
                    tmpF.delete();
                }
            }

        }
        listaWebStranica = null;
        cache = null;
        setChanged();
        notifyObservers("Sadržaj Direktorija je pobrisan!");
    }
    public String getPutanjaSpremista(){
        return putanjaSpremista;
    }
}
