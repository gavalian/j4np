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
import j4np.utils.io.OptionParser;
import j4np.utils.io.OptionStore;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;


/**
 *
 * @author gavalian
 */
public class ArchiveUtils {
    private String achiveName = "";
    public ArchiveUtils(){
        
    }
    
    public static InputStream createFromList(List<String> dataFile) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();        
        int nsize = dataFile.size();
        for(int i = 0; i < nsize; i++){
            baos.write(dataFile.get(i).getBytes());
            baos.write("\n".getBytes());
        }        
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static boolean removeFile(String zipfile, String file){
        try {
            ZipFile zip = new ZipFile(zipfile);
            zip.removeFile(file);
        } catch (ZipException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static void writeFile(String zipfile, String outputName, List<String> fileLines){
        InputStream stream = null;
        try {
            stream = ArchiveUtils.createFromList(fileLines);
            ArchiveUtils.writeInputStream(zipfile, outputName, stream);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void writeInputStream(String zipfile, String outputName, InputStream stream){
        String directory = String.format("%s",outputName);
        //System.out.println("[exporting] -> " + directory);
        
        try {
            ZipFile zip = new ZipFile(zipfile);
            ZipParameters pars = new ZipParameters();
            pars.setOverrideExistingFilesInZip(true);
            pars.setFileNameInZip(directory);
            zip.addStream(stream, pars);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public static InputStream getInputStream(String zipfile, String filename){
        InputStream inputStream = null;
        
        ZipFile zip = new ZipFile(zipfile);
        try {
            FileHeader header = zip.getFileHeader(filename);
            //System.out.println("found file : " + header.getFileName());
            inputStream = zip.getInputStream(header);            
        } catch (ZipException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
       return inputStream;
    }
    
    public static void addInputStream(String zipfile, String outputName, List<String> dataFile){
        String directory = String.format("%s",outputName);
        //System.out.println("[exporting] -> " + directory);
        
        try {
            InputStream stream = ArchiveUtils.createFromList(dataFile);
            ZipFile zip = new ZipFile(zipfile);
            ZipParameters pars = new ZipParameters();
            pars.setOverrideExistingFilesInZip(true);
            pars.setFileNameInZip(directory);
            zip.addStream(stream, pars);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean hasFile(String zipfile, String file){
        try {
            ZipFile zip = new ZipFile(zipfile);
            List<FileHeader> headers = zip.getFileHeaders();
            for(int i = 0; i < headers.size(); i++){
                String name = headers.get(i).getFileName();
                if(name.compareTo(file)==0) return true;
                //System.out.println("name : " + name + " has = " + name.compareTo(file));
            }                   
        } catch (ZipException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static List<String> getList(String zipfile){
        return ArchiveUtils.getList(zipfile, ".*");
    }
    
    public static List<String> getList(String zipfile, String pattern){
        
        List<String>  files = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        try {
            ZipFile zip = new ZipFile(zipfile);
            List<FileHeader> headers = zip.getFileHeaders();
            for(int i = 0; i < headers.size(); i++){
                String name = headers.get(i).getFileName();
                Matcher m = r.matcher(name);
                if(m.find()==true){ files.add(name);}
                //System.out.println("name : " + name + " has = " + name.compareTo(file));
            }
            
        } catch (ZipException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Collections.sort(files);

        /*System.out.println("- LIST OF THE FILES IN THE ARCHIVE");
        System.out.println("-------------");
        for(int i = 0; i < files.size(); i++){
            System.out.printf(": %s\n",files.get(i));
        }
        System.out.println("-------------\n\n");*/
        return files;
    }
    
    public static void list(String zipfile, String pattern){
            List<String>  files = new ArrayList<>();
            try {
                ZipFile zip = new ZipFile(zipfile);
                List<FileHeader> headers = zip.getFileHeaders();
                for(int i = 0; i < headers.size(); i++){
                    String name = headers.get(i).getFileName();
                    files.add(name);
                    //System.out.println("name : " + name + " has = " + name.compareTo(file));
                }
                
            } catch (ZipException ex) {
                Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Collections.sort(files);

        System.out.println("- LIST OF THE FILES IN THE ARCHIVE");
        System.out.println("-------------");
        for(int i = 0; i < files.size(); i++){
            System.out.printf(": %s\n",files.get(i));
        }
        System.out.println("-------------\n\n");
    }
        
        
    public static List<String>  getFileAsList(String zipfile, String filename){
        
        List<String>  content = new ArrayList<>();
        
        ZipFile zip = new ZipFile(zipfile);
        try {
            FileHeader header = zip.getFileHeader(filename);
            //System.out.println("found file : " + header.getFileName());
            InputStream inputStream = zip.getInputStream(header);
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            for (String line; (line = reader.readLine()) != null; ) {
                content.add(line);
            }
            
        } catch (ZipException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }
    
    public static String  getFile(String zipfile, String filename){
                
        StringBuilder str = new StringBuilder();
        
        ZipFile zip = new ZipFile(zipfile);
        try {
            FileHeader header = zip.getFileHeader(filename);
            //System.out.println("found file : " + header.getFileName());
            InputStream inputStream = zip.getInputStream(header);
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            for (String line; (line = reader.readLine()) != null; ) {
                str.append(line);
            }            
        } catch (ZipException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str.toString();
    }
       
    
    public static void show(String file, String system){
        List<String>  items = ArchiveUtils.getList(file,  system);
        System.out.println(" number of items = " + items.size());
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(120).height(items.size()*4+1);
        Table table = new Table(3, items.size());
        
        ///table.setElement(1, 1, new Text(items.get(0)),true);
        //table.setElement(1, 2, new Text(items.get(1)),true);
        
        for(int i = 0; i < items.size(); i++){
            table.setElement(1, i+1, new Text(items.get(i) + "\n" + items.get(i)),true);
        }
        
        builder.element(table);
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();
        System.out.println(s);
    }
    
    public static void main(String[] args){
        String file = "ejmlclas12.network";
        List<String> list = ArchiveUtils.getList(file);
        
        list.stream().forEach(System.out::println);
        
       
        /*
        
        OptionStore parser = new OptionStore("archive");
        
        parser.addCommand("-extract", "extract file from archive");
        parser.addCommand("-list", "list the files in the archive");
        parser.addCommand("-import", "import a file into archie");
        
        parser.getOptionParser("-extract").addRequired("-f", "file name to extract");
        parser.getOptionParser("-extract").addOption("-r", "false","remove the file from archive");
        
        parser.getOptionParser("-import").addRequired("-f", "file name to import");
        parser.getOptionParser("-import").addRequired("-d", "directory in the archive");
        
        parser.parse(args);
        
        if(parser.getCommand().compareTo("-list")==0){
            List<String> inputs = parser.getOptionParser("-list").getInputList();
            ArchiveUtils.list(inputs.get(0), "*");            
        }
        
        if(parser.getCommand().compareTo("-extract")==0){
            List<String> inputs = parser.getOptionParser("-extract").getInputList();
            String         file = parser.getOptionParser("-extract").getOption("-f").stringValue();
            String       delete = parser.getOptionParser("-extract").getOption("-r").stringValue();
            int           index = file.lastIndexOf("/");
            String     diskFile = file.substring(index+1);
            //ArchiveUtils.list(inputs.get(0), "*");    
            System.out.println("exporting file : " + file);
            System.out.println("     into file : " + diskFile);
            
            List<String> content = ArchiveUtils.getFileAsList(inputs.get(0), file);
            TextFileWriter w = new TextFileWriter();
            w.open(diskFile);
            for(String line : content)
                w.writeString(line);
            w.close();
            
            if(delete.compareTo("true")==0){
                ArchiveUtils.removeFile(inputs.get(0), file);
            }
        }
        
        if(parser.getCommand().compareTo("-import")==0){
            List<String>  inputs = parser.getOptionParser("-import").getInputList();
            String          file = parser.getOptionParser("-import").getOption("-f").stringValue();
            String           dir = parser.getOptionParser("-import").getOption("-d").stringValue();
            List<String> content = TextFileReader.readFile(file);
            String   archiveName = dir + "/" + file;
            ArchiveUtils.addInputStream(inputs.get(0), archiveName, content);
        }
        */
        /*
        List<String> data = Arrays.asList("first line","second line","and finally the third line");
        ArchiveUtils.addInputStream("archive.twig", "data.txt", data);
        ArchiveUtils.addInputStream("archive.twig", "directory/data.txt", data);
        ArchiveUtils.addInputStream("archive.twig", "directory/hist/data.txt", data);
        
        ArchiveUtils.list("archive.twig", "*");
        ArchiveUtils.removeFile("archive.twig", "directory/data.txt");
        ArchiveUtils.list("archive.twig", "*");
        
        
        String content = ArchiveUtils.getFile("archive.twig", "directory/hist/data.txt");
        System.out.println(content);
        */
    }
}
