/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.base;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static void addInputStream(String zipfile, String outputName, List<String> dataFile){
        String directory = String.format("%s",outputName);
        System.out.println("[exporting] -> " + directory);
        
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
            System.out.println("found file : " + header.getFileName());
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
            System.out.println("found file : " + header.getFileName());
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
    
    public static void main(String[] args){
        
        List<String> data = Arrays.asList("first line","second line","and finally the third line");
        ArchiveUtils.addInputStream("archive.twig", "data.txt", data);
        ArchiveUtils.addInputStream("archive.twig", "directory/data.txt", data);
        ArchiveUtils.addInputStream("archive.twig", "directory/hist/data.txt", data);
        
        ArchiveUtils.list("archive.twig", "*");
        ArchiveUtils.removeFile("archive.twig", "directory/data.txt");
        ArchiveUtils.list("archive.twig", "*");
        
        
        String content = ArchiveUtils.getFile("archive.twig", "directory/hist/data.txt");
        System.out.println(content);
    }
}
