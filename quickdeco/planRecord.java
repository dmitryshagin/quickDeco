/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package quickdeco;

/**
 *
 * @author dmitr_000
 */
public class planRecord {
    public double depth;
    public double time;
    public Gas gas;
    
    public planRecord(double depth,double time,Gas gas){
        this.depth=depth;
        this.time=time;
        this.gas=gas;
    }

    public boolean isOk() {
        if(depth<=0.0){
            return false;
        }
        if(time<=0.0){
            return false;
        }
        if(gas.He>1.0||gas.N2>1.0||gas.O2>1.0||gas.He<0||gas.N2<0||gas.O2<0.01){
            return false;
        }
        return true;
    }
}
