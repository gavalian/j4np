/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.utils;

import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class EJMLLoader {
    public static int LOADER_DEBUG = 1;
    
    public static EJMLModel load(String archive, String network, int run, String variation) throws Exception {
        ArchiveProvider ap = new ArchiveProvider(archive);
        int runNumber = ap.findEntry(run);
        //System.out.printf("EJMLLoader::load()  archive reader request (run =%5d) found (run =%5d)\n",run,runNumber);
        String archiveFile = String.format("network/%d/%s/%s",runNumber,variation,network);
        //System.out.println(" looking for file : " + archiveFile);
        if(ArchiveUtils.hasFile(archive, archiveFile)==false) 
            throw new Exception(String.format("EJML::Loader:: (ERROR) ::: can't find [%s] in the archive file [%s]", archiveFile,archive));
        //List<String> list = ArchiveUtils.getList(archive);
        //for(String t : list) System.out.println(t);
        List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        EJMLModel model = EJMLModel.create(networkContent);
        if(LOADER_DEBUG>0)
            System.out.printf("EJML::Loader:: (SUCCESS) ::: reading [%s] in the archive file [%s]\n", archiveFile,archive);
        return model;
    }
    
    public static int getRun(String archive, int run)  {
        try {
            ArchiveProvider ap = new ArchiveProvider(archive);
            int runNumber = ap.findEntry(run);
            return runNumber;
        } catch (Exception e){            
        }
        return -1;
    }
    
    public static void main(String[] args){
        try {
            EJMLModel model = EJMLLoader.load("clas12default.network", "trackFixer.network", 6128, "default");
        } catch (Exception ex) {
            System.out.println("Bummer");
        }
    }
}
