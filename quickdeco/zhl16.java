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
    public double descRate=20.0;
    public double ascRate=10.0;
    public double currentDepth=0.0;
    public Double currentTime=0.0;
    public double currentN2=0.0;
    public double currentHe=0.0;
    private double gfLow=1.0;
    private double gfHigh=1.0;
    private double gf=gfLow;
    private double gfSlope=1.0;
    private double seaLevel=10.1325;
    public static final int COMPARTMENTS=17;

    
    public double dt=0.1;
    public ArrayList<planRecord> divePlan;
    public ArrayList<DiveRecord> dive;
    private ArrayList<Gas> decoGasList;
    private int iterationsLimit;

    

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
    
    
/*    zh_l16 : array [1..16, 1..8] of double = - from tausim

      (* t1/2 N2   ZH-L16A theoretisch    ZH-L16B     ZH-L16   t1/2 He                     *)
      (*           -------------------    Tabelle    Computer                              *)
      (*  [min]       b           a          a           a      [min]       b          a   *)

    (
      (   4.0,     0.5050,      1.2599,    1.2599,    1.2599,    1.51,    0.4245,    1.7424),
      (   8.0,     0.6514,      1.0000,    1.0000,    1.0000,    3.02,    0.5747,    1.3830),
      (  12.5,     0.7222,      0.8618,    0.8618,    0.8618,    4.72,    0.6527,    1.1919),
      (  18.5,     0.7825,      0.7562,    0.7562,    0.7562,    6.99,    0.7223,    1.0458),
      (  27.0,     0.8126,      0.6667,    0.6667,    0.6200,   10.21,    0.7582,    0.9220),
      (  38.3,     0.8434,      0.5933,    0.5600,    0.5043,   14.48,    0.7957,    0.8205),
      (  54.3,     0.8693,      0.5282,    0.4947,    0.4410,   20.53,    0.8279,    0.7305),
      (  77.0,     0.8910,      0.4701,    0.4500,    0.4000,   29.11,    0.8553,    0.6502),
      ( 109.0,     0.9092,      0.4187,    0.4187,    0.3750,   41.20,    0.8757,    0.5950),
      ( 146.0,     0.9222,      0.3798,    0.3798,    0.3500,   55.19,    0.8903,    0.5545),
      ( 187.0,     0.9319,      0.3497,    0.3497,    0.3295,   70.69,    0.8997,    0.5333),
      ( 239.0,     0.9403,      0.3223,    0.3223,    0.3065,   90.34,    0.9073,    0.5189),
      ( 305.0,     0.9477,      0.2971,    0.2850,    0.2835,  115.29,    0.9122,    0.5181),
      ( 390.0,     0.9544,      0.2737,    0.2737,    0.2610,  147.42,    0.9171,    0.5176),
      ( 498.0,     0.9602,      0.2523,    0.2523,    0.2480,  188.24,    0.9217,    0.5172),
      ( 635.0,     0.9653,      0.2327,    0.2327,    0.2327,  240.03,    0.9267,    0.5119)  );*/
    
    
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
            this.updatePressure(currentDepth+this.getSeaLevel(),currentN2,currentHe,dt);
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
                    this.updatePressure(currentDepth+this.getSeaLevel(),currentN2,currentHe,dt);
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
            this.updatePressure(currentDepth+this.getSeaLevel(),currentN2,currentHe,dt);
        }
        
        truncateCurrentTime();

        DiveRecord recordLogSeg =new DiveRecord(currentDepth,new Gas(1.0-currentN2-currentHe,currentHe,10000.0));
        recordLogSeg.timeStart=currentTime;
        if(dive.isEmpty()){
            //для первого сегмента учтем возможность мгновенного спуска
            updatePressure(record.depth+getSeaLevel(),currentN2,currentHe,record.time-currentTime);
        }else{
            updatePressure(record.depth+getSeaLevel(),currentN2,currentHe,record.time);
        }
        setGfSlopeAtDepth(record.depth);
        setGfAtDepth(currentDepth);
        recordLogSeg.timeEnd=currentTime;
        printCompartments();
        dive.add(recordLogSeg);
    }

    public boolean applyDeco(double ceiling) {
        boolean needDeco=false;
        while(getStop()>ceiling&&iterationsLimit>0){
            needDeco=true;
            iterationsLimit--;
            if(performAscend(getStop())>ceiling){
                performLinear(getStop());
            }
        }
        return needDeco;
    }

    private boolean updateCurrentGas() {
        Gas oldGas=new Gas(1.0-currentHe-currentN2,currentHe,10000.0);
        boolean chagned=false;
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
        //если газ изменился - остановимся на минуту для смены
        if(oldGas.N2!=currentN2||oldGas.He!=currentHe){
            chagned=true;
            //System.out.println("Gas changed at "+currentDepth );
            //updatePressure(currentDepth/zhl16.ATA+getSeaLevel(),currentN2,currentHe,1.0);
        }
        return chagned;
    }

    private double performAscend(double stop) {
        System.out.println("asc from "+currentDepth+" to "+stop);
        setGfAtDepth(currentDepth);
        while(currentDepth>stop){
             currentDepth-=ascRate*dt;
             if(currentDepth<=stop){
                 currentDepth=stop;
                 truncateCurrentTime();
             }else{
                 updatePressure(currentDepth+getSeaLevel(),currentN2,currentHe,dt);
             }
             if(updateCurrentGas()){
                 //TODO - адекватно определить глубину и выровнять время
                DiveRecord recordLog=new DiveRecord(currentDepth,new Gas(1.0-currentN2-currentHe,currentHe,10000.0));
                recordLog.timeStart=currentTime;
                updatePressure(currentDepth+getSeaLevel(),currentN2,currentHe,1.0);            
                recordLog.timeEnd=currentTime; 
                dive.add(recordLog);
             }
             
         }
        printCompartments();
        return getStop();
    }

    private double performLinear(double stop) {
        if(stop<getStop()){
            return getStop();
        }
        System.out.println("linear deco at "+stop);
        currentDepth=stop;
        updateCurrentGas();
       // truncateCurrentTime();
        DiveRecord recordLog=new DiveRecord(currentDepth,new Gas(1.0-currentN2-currentHe,currentHe,10000.0));
        recordLog.timeStart=currentTime;
        while(stop==getStop()){
            updatePressure(currentDepth+getSeaLevel(),currentN2,currentHe,1.0);            
            recordLog.timeEnd=currentTime;
        }
        dive.add(recordLog);
        printCompartments();
        return getStop();
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
            //water paur - I'm wondering why everybody uses both N2 and He for 
            //this calculation, but I'll do the same - just to generate same
            //profile like GUE DecoPlanner does
            Pn[i]=calcCompartmentPressure(GasType.Nitrogen,Pn[i],(currentPa-WaterPour)*PnPercent,dt,i);
            Ph[i]=calcCompartmentPressure(GasType.Helium,Ph[i],(currentPa-WaterPour)*PhePercent,dt,i);
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

    public void cleanAll() {
        cleanDivePlan();
        cleanDecoGasList();
        dive.clear();
        currentDepth=0.0;
        currentTime=0.0;
        currentHe=0.0;
        currentN2=0.0;
    }

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

    public double getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(double seaLevel) {
        this.seaLevel = seaLevel;
    }

    public double getDecoStep() {
        return decoStep;
    }

    public void setDecoStep(double decoStep) {
        this.decoStep = decoStep;
    }

    public double getWaterPour() {
        return WaterPour;
    }

    public void setWaterPour(double WaterPour) {
        this.WaterPour = WaterPour;
    }

    
    public static String trunc(double in){
        return String.valueOf(((double)(new Double(in*100.0).intValue()))/100.0);
    }

    
}
