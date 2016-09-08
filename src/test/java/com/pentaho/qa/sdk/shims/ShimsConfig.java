package com.pentaho.qa.sdk.shims;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ihar_Chekan on 4/17/2015.
 */
public class ShimsConfig {

    public void setupShimToTest(String shimToTest, String pluginsFullPath, String securedOrUnsecured, String clusterConfigsFullPath, String pathToJobs) throws IOException{

        //copy cluster configs
        File copyFrom1 = new File(clusterConfigsFullPath + "\\configs\\" + shimToTest + "\\" + securedOrUnsecured + "\\pentaho-big-data-plugin");
        File copyTo1 = new File(pluginsFullPath + "\\pentaho-big-data-plugin");
        FileUtils.copyDirectory(copyFrom1, copyTo1);

        //copy job configs
        File copyFrom2 = new File(clusterConfigsFullPath + "\\testdata\\" + shimToTest + "\\" + securedOrUnsecured + "\\test.properties");
        File copyTo2 = new File(pathToJobs);
        FileUtils.copyFileToDirectory(copyFrom2, copyTo2);

    }

}
