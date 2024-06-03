/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.finder;

import j4ml.ejml.EJMLModel;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class EJMLLoader {
    public static EJMLModel load(String archive, String network, int run, String variation){
        ArchiveProvider ap = new ArchiveProvider(archive);
        int runNumber = ap.findEntry(run);
        System.out.printf("EJMLLoader::load()  archive reader request (run=%5d) found (run=%d)\n",run,runNumber);
        String archiveFile = String.format("network/%d/%s/%s",runNumber,variation,network);
        List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        EJMLModel model = EJMLModel.create(networkContent);
        return model;
    }
}
