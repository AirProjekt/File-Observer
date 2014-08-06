/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.uzdiz.Strategy;

import hr.foi.uzdiz.classes.UrlGlobal;
import java.util.List;

/**
 *
 * @author Winner
 */
public class ConcreteStrategyKB implements Strategy{

    @Override
    public double vratiVelicinuSpremnika(List<UrlGlobal> lista) {
        double ukupno = 0;
        for (UrlGlobal urlGlobal : lista) {
            ukupno += urlGlobal.getKbSize();
        }
        return ukupno;
    }

    
    
}
