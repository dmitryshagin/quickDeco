/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package quickdeco;

/**
 *
 * @author dmitr_000
 */
public class DiveRecord {
    public double depth;
    public double timeStart;
    public double timeEnd;
    public Gas gas;
    
    public DiveRecord(double depth,Gas gas){
        this.depth=depth;
        this.gas=gas;
    }
}
