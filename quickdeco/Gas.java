/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package quickdeco;

/**
 *
 * @author dmitr_000
 */
public class Gas {
    public double O2;
    public double He;
    public double N2;
    public double MOD;
    
    public Gas(double O2,double He,double MOD){
        this.O2=O2;
        this.He=He;
        this.MOD=MOD;
        N2=1.0-O2-He;
    }
}
