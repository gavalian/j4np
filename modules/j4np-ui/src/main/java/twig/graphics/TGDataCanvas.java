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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;
import twig.config.TStyle;
import twig.data.DataSet;
import twig.data.H1F;
import twig.data.H2F;
import twig.editors.CanvasEditorPanel;
import twig.editors.DataCanvasEditorDialog;
import twig.studio.TwigStudio;
import twig.widgets.Line;
import twig.widgets.PaveText;
import twig.widgets.WidgetEditor;

/**
 *
 * @author gavalian
 */
public class TGDataCanvas extends Canvas2D implements ActionListener {

    private int activeRegion = 0;
    private CanvasPopupProvider popupProvider = null;//new CanvasPopupProvider();
    private boolean drawRegionsEmpty = false;
    
    public TGDataCanvas(){
        TStyle style = TStyle.getInstance();
        Color color = TStyle.getInstance().getPalette().getColor(style.getCanvasBackgroundColor());
        Background2D back = Background2D.createBackground(color.getRed(),color.getGreen(),color.getBlue());
        setBackground(back);        
        popupProvider = new CanvasPopupProvider(this);
        this.setPopupProvider(popupProvider);
        divide(1,1);
    }
    
    public void setDrawEmptyRegions(boolean flag){
        drawRegionsEmpty = flag;
    }
    
    public void divide(double[][] fractions){
        int ncolumns = fractions.length;
        int size = 0;
        for(int i = 0; i < ncolumns; i++) size += fractions[i].length;
        System.out.println("DIVIDING CANVAS (cols): " + ncolumns + " TOTAL SIZE = " + size);
        this.getGraphicsComponents().clear();
        for(int i = 0; i < size; i++) {
            addNode(new TGRegion(drawRegionsEmpty));
        }
        arrange(fractions);
    }
    

    public void divide(double left, double bottom,int cols, int rows){
        this.getGraphicsComponents().clear();
        for(int i = 0; i < cols*rows; i++){
            TGRegion pad = new TGRegion(drawRegionsEmpty);
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
            TGRegion pad = new TGRegion(drawRegionsEmpty);
            this.addNode(pad);
        }
        this.arrange(cols, rows);
        this.repaint();
        this.activeRegion = 0;
    }
    
    public TGRegion region(){ 
        return (TGRegion) getGraphicsComponents().get(activeRegion);
    }
    
    public TGDataCanvas addLabels(double x, double y){
        return addLabels(x,y,'a');
    }
    
    public TGDataCanvas addLabels(double x, double y, String[] labels){
        int nRegions = this.count();        
        for(int i = 0; i < labels.length; i++){
            PaveText ta = new PaveText(labels[i],x,y);
            ta.setNDF(true);
            ta.setDrawBox(false);
            ta.setFillBox(false);
            ta.setFont(region(i).axisX().getAttributes().getAxisLabelFont());
            region(i).draw(ta);
        }

        return this;
    }  
    /*
    public void initTimer(int interval) {
        System.out.println("[EmbeddedCanvas] ---->  starting an update timer.");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                update();
              
            }
        };
        updateTimer = new Timer("EmbeddeCanvasTimer");
        updateTimer.scheduleAtFixedRate(timerTask, 30, interval);
    }*/
    
    public TGDataCanvas addLabels(double x, double y, char start){
        int nRegions = this.count();
        char label = start;
        for(int i = 0; i < nRegions; i++){
            PaveText ta = new PaveText(label+")",x,y);
            ta.setNDF(true);
            ta.setDrawBox(false);
            ta.setFillBox(false);
            ta.setFont(region(i).axisX().getAttributes().getAxisLabelFont());
            region(i).draw(ta);
            label++;
        }

        return this;
    }  
    
    public TGDataCanvas cd(int index){
        if(index < 0) { activeRegion = 0; return this;}
        
        if(index >= this.getGraphicsComponents().size()){
            activeRegion = this.getGraphicsComponents().size() - 1;
            return this;
        } 
        activeRegion = index;
        return this;
    }
    
    public TGDataCanvas next(){
        activeRegion++;
        if(activeRegion>=getGraphicsComponents().size()){
            activeRegion = 0;
        }

        return this;
    }
    
    public TGRegion region(int index){ 
        return (TGRegion) getGraphicsComponents().get(index);         
    }
    
    public int count(){
        return getGraphicsComponents().size();
    }
    
    public BufferedImage getScreenShot() {
        BufferedImage bi = new BufferedImage(
                this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        this.paint(bi.getGraphics());
        return bi;
    }
    
    public void export(String filename){
        if(filename.endsWith("png")||filename.endsWith("PNG")){
            this.export(filename,"png"); return;
        }
        if(filename.endsWith("pdf")||filename.endsWith("PDF")){
            this.export(filename,"pdf"); return;
        }
        System.out.println("<canvas.export> unable to determine file type from name : " + filename);
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
        if(type.compareTo("PNG")==0||type.compareTo("png")==0){
            File imageFile = new File(filename);
            try {
                imageFile.createNewFile();
                ImageIO.write(getScreenShot(), "png", imageFile);
            } catch (Exception ignored) {
            }
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

    public TGDataCanvas setAxisTileFont(Font font){
        for(Node2D node : getGraphicsComponents()){
            ((TGRegion) node).getAxisFrame().getAxisX().getAttributes().setAxisTitleFont(font);
            ((TGRegion) node).getAxisFrame().getAxisY().getAttributes().setAxisTitleFont(font);
        }
        return this;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().compareTo("New Canvas")==0){
            if(popupProvider.region!=null){
                List<TDataNode2D> obj = popupProvider.region.getAxisFrame().dataNodes;
                //TwigStudio.getInstance().getCopyBuffer().clear();
                
                //System.out.println("copy region : size = " + obj.size());
                TGCanvas c = new TGCanvas("canvas",500,500,false);
                
                for( TDataNode2D dn : obj){
                    c.view().region().draw(dn.getDataSet(),"same");
                    //TwigStudio.getInstance().getCopyBuffer().add(dn.dataSet);
                    //System.out.println("copied : name = " + dn.dataSet.getName());
                }
            } else {
                System.out.println("no region was selected....");
            }
        }
        
        if(e.getActionCommand().compareTo("projection_x")==0){
            if(popupProvider.region!=null){
                List<TDataNode2D> obj = popupProvider.region.getAxisFrame().dataNodes;
                if(obj.get(0).getDataSet() instanceof H2F){
                    TGCanvas c = new TGCanvas("canvas",500,500,false); 
                    H1F h = ((H2F)obj.get(0).getDataSet()).projectionX();
                    c.view().region().draw(h);
                }
            }
        }
        if(e.getActionCommand().compareTo("projection_y")==0){
            if(popupProvider.region!=null){
                List<TDataNode2D> obj = popupProvider.region.getAxisFrame().dataNodes;
                if(obj.get(0).getDataSet() instanceof H2F){
                    TGCanvas c = new TGCanvas("canvas",500,500,false); 
                    H1F h = ((H2F)obj.get(0).getDataSet()).projectionY();
                    c.view().region().draw(h);
                }
            }
        }
        
        if(e.getActionCommand().compareTo("profile_x")==0){
            if(popupProvider.region!=null){
                List<TDataNode2D> obj = popupProvider.region.getAxisFrame().dataNodes;
                if(obj.get(0).getDataSet() instanceof H2F){
                    TGCanvas c = new TGCanvas("canvas",500,500,false); 
                    H1F h = ((H2F)obj.get(0).getDataSet()).profileX();
                    c.view().region().draw(h);
                }
            }
        }
        
        if(e.getActionCommand().compareTo("Copy Region")==0){
            if(popupProvider.region!=null){
                List<TDataNode2D> obj = popupProvider.region.getAxisFrame().dataNodes;
                TwigStudio.getInstance().getCopyBuffer().clear();
                
                //System.out.println("copy region : size = " + obj.size());
                for( TDataNode2D dn : obj){
                    TwigStudio.getInstance().getCopyBuffer().add(dn.dataSet);
                    //System.out.println("copied : name = " + dn.dataSet.getName());
                }
            } else {
                System.out.println("no region was selected....");
            }
        }
        
        if(e.getActionCommand().compareTo("Paste Region")==0){
            if(popupProvider.region!=null){
                
                TGRegion reg = popupProvider.region;                                
                //System.out.println("copy region : size = " + obj.size());
                for( DataSet ds : TwigStudio.getInstance().getCopyBuffer()){
                    reg.draw(ds,"same");
                    //TwigStudio.getInstance().getCopyBuffer().add(dn.dataSet);
                    //System.out.println("pasting : name = " + ds.getName());
                }
            } else {
                System.out.println("no region was selected....");
            }
            this.repaint();
        }
        
        if(e.getActionCommand().compareTo("save_pdf")==0){
            this.export(this.getName()+".pdf","PDF");            
            //this.save(this.getName()+".pdf");
        }
        
        if(e.getActionCommand().compareTo("save_svg")==0){
            this.save(this.getName()+".svg");
        }
        if(e.getActionCommand().compareTo("Edit Canvas")==0){
            TGRegion reg = popupProvider.region;
            if(reg!=null){
                CanvasEditorPanel.openOptionsPanel(this, reg);
            } else {                            
                DataCanvasEditorDialog.openOptionsPanel(this);
            }
            
        }
        
        if(e.getActionCommand().compareTo("Edit Region")==0){
            TGRegion reg = popupProvider.region; 
            DataCanvasEditorDialog.openOptionsAttributes(this,reg);
        }
        
        
        if(e.getActionCommand().compareTo("set_canvas_palette_2d")==0){
            TStyle.getInstance().getPalette().palette2d().choosePalette(this);
            this.repaint();
        }
                
        if(e.getActionCommand().startsWith("divide_c_")==true){
            String divSize = e.getActionCommand();
            switch(divSize){
                case "divide_c_1x1": this.divide(1, 1); break;
                case "divide_c_1x2": this.divide(1, 2); break;
                case "divide_c_2x1": this.divide(2, 1); break;
                case "divide_c_2x2": this.divide(2, 2); break;
                
                case "divide_c_1x3": this.divide(1, 3); break;
                case "divide_c_3x1": this.divide(3, 1); break;
                case "divide_c_2x3": this.divide(2, 3); break;
                case "divide_c_3x2": this.divide(3, 2); break;
                case "divide_c_3x3": this.divide(3, 3); break;
                default: this.divide(1, 1);
            }
        }
        
        if(e.getActionCommand().compareTo("show_region_legend")==0){
            if(popupProvider.region!=null){                
                TGRegion reg = popupProvider.region;
                reg.showLegend(0.05, 0.95);
                this.repaint();
            }
        }
        
        if(e.getActionCommand().compareTo("hide_region_legend")==0){
            if(popupProvider.region!=null){                
                TGRegion reg = popupProvider.region;
                reg.hideLegend();
                this.repaint();
            }
        }
        
        if(e.getActionCommand().compareTo("edit_region_legend")==0){
            if(popupProvider.region!=null){                
                TGRegion reg = popupProvider.region;
                reg.editLegendPosition();
                this.repaint();
            }
        }
        
        if(e.getActionCommand().compareTo("set_log_y_true")==0){
            if(popupProvider.region!=null){                
                TGRegion reg = popupProvider.region;
                reg.getAxisFrame().setLogY(true);
                this.repaint();
            }
        }

        if(e.getActionCommand().compareTo("set_log_y_false")==0){
            if(popupProvider.region!=null){
                TGRegion reg = popupProvider.region;
                reg.getAxisFrame().setLogY(false);
                this.repaint();
            }
        }
                
        if(e.getActionCommand().compareTo("show_region_stats")==0){
            if(popupProvider.region!=null){                
                TGRegion reg = popupProvider.region;
                reg.showStats(0.985, 0.975);
                this.repaint();
            }
        }
        
        if(e.getActionCommand().compareTo("hide_region_stats")==0){
            if(popupProvider.region!=null){                
                TGRegion reg = popupProvider.region;
                reg.hideStats();
                this.repaint();
            }
        }
        
        if(e.getActionCommand().compareTo("add_region_text")==0){
            if(popupProvider.region!=null){
                PaveText t = new PaveText("text",0.15,0.85);
                popupProvider.region.getAxisFrame().addWidget(t);
                this.repaint();
            }            
        }
        
        if(e.getActionCommand().compareTo("add_region_line")==0){
            if(popupProvider.region!=null){
                Line t = new Line(0.,0.5,1.0,0.5);
                t.setNDF(true);
                popupProvider.region.getAxisFrame().addWidget(t);
                this.repaint();
            }            
        }
        
        if(e.getActionCommand().compareTo("edit_region_widgets")==0){
            if(popupProvider.region!=null){
                WidgetEditor editor = new WidgetEditor(popupProvider.region.axisFrame.widgetNodes, this);
                editor.show();
                this.repaint();
            }
        }
        
        if(e.getActionCommand().compareTo("region_duplicate")==0){
            if(popupProvider.region!=null){                
                List<TDataNode2D> obj = popupProvider.region.getAxisFrame().dataNodes;
                TGCanvas c = new TGCanvas("canvas",500,500,false);                
                for( TDataNode2D dn : obj){
                    c.view().region().draw(dn.getDataSet(),"same");                   
                }
            }
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
            this.addMenu(menu, "Data", 
                    new String[]{"Projection X","Projection Y",
                        "Profile X", "Profile Y"}, 
                    new String[]{"projection_x","projection_y",
                        "profile_x","profile_y"}
            );
            menu.add(new JSeparator());
            
            this.addMenu(menu, "Export", 
                    new String[]{"Export twig","Export txt", "Export h5"}, 
                    new String[]{"export_twig","export_txt", "export_h5"}
            );
            
            this.addMenu(menu, "Import", 
                    new String[]{"From twig","From txt", "From h5"}, 
                    new String[]{"import_twig","import_txt", "import_h5"}
            );
            
            this.addMenu(menu, "Save...", 
                    new String[]{"Save PDF","Save PNG", "Save SVG"}, 
                    new String[]{"save_pdf","save_png", "save_svg"}
                    );

            menu.add(new JSeparator());
            this.addMenu(menu, "Divide", 
                    new String[]{"1x1","1x2", "2x1","2x2","3x1","1x3","2x3","3x3"}, 
                    new String[]{"divide_c_1x1","divide_c_1x2", "divide_c_2x1",
                        "divide_c_2x2","divide_c_3x1","divide_c_1x3",
                        "divide_c_2x3","divide_c_3x3"}
                    );
            
            menu.add(new JSeparator());
            
            //addMenuItem(menu, "New Canvas");
            
            this.addMenu(menu, "Region", 
                    new String[]{"Duplicate" ,"Show Legend","Hide Legend",
                        "Edit Legend",
                        "Show Stats","Hide Stats","Edit Stats",
                        "Add Text","Add Line","Edit Widgets",                        
                        "Log Y", "Lin Y"}, 
                    new String[]{"region_duplicate", 
                        "show_region_legend", "hide_region_legend","edit_region_legend",
                        "show_region_stats","hide_region_stats","edit_region_stats",
                        "add_region_text","add_region_line","edit_region_widgets",
                        "set_log_y_true","set_log_y_false"}
            );
            
            menu.add(new JSeparator());
            this.addMenuItem(menu, "Edit Region");
            this.addMenuItem(menu, "Edit Canvas");
            
            this.addMenu(menu, "Configure", 
                    new String[]{"Set Palette" ,"Set Palette 2D"}, 
                    new String[]{"set_canvas_palette", "set_canvas_palette_2d"}
            );
        }
        
        @Override
        public JPopupMenu createMenu(Node2D node){
            region = (TGRegion) node;
            return menu;
        }        
    }
    
}

