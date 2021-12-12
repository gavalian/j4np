/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Background2D;
import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import j4np.graphics.PopupProvider;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;
import twig.config.TStyle;
import twig.editors.DataCanvasEditorDialog;

/**
 *
 * @author gavalian
 */
public class TGDataCanvas extends Canvas2D implements ActionListener {
    
    private int activeRegion = 0;
    private CanvasPopupProvider popupProvider = null;//new CanvasPopupProvider();
    
    public TGDataCanvas(){
        Color color = TStyle.getInstance().getPalette().getColor(30004);
        Background2D back = Background2D.createBackground(color.getRed(),color.getGreen(),color.getBlue());
        setBackground(back);        
        popupProvider = new CanvasPopupProvider(this);
        this.setPopupProvider(popupProvider);
        divide(1,1);
    }
    
    
    
    public void divide(double[][] fractions){
        int ncolumns = fractions.length;
        int size = 0;
        for(int i = 0; i < ncolumns; i++) size += fractions[i].length;
        System.out.println("DIVIDING CANVAS (cols): " + ncolumns + " TOTAL SIZE = " + size);
        this.getGraphicsComponents().clear();
        for(int i = 0; i < size; i++)  addNode(new TGRegion());
        arrange(fractions);
    }
    

    public void divide(double left, double bottom,int cols, int rows){
        this.getGraphicsComponents().clear();
        for(int i = 0; i < cols*rows; i++){
            TGRegion pad = new TGRegion();
            this.addNode(pad);
        }
        this.arrangeWithGap(left, bottom, cols, rows);
        this.repaint();
        this.activeRegion = 0;
    }
    
    @Override
    public void divide(int cols, int rows){
        this.getGraphicsComponents().clear();
        for(int i = 0; i < cols*rows; i++){
            TGRegion pad = new TGRegion();
            this.addNode(pad);
        }
        this.arrange(cols, rows);
        this.repaint();
        this.activeRegion = 0;
    }
    
    public TGRegion region(){ 
        return (TGRegion) getGraphicsComponents().get(activeRegion);
    }
    
    public TGDataCanvas cd(int index){
        if(index < 0) { activeRegion = 0; return this;}
        if(index >= this.getGraphicsComponents().size()){
            activeRegion = this.getGraphicsComponents().size() - 1;
        } 
        activeRegion = index;
        return this;
    }
    
    public TGRegion region(int index){ 
        return (TGRegion) getGraphicsComponents().get(index);         
    }
    
    public int count(){
        return getGraphicsComponents().size();
    }
    
    public void export(String filename, String type){
        if(type.compareTo("PDF")==0||type.compareTo("pdf")==0){
            System.out.println("[canvas] >>> exporting file : " + filename);
            PDFDocument pdfDoc = new PDFDocument();
            Page page = pdfDoc.createPage(new Rectangle(this.getSize().width, this.getSize().height));
            PDFGraphics2D g2 = page.getGraphics2D();
            this.paint(g2);
            pdfDoc.writeToFile(new File(filename));
            
        }
    }
    
    public TGDataCanvas left(int left){
        for(Node2D node : getGraphicsComponents()) node.getInsets().left(left);
        return this;
    }
    
    public TGDataCanvas right(int right){
        for(Node2D node : getGraphicsComponents()) node.getInsets().right(right);
        return this;
    }
    
    public TGDataCanvas top(int top){
        for(Node2D node : getGraphicsComponents()) node.getInsets().top(top);
        return this;
    }
    
    public TGDataCanvas bottom(int bottom){
        for(Node2D node : getGraphicsComponents()) node.getInsets().bottom(bottom);
        return this;
    }
    
    public TGDataCanvas ticksSizeX(int tsX){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisTickMarkSize(tsX);
        }
        return this;
    }
    
    public TGDataCanvas ticksSizeY(int tsY){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisTickMarkSize(tsY);
        }
        return this;
    }
    public TGDataCanvas divisionsX(int divX){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisTickMarkCount(divX);
        }
        return this;
    }
    
    
    public TGDataCanvas divisionsY(int divY){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisTickMarkCount(divY);
        }
        return this;
    }
    
    public TGDataCanvas labelOffsetX(int offset){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisLabelOffset(offset);
        }
        return this;
    }
    
    public TGDataCanvas labelOffsetY(int offset){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisLabelOffset(offset);
        }
        return this;
    }
    
    public TGDataCanvas titleOffsetX(int offset){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisTitleOffset(offset);
        }
        return this;
    }
    
    public TGDataCanvas titleOffsetY(int offset){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisTitleOffset(offset);
        }
        return this;
    }
    
    public TGDataCanvas axisLineWidth(int width){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisLineWidth(width);
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisLineWidth(width);
        }
        return this;
    }
    
    public TGDataCanvas axisTicksLineWidth(int width){        
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisTicksLineWidth(width);
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisTicksLineWidth(width);
        }
        return this;
    }
    
    public TGDataCanvas setAxisFont(Font font){
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisLabelFont(font);
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisLabelFont(font);
        }
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().compareTo("save_pdf")==0){
            this.export(this.getName()+".pdf","PDF");
            //this.save(this.getName()+".pdf");
        }
        
        if(e.getActionCommand().compareTo("save_svg")==0){
            this.save(this.getName()+".svg");
        }
        if(e.getActionCommand().compareTo("Edit Canvas")==0){
            DataCanvasEditorDialog.openOptionsPanel(this);
        }
    }
    
    public static class CanvasPopupProvider extends PopupProvider {
        
        JPopupMenu          menu = null;
        TGRegion          region = null;
        ActionListener  listener = null;
        
        
        public CanvasPopupProvider(ActionListener al){
            listener = al;
            initialize();
        }
        
        private void addMenuItem(JPopupMenu m, String name){
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(listener);
            m.add(item);
        }
        
        private void addMenu(JPopupMenu m, String menu, String[] names, String[] actions){
            JMenu jm = new JMenu(menu);
            for(int i = 0; i < names.length; i++){ 
                JMenuItem jmi = new JMenuItem(names[i]);
                jmi.setActionCommand(actions[i]);
                jmi.addActionListener(listener);
                jm.add(jmi);
            }
            m.add(jm);
        }
        
        private void initialize(){
            
            menu = new JPopupMenu();
            
            this.addMenuItem(menu, "Copy Region");
            this.addMenuItem(menu, "Paste Region");
            
            menu.add(new JSeparator());
            
            this.addMenu(menu, "Export", 
                    new String[]{"Export twig","Export txt", "Export h5"}, 
                    new String[]{"export_twig","export_txt", "export_h5"}
            );
            
            this.addMenu(menu, "Save...", 
                    new String[]{"Save PDF","Save PNG", "Save SVG"}, 
                    new String[]{"save_pdf","save_png", "save_svg"}
                    );

            menu.add(new JSeparator());
            this.addMenuItem(menu, "Edit Region");
            this.addMenuItem(menu, "Edit Canvas");
        }
        
        @Override
        public JPopupMenu createMenu(Node2D node){
            region = (TGRegion) node;
            return menu;
        }        
    }
    
}

