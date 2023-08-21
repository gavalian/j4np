/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.base;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.Table;
import com.indvd00m.ascii.render.elements.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipFile;

/**
 * This class is writing organized data into directories 
 * that can be interpreted as run dependent data. Similar
 * to Calibration Database. Why ? - Why not ?
 * @author gavalian
 */
public class ArchiveProvider {
    
    private String    flavor = "default";
    private String    archiveFile = "";
    private String    system = "network";
    
    public ArchiveProvider(String file, String fl){
        this.archiveFile = file;
        this.flavor = fl;
    }
    
    public ArchiveProvider(String file){
        this.archiveFile = file;    
    }
    
    public void setFlavor(String __fl){
        flavor = __fl;
        System.out.printf("\n\n::: switched the flavor to : %s\n\n",flavor);
        System.out.printf("::: current configuration\n");
        System.out.printf("::: archive   file : %s\n",archiveFile);
        System.out.printf("::: archive flavor : %s\n",flavor);        
    }
    
    public List<String> getFile(String filename, int run){
        int adjustedRun = findEntry(run);
        String path = String.format("%s/%d/%s/%s", system,adjustedRun,flavor,filename);
        System.out.println("[archive:provider] -> reading file : " + path);
        return ArchiveUtils.getFileAsList(archiveFile,path);
    }
    
    public boolean hasFileForRun(int run, String path){
        //ZipFile zipFile = null;
        //reader.
        int derivedRun = findEntry(run);
        String filename = String.format("%s/%d/%s/%s", system,derivedRun, this.flavor, path);
        String filter   = String.format("%s/%d/%s", system, derivedRun, this.flavor);
        List<String>  fileList = ArchiveUtils.getList(archiveFile, filter);        
        for(String entry : fileList){
            System.out.println( entry + " " + (entry.compareTo(filename)==0));
            if(entry.compareTo(filename)==0) return true;
        }
        return false;
    }
    
    
    public boolean hasFile(String filename, int run){
        int adjustedRun = findEntry(run);
        String path = String.format("%s/%d/%s/%s", system,adjustedRun,flavor,filename);
        return true;
    }
    
    public String findDirectory(int run){
        int runNumber = this.findEntry(run);
        return String.format("%s/%d",system, runNumber);
    }
    
    public String findDirectory(int run, String flavor){
        int runNumber = this.findEntry(run);
        String dir = String.format("%s/%d/%s", system,runNumber,flavor);
        return dir;
    }
    /**
     * copy file from same flavor to the same flavor for different
     * run number
     * @param srcRun source run number
     * @param dstRun destination run number
     * @param file filename
     */
     public void copyFile(int srcRun, int dstRun,  String file){
         copyFile(srcRun,flavor,dstRun,flavor,file);
     }
    /**
     * Copy a file into a new run range
     * @param srcRun source run number
     * @param srcFlavor source flavor
     * @param dstRun destination run number
     * @param dstFlavor destination flavor
     * @param file filename to copy
     */
    public void copyFile(int srcRun, String srcFlavor, 
            int dstRun, String dstFlavor, String file){
        
        int      realRun = this.findEntry(srcRun);
        String   srcFile = String.format("%s/%d/%s/%s", 
                system,realRun,srcFlavor,file);
        String   dstFile = String.format("%s/%d/%s/%s", 
                system,dstRun,dstFlavor,file);
        if(dstFile.compareTo(srcFile)==0){
            System.out.printf("\n redundant copy : \n %s ==> %s\n",srcFile,dstFile);
        } else {
            
            List<String> content = ArchiveUtils.getFileAsList(archiveFile, srcFile);
            ArchiveUtils.writeFile(archiveFile, dstFile, content);
            System.out.printf("\n\n copy success : \n %s ==> %s\n",srcFile,dstFile);
        }
    }
    
    /**
     * Copy a file into a new run range
     * @param srcRun source run number
     * @param srcFlavor source flavor
     * @param dstRun destination run number
     * @param dstFlavor destination flavor
     * @param file filename to copy
     */
    protected void copyFileToFile(int srcRun, String srcFlavor, 
            int dstRun, String dstFlavor, String file, String newArchiveFile){
        
        int      realRun = this.findEntry(srcRun);
        String   srcFile = String.format("%s/%d/%s/%s", 
                system,realRun,srcFlavor,file);
        String   dstFile = String.format("%s/%d/%s/%s", 
                system,dstRun,dstFlavor,file);
            
        List<String> content = ArchiveUtils.getFileAsList(archiveFile, srcFile);
        ArchiveUtils.writeFile(newArchiveFile, dstFile, content);
        System.out.printf("\n\n copy success : \n %s ==> %s\n",srcFile,dstFile);
        
    }
    
    public void copy(int srcRun, int dstRun){
        this.copy(srcRun, flavor, dstRun, flavor);
    }
    /**
     * Copy all files in for the given run number into entry for different
     * run number.
     * 
     * @param srcRun
     * @param srcFlavor
     * @param dstRun
     * @param dstFlavor
     */
    public void copy(int srcRun, String srcFlavor, int dstRun, String dstFlavor){
        int  realRun = this.findEntry(srcRun);        
        String directory = this.findDirectory(srcRun, srcFlavor);
        System.out.printf("\nrun %d search... entry found %d\n\n",srcRun,realRun);
        
        List<String> fileList = ArchiveUtils.getList(archiveFile, directory);
        for(String fn : fileList){
            String file = fn.replace(directory+"/", "");
            //System.out.println("copy file -> " + fn + "  : " + file);
            this.copyFile(srcRun, srcFlavor, dstRun, dstFlavor, file);
        }
    }
    
    public void copyToFile(int srcRun, String srcFlavor, String newArchiveFile){
        int  realRun = this.findEntry(srcRun);        
        String directory = this.findDirectory(srcRun, srcFlavor);
        //System.out.printf("\nrun %d search... entry found %d\n\n",srcRun,realRun);
        List<String> fileList = ArchiveUtils.getList(archiveFile, directory);
        for(String fn : fileList){
            String file = fn.replace(directory+"/", "");
            //System.out.println("copy file -> " + fn + "  : " + file);
            this.copyFileToFile(srcRun, srcFlavor, realRun, srcFlavor, file, newArchiveFile);
            System.out.printf(":::: copy run = %8d (%8d) to new archive : %s\n",
                    srcRun, realRun, newArchiveFile);
        }        
    }
    
    public void copyToFile(int runStart, int runEnd, String srcFlavor, String newArchiveFile){
        List<Integer> runList = this.getRunList();
        Collections.sort(runList);
        
        for(Integer run : runList){
            if(run>=runStart&&run<=runEnd)
            this.copyToFile(run, srcFlavor, newArchiveFile);
        }

    }
    
    public void removeFile(int run, String file){
        this.removeFile(run, flavor, file);
    }
    
    public void removeFile(int run, String flavor, String file){
        int  realRun = this.findEntry(run);        
        String directory = this.findDirectory(run, flavor);
        String fullPath  = String.format("%s/%s", directory,file);
        if(ArchiveUtils.hasFile(archiveFile, fullPath)==true){
            ArchiveUtils.removeFile(archiveFile, fullPath);
        } else {
            System.out.println("\n\n::: file not found : " + fullPath + "\n\n");
        }        
    }
    
    public void remove(int run){
        int  realRun = this.findEntry(run);        
        String directory = this.findDirectory(run, flavor);
        System.out.printf("\nrun %d search... entry found %d\n\n",run,realRun);
        
        List<String> fileList = ArchiveUtils.getList(archiveFile, directory);
        for(String fn : fileList){
            ArchiveUtils.removeFile(archiveFile, fn);
            System.out.println("\t removing file : " + fn);
        }
    }
    
    public List<Integer> getRunList(){
       String         filter = String.format(".*/.*/%s", flavor); 
       List<String> directories = ArchiveUtils.getList(archiveFile, filter);
       Set<Integer> runSet = new HashSet<>();
        for(String directory : directories){
            String[] tokens = directory.split("/");
            Integer   value = Integer.parseInt(tokens[1]);
            runSet.add(value);
            //System.out.println(value);
        }
        List<Integer> array = new ArrayList<>();        
        for(Integer item : runSet) array.add(item);        
        Collections.sort(array);
        return array;
    }
    
    public List<String> getComment(int run){
        List<String>  comments = new ArrayList<>();
        int      realRun = this.findEntry(run);
        String directory = this.findDirectory(run, flavor);
        
        System.out.printf("\nrun %d search... entry found %d\n\n",run,realRun);
        String comment = String.format("%s/%d/%s/comment.txt", 
                system,realRun,flavor);
        if(ArchiveUtils.hasFile(archiveFile, comment)){
            List<String> fileContent = ArchiveUtils.getFileAsList(archiveFile, comment);
            comments.addAll(fileContent);
        } else {
            comments.add("......");
        }
        return comments;
    }
    
    public List<String> getComments(List<Integer> runlist){
        List<String>  comments = new ArrayList<>();
        for(int i = 0; i < runlist.size(); i++){
            String comment = String.format("%s/%d/%s/comment.txt", 
                    system,runlist.get(i),flavor);
            if(ArchiveUtils.hasFile(archiveFile, comment)){
                List<String> fileContent = ArchiveUtils.getFileAsList(archiveFile, comment);
                StringBuilder str = new StringBuilder();
                fileContent.stream().forEach(e -> str.append(e).append(";"));
                comments.add(str.toString());
            } else { comments.add("...");}
            
        }
        return comments;
    }
    /**
     * returns set of existing flavors in the file, can be printed.
     * @param archive archive file name
     * @return set of flavors
     */
    public static Set<String>  getFlavorSet(String archive){
        List<String> files = ArchiveUtils.getList(archive);
        SortedSet<String> result = new TreeSet<>();
        for(String file : files){
            String[] tokens = file.split("/");
            if(tokens.length>=3)
                result.add(tokens[2]);
        }
        return result;
    }
    /**
     * Displays run tables for all flavors that exist in the archive
     * @param archive archive file name.
     */
    public static void scan(String archive){
        Set<String> flavors = ArchiveProvider.getFlavorSet(archive);
        for(String flavor : flavors){
            ArchiveProvider provider = new ArchiveProvider(archive,flavor);
            provider.showRunList();
        }
    }
    /**
     * Shows files that are present in the archive for give run number.
     * The entries shown are determined by the run ranges that applies.
     * @param run run number to investigate.
     */
    public void showFiles(int run){
        
        int  realRun = this.findEntry(run);        
        String directory = this.findDirectory(run, flavor);
        System.out.printf("\nrun %d search... entry found %d\n\n",run,realRun);
        
        List<String> fileList = ArchiveUtils.getList(archiveFile, directory);
        for(String fn : fileList){
            System.out.println("\t -> " + fn);
        }
    }
    
    /*public List<Integer>  getRunList(){
        List<Integer> runList = new ArrayList<>();
        
    }*/
    /**
     * Shows list of runs for for given flavor. The flavor can be
     * specified in the constructor.
     */
    public void showRunList(){
        
        List<Integer>  items = this.getRunList();
        List<String>  comments = this.getComments(items);
        
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(120).height(items.size()*2+3);
        Table table = new Table(3, items.size()+1);
        
        ///table.setElement(1, 1, new Text(items.get(0)),true);
        //table.setElement(1, 2, new Text(items.get(1)),true);
        table.setElement(1, 1, new Text(" RUN"),true);
        table.setElement(2, 1, new Text(" RANGE"),true);
        table.setElement(3, 1, new Text(" COMMENTS"),true);
        if(items.size()==1){
            table.setElement(1, 2, new Text(items.get(0).toString()));
            table.setElement(2, 2, new Text(String.format("%6d -    inf",0)));
            table.setElement(3, 2, new Text(comments.get(0))); 
        } else {
            for(int i = 0; i < items.size(); i++){
                String range = "";
                if(i==0){
                    range = String.format("%6d - %6d", 0,items.get(i+1)-1);
                } else {
                    if(i==items.size()-1){
                        range = String.format("%6d -    inf",items.get(i));
                } else {
                        range = String.format("%6d - %6d", items.get(i),items.get(i+1)-1);
                    }
                }
                //Text t = new Text(items.get(i).toString());
            
                table.setElement(1, i+2, new Text(items.get(i).toString()));
                table.setElement(2, i+2, new Text(range));
                table.setElement(3, i+2, new Text(comments.get(i)));            
            }
        }
        
        builder.element(table);
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();
        System.out.println("\n");
        System.out.println("SUMMARY Archive : " + archiveFile);
        System.out.println("SUMMARY  Flavor : " + flavor + "\n");
        
        System.out.println(s);
    }
    
    public void addComment(int run, String... comments){
       List<String>  list = Arrays.asList(comments);
       String file = String.format("%s/%d/%s/comment.txt", system,run,flavor);
       ArchiveUtils.writeFile(archiveFile, file, list);
    }
    
    public Integer findEntry(int run){
        
        List<Integer> array = this.getRunList();
        //for(int i = 0; i < array.size(); i++) System.out.printf("%4d : %6d \n",i,array.get(i));
        int index = Collections.binarySearch(array,run);
        //System.out.println(" seach for "  + run + " , index =  " + index);

        if(index>=0)  return array.get(index);
        if(index==-1) return array.get(0);
        int trueIndex = Math.abs(index)-2;
        return array.get(trueIndex); 
    }
    
    public boolean writeFile(int run, String filename, List<String> fileLines){
        String comment = String.format("%s/%d/%s/%s", 
                    system,run,flavor,filename);
        if(ArchiveUtils.hasFile(archiveFile, comment)){
            System.out.println("\n\n (archive) -> overwriting file : " + comment + "\n\n");
        }
        ArchiveUtils.writeFile(archiveFile, comment, fileLines);
        return true;
    }
    
    
    public static void main(String[] args){        
        ArchiveProvider provider = new ArchiveProvider("ejmlclas12.network");        
        boolean flag = provider.hasFileForRun(5048, "trackClassifier.network");
        System.out.println("has file = " + flag);
        int index = 0;
        provider.hasFileForRun(5038, "trackParametersPositive.network");
        provider.addComment(5038, "trained on 03/14/32","inbending data");
        provider.showRunList();
        
        ArchiveProvider.scan("ejmlclas12.network");
        
        /*
        index = provider.findEntry(5);
        System.out.println("RUN = " + index);
        index = provider.findEntry(1000);
        System.out.println("RUN = " + index);
        index = provider.findEntry(1200);
        System.out.println("RUN = " + index);        
        index = provider.findEntry(5038);
        System.out.println("RUN = " + index);
        index = provider.findEntry(5100);
        System.out.println("RUN = " + index);
        index = provider.findEntry(5800);
        System.out.println("RUN = " + index);
        index = provider.findEntry(6800);
        System.out.println("RUN = " + index);
        index = provider.findEntry(7600);
        System.out.println("RUN = " + index);
        index = provider.findEntry(8014);
        System.out.println("RUN = " + index);
        
        
        List<String> fileContent = provider.getFile("trackClassifier.network", 5060);
        System.out.println("lines read = " + fileContent.size());*/
    }
}
