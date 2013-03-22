/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package quickdeco;

import java.util.ArrayList;


/**
 *
 * @author dmitr_000
 */
public class zhl16 {

    private double WaterPour=0.567;//давлние водяного пара
    private double decoStep=3.0;
    private double lastStop=3.0;
    public double descRate=18.0;
    public double ascRate=9.0;
    public double currentDepth=0.0;
    public Double currentTime=0.0;
    public double currentN2=0.0;
    public double currentHe=0.0;
    
    public double dt=0.1;
    public ArrayList<planRecord> divePlan;
    public ArrayList<DiveRecord> dive;
    private ArrayList<Gas> decoGasList;
    private int iterationsLimit;
    
    public void cleanDive(){
        dive.clear();
    }
    
    public void cleanDivePlan(){
        divePlan.clear();
        iterationsLimit=10000;
    }
    
    public void addPlanRecord(planRecord record){
        divePlan.add(record);
    }
    
    
    public void cleanDecoGasList(){
        decoGasList.clear();
    }
    
    public void addDecoGas(Gas gas){
        decoGasList.add(gas);
    }
    
    public double getLastStop() {
        return lastStop;
    }

    public void setLastStop(double lastStop) {
        this.lastStop = lastStop;
    }
    private double gfLow=1.0;

    public double getGfLow() {
        return gfLow;
    }

    public void setGfLow(double gfLow) {
        this.gfLow = gfLow;
    }

    public double getGfHigh() {
        return gfHigh;
    }

    public void setGfHigh(double gfHigh) {
        this.gfHigh = gfHigh;
    }
    private double gfHigh=1.0;
    private double gf=gfLow;
    private double gfSlope=1.0;
    private double seaLevel=10.1325;
    public static final double ATA=1.01325;
    public static final int COMPARTMENTS=17;

    public double getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(double seaLevel) {
        this.seaLevel = seaLevel;
    }

    
    public zhl16(){
        gf=gfLow;
        divePlan=new ArrayList();
        decoGasList=new ArrayList();
        dive=new ArrayList();
        iterationsLimit=10000;
    }
    
    public void init(Double Pstart){
        if(Pstart!=null){
            seaLevel=Pstart;
        }
        for(int i=0;i<COMPARTMENTS;i++){
            Pn[i]=0.78084*(seaLevel-WaterPour); //начальное давление + коррекция по водяному пару
            Ph[i]=0.0;
        }
    }
    
    public double getDecoStep() {
        return decoStep;
    }

    public void setDecoStep(double decoStep) {
        this.decoStep = decoStep;
    }

    
    public static String trunc(double in){
        return String.valueOf(((double)(new Double(in*10.0).intValue()))/10.0);
    }
    
    private void printCompartments(){
        System.out.println("\ndepth="+currentDepth+", time="+trunc(currentTime)+", He="+trunc(currentHe)+", N2="+trunc(currentN2)+", GF="+trunc(getGF()));
        for(int i=0;i<zhl16.COMPARTMENTS;i++){
            System.out.print(i+") ceil: "+trunc(getCompartmentStop(i)));
            System.out.print(" pHe: "+trunc(Ph[i]));
            System.out.print(" pN2: "+trunc(Pn[i]));
            System.out.print(" pTotal: "+trunc(Ph[i]+Pn[i]));
            System.out.print(" %: "+trunc((Pn[i]+Ph[i])/(currentDepth+getSeaLevel())*100.0));
            double M=calcM(currentDepth, Pn[i], Ph[i], i);
            System.out.print(" M: "+trunc(M));
            System.out.println(" %M: "+trunc((Pn[i]+Ph[i])/M*100.0));
        }
    }

    
    
     // Data ZH-L16B
     // aN2,     bN2,    aHe,    bHe
    public static final double CompartmentsAB[][]={
        {12.599, 0.5050, 17.424, 0.4245},
        {11.696, 0.5578, 16.189, 0.4770},
        {10.000, 0.6514, 13.838, 0.5747},
        {08.618, 0.7222, 11.919, 0.6527},
        {07.562, 0.7825, 10.458, 0.7223},
        {06.667, 0.8126, 09.220, 0.7582},
        {05.600, 0.8434, 08.205, 0.7957},
        {04.947, 0.8693, 07.305, 0.8279},
        {04.500, 0.8910, 06.502, 0.8553},
        {04.187, 0.9092, 05.950, 0.8757},
        {03.798, 0.9222, 05.545, 0.8903},
        {03.497, 0.9319, 05.333, 0.8997},
        {03.223, 0.9403, 05.189, 0.9073},
        {02.850, 0.9477, 05.181, 0.9122},
        {02.737, 0.9544, 05.176, 0.9171},
        {02.523, 0.9602, 05.172, 0.9217},
        {02.327, 0.9653, 05.119, 0.9267}
};

    
// Compartiment half-life, in minute
//  N2     He 
    public static final double CompartmentsHT[][] = {
    {4.0,   1.51},
    {5.0,   1.88},
    {8.0,   3.02},
    {12.5,  4.72},
    {18.5,  6.99},
    {27.0,  10.21},
    {38.3,  14.48},
    {54.3,  20.53},
    {77.0,  29.11},
    {109.0, 41.20},
    {146.0, 55.19},
    {187.0, 70.69},
    {239.0, 90.34},
    {305.0, 115.29},
    {390.0, 147.42},
    {498.0, 188.24},
    {635.0, 240.03}
};
    
    
    public double Pn[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    public double Ph[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    
    private void truncateCurrentTime(){
        currentTime=((double)(new Double(currentTime*10.0).intValue()))/10.0;
        while(((new Double(currentTime*10.0).intValue())%10!=0)){
            this.updatePressure(currentDepth/zhl16.ATA+this.getSeaLevel(),currentN2,currentHe,dt);
        }
        currentTime=((double)(new Double(currentTime*10.0).intValue()))/10.0;
    }
    
    
    public void moveTo(planRecord record,boolean immediate) {
        
        currentHe=record.gas.He;
        currentN2=record.gas.N2;

        if(!immediate){
            while(currentDepth<record.depth){
                    currentDepth+=descRate*dt;
                    if(currentDepth>=record.depth){
                        currentDepth=record.depth;
                    }
                    this.updatePressure(currentDepth/zhl16.ATA+this.getSeaLevel(),currentN2,currentHe,dt);
            }
        }else{
            currentDepth=record.depth;
        }
        
        
        applyDeco(record.depth);
        
        while(record.depth<currentDepth){

            currentDepth-=ascRate*dt;
            if(currentDepth<=record.depth){
                currentDepth=record.depth;
            }
            this.updatePressure(currentDepth/zhl16.ATA+this.getSeaLevel(),currentN2,currentHe,dt);
        }
        
        truncateCurrentTime();
        

        DiveRecord recordLogSeg =new DiveRecord(currentDepth,new Gas(1.0-currentN2-currentHe,currentHe,10000.0));
        recordLogSeg.timeStart=currentTime;
        updatePressure(record.depth/zhl16.ATA+getSeaLevel(),currentN2,currentHe,record.time-currentTime);
        setGfSlopeAtDepth(record.depth);
        recordLogSeg.timeEnd=currentTime;
        printCompartments();
        dive.add(recordLogSeg);
        
    }

    public boolean applyDeco(double ceiling) {
        double currStop=getStop();
        boolean needDeco=false;
        double oldStop=currentDepth;
        DiveRecord recordLog=new DiveRecord(currentDepth,new Gas(1.0-currentN2-currentHe,currentHe,10000.0));
        
        while(currStop>ceiling&&iterationsLimit>0){
            
           needDeco=true;
           iterationsLimit--;
             
           while(currentDepth>currStop){
                currentDepth-=ascRate*dt;
                if(currentDepth<=currStop){
                    currentDepth=currStop;
                    recordLog.depth=currentDepth;
                    truncateCurrentTime();
                    recordLog.timeStart=currentTime;
                }
                updatePressure(currentDepth/zhl16.ATA+getSeaLevel(),currentN2,currentHe,dt);
            }
            
            setGfAtDepth(currentDepth);
            updateCurrentGas();
            recordLog.gas.N2=currentN2;
            recordLog.gas.He=currentHe;
            recordLog.gas.O2=1.0-currentHe-currentN2;
            
            currStop=getStop();
            
            if(oldStop!=currStop){
                truncateCurrentTime();
                printCompartments();
                recordLog.timeEnd=currentTime;
                dive.add(recordLog);
                recordLog=new DiveRecord(currentDepth,new Gas(1.0-currentN2-currentHe,currentHe,10000.0));
                recordLog.timeStart=currentTime;

                oldStop=currStop;
            }
            updatePressure(currentDepth/zhl16.ATA+getSeaLevel(),currentN2,currentHe,1.0);            
            
        }
        
        if(needDeco){
            System.out.println("iterations left="+iterationsLimit+" total time="+currentTime);
        }
        
        return needDeco;
    }

    public void cleanAll() {
        cleanDivePlan();
        cleanDecoGasList();
        dive.clear();
        currentDepth=0.0;
        currentTime=0.0;
        currentHe=0.0;
        currentN2=0.0;
    }

    private void updateCurrentGas() {
        double selectedMOD=10000.0;
        for(Gas gas:decoGasList){
            if(gas.MOD>=currentDepth){
                if(selectedMOD>=gas.MOD){
                    selectedMOD=gas.MOD;
                    currentHe=gas.He;
                    currentN2=gas.N2;
                }
            }
        }
    }

    
    
    public static enum GasType {Nitrogen,Helium};

    public double calcCompartmentPressure(GasType gas, double Pbegin,double Pgas, double te,int compartment){
        double result;
        result = Pbegin + (Pgas-Pbegin)*(1.0-Math.pow(2.0,-te/CompartmentsHT[compartment][(gas==GasType.Helium)?1:0]));
        return result;
    } 

    private double getGF(){
        if (gf >= gfLow){ 
            return gf;
        }else{
            return gfLow;
        }
    }
    
    public void setGfAtDepth(double depth){
        if ((gfSlope < 1.0) && (depth >= 0.0)){
            gf=(depth*gfSlope) + gfHigh;       
        }
    }
    
    public void setGfSlopeAtDepth(double depth){
        if (depth > 0.0) {
            gfSlope = (gfHigh-gfLow)/(0.0-depth);      
        }
    }  
    
   
    public double getMaxAmb(double Pn,double Phe,double gf,int compartment)
    {
        double A = ((CompartmentsAB[compartment][0] * Pn) + (CompartmentsAB[compartment][2] * Phe))/(Pn + Phe);
        double B = ((CompartmentsAB[compartment][1] * Pn) + (CompartmentsAB[compartment][3] * Phe))/(Pn + Phe);
        return ((Pn+Phe) - A*gf)/(gf/B-gf+1.0)-seaLevel;
    }

    
    public double calcM(double depth,double Pn,double Phe, int compartment){
        double A = ((CompartmentsAB[compartment][0] * Pn) + (CompartmentsAB[compartment][2] * Phe))/(Pn + Phe);
        double B = ((CompartmentsAB[compartment][1] * Pn) + (CompartmentsAB[compartment][3] * Phe))/(Pn + Phe);
        return (1.0/B)*(depth)+(A+seaLevel/B);
    }

    
    public void updatePressure(double currentPa,double PnPercent,double PhePercent,double dt){
        for(int i=0;i<COMPARTMENTS;i++){
            Pn[i]=calcCompartmentPressure(GasType.Nitrogen,Pn[i],currentPa*PnPercent,dt,i);
            Ph[i]=calcCompartmentPressure(GasType.Helium,Ph[i],currentPa*PhePercent,dt,i);
        }
        currentTime+=dt;
    }

    
    
    public double getCompartmentStop(int i){
        double current;
        current=getMaxAmb(Pn[i], Ph[i], getGF(), i);
        return current;
    }
    
    public double getStop(){
        double stop;
        double max=0.0;
        double current;
        for(int i=0;i<COMPARTMENTS;i++){
            current=getMaxAmb(Pn[i], Ph[i], getGF(), i);
            if(current>0.0){
                stop=(Math.ceil(current/decoStep))*decoStep;
                max=Math.max(max, stop);
                if(max<lastStop){
                    max=lastStop;
                }
            }
        }
        return max;
    }
}
