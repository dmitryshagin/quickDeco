/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simp;

import java.awt.Color;

/**
 *
 * @author vsd
 */
public class DecoGasAtom extends javax.swing.JPanel {

    /**
     * Creates new form PlanAton
     */
    
    public DecoGasAtom(String gas, boolean active) {
        initComponents();
        main.dcd.addWatchingItem(jtfDepth);
        main.dcd.addWatchingItem(jtfGas);
        main.dcd.addWatchingItem(jCheckBox1);

        if(gas!=null){
            resetText(jtfGas);
            jtfGas.setText(gas);
            int depth=setBestDepth(gas);
            if(depth!=0){
                resetText(jtfDepth);
                jtfDepth.setText(String.valueOf(depth));
            }else{
                jtfDepth.setText("");
                applyText(jtfDepth,"Глубина");
            }
        }
        jCheckBox1.setSelected(active);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jbRemove = new javax.swing.JButton();
        jtfGas = new javax.swing.JTextField();
        jtfDepth = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        jbRemove.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        jbRemove.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        add(jbRemove, gridBagConstraints);

        jtfGas.setFont(new java.awt.Font("DejaVu Sans", 2, 10)); // NOI18N
        jtfGas.setForeground(java.awt.Color.gray);
        jtfGas.setText("Газ");
        jtfGas.setMinimumSize(new java.awt.Dimension(60, 23));
        jtfGas.setPreferredSize(new java.awt.Dimension(60, 23));
        jtfGas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfGasMouseClicked(evt);
            }
        });
        jtfGas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfGasFocusLost(evt);
            }
        });
        jtfGas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfGasKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfGasKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        add(jtfGas, gridBagConstraints);

        jtfDepth.setFont(new java.awt.Font("DejaVu Sans", 2, 10)); // NOI18N
        jtfDepth.setForeground(java.awt.Color.gray);
        jtfDepth.setText("Глубина");
        jtfDepth.setMinimumSize(new java.awt.Dimension(60, 23));
        jtfDepth.setPreferredSize(new java.awt.Dimension(60, 23));
        jtfDepth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfDepthMouseClicked(evt);
            }
        });
        jtfDepth.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfDepthFocusLost(evt);
            }
        });
        jtfDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfDepthKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfDepthKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        add(jtfDepth, gridBagConstraints);

        jCheckBox1.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        add(jCheckBox1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    
    
    public double getO2(){
        String gas=jtfGas.getText().trim();
        double o2;
        if(gas.length()>1){
            if(gas.contains("/")){
                try{
                    o2=Double.parseDouble(gas.trim().substring(0, gas.trim().indexOf("/")));
                    return o2/100.0;
                }catch(Exception ex){
                }
            }else{
                try{
                    o2=Double.parseDouble(gas.trim());
                    return o2/100.0;
                }catch(Exception ex){
                }
            }
        }
        return 0.0;
    }
    
    public double getHe(){
        String gas=jtfGas.getText().trim();
        double He;
        if(gas.length()>1){
            if(gas.contains("/")){
                try{
                    He=Double.parseDouble(gas.trim().substring(gas.trim().indexOf("/")+1));
                    return He/100.0;
                }catch(Exception ex){
                }
            }else{
                return 0.0;
            }
        }
        return 0.0;
    }
    
    public double getDepth(){
        try{
            int depth=Integer.parseInt(jtfDepth.getText().trim());
            if(depth>0){
                return depth;
            }else{
                return 0.0;
            }
        }catch(Exception ex){
            return 0.0;
        }
    }
    
    public boolean isTurnedOn(){
        return jCheckBox1.isSelected();
    }
    
    
    public static int setBestDepth(String gas){
        int o2=0;
        if(gas.trim().length()>1){
            if(gas.contains("/")){
                try{
                    o2=Integer.parseInt(gas.trim().substring(0, gas.trim().indexOf("/")));
                }catch(Exception ex){
                }
            }else{
                try{
                    o2=Integer.parseInt(gas.trim());
                }catch(Exception ex){
                }
            }
        }
        if(o2!=0){
            int ppO2=1610;
            if(o2<50){
                ppO2=1410;
            }
            /*if(o2<21){
                ppO2=1210;
            }*/
            int target=(ppO2/o2)-10;
            return (target/3)*(3);
        }
        return 0;
    }
    
    private void jtfDepthKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfDepthKeyReleased
        applyText(jtfDepth,"Глубина");
    }//GEN-LAST:event_jtfDepthKeyReleased

    private void jtfDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfDepthKeyTyped
        resetText(jtfDepth);
    }//GEN-LAST:event_jtfDepthKeyTyped

    private void jtfDepthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfDepthMouseClicked
        resetText(jtfDepth);
    }//GEN-LAST:event_jtfDepthMouseClicked

    private void jtfDepthFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfDepthFocusLost
        applyText(jtfDepth,"Глубина");
    }//GEN-LAST:event_jtfDepthFocusLost

    private void jtfGasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfGasFocusLost
        applyText(jtfGas,"Газ");
        applyText(jtfDepth,"Глубина");
        int depth=setBestDepth(jtfGas.getText());
        if(depth!=0){
            resetText(jtfDepth);
            jtfDepth.setText(String.valueOf(depth));
        }else{
            jtfDepth.setText("");
            applyText(jtfDepth,"Глубина");
        }
    }//GEN-LAST:event_jtfGasFocusLost

    private void jtfGasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfGasMouseClicked
        resetText(jtfGas);
    }//GEN-LAST:event_jtfGasMouseClicked

    private void jtfGasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfGasKeyTyped
        resetText(jtfGas);
    }//GEN-LAST:event_jtfGasKeyTyped

    private void jtfGasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfGasKeyReleased
        applyText(jtfGas,"Газ");
        int depth=setBestDepth(jtfGas.getText());
        if(depth!=0){
            resetText(jtfDepth);
            jtfDepth.setText(String.valueOf(depth));
        }else{
            jtfDepth.setText("");
            applyText(jtfDepth,"Глубина");
        }
    }//GEN-LAST:event_jtfGasKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    public javax.swing.JButton jbRemove;
    private javax.swing.JTextField jtfDepth;
    private javax.swing.JTextField jtfGas;
    // End of variables declaration//GEN-END:variables

    private void resetText(javax.swing.JTextField jtf) {
        if (jtf.getFont().isItalic()) {
            jtf.setFont(jtf.getFont().deriveFont(0));
            jtf.setForeground(Color.black);
            jtf.setText("");
        }
    }
    
    private void applyText(javax.swing.JTextField jtf,String defaultText) {
        if (jtf.getText().length() == 0) {
            jtf.setFont(jtfDepth.getFont().deriveFont(2));
            jtf.setForeground(Color.GRAY);
            jtf.setText(defaultText);
            jtf.setCaretPosition(0);
        }
    }

    void clearDCD() {
        main.dcd.removeWatchingItem(jtfDepth);
        main.dcd.removeWatchingItem(jtfGas);
        main.dcd.removeWatchingItem(jCheckBox1);
    }
}