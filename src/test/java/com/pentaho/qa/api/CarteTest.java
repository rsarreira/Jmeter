package com.pentaho.qa.api;

import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.api.CarteRequest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/5/TestCase/11299.aspx
@SpiraTestCase( projectId = 5, testCaseId = SpiraTestcases.CarteTest )
public class CarteTest extends APIBaseTest {
  protected static final Logger LOGGER = Logger.getLogger( CarteTest.class );
  private final String CARTE_ALL_CSV = "CSV_data/CarteTest/Carte_API_All.csv";
  private final String CARTE_TRANS_CSV = "CSV_data/CarteTest/Carte_API_Transformations.csv";
  private final String CARTE_JOBS_CSV = "CSV_data/CarteTest/Carte_API_Jobs.csv";

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58697" )
  // @XlsDataSourceParameters( sheet = "root", dsUid = "Positive", executeColumn = "Execute", executeValue = "Y" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "root", path = CARTE_ALL_CSV )
  public void rootTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.root();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58698" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "status", path = CARTE_ALL_CSV )
  public void statusTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.status();
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58699" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "getSlaves", path = CARTE_ALL_CSV )
  public void getSlavesTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.getSlaves();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58700" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "nextSeq", path = CARTE_ALL_CSV )
  public void nextSequenceTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.nextSequence( args.get( "sequenceName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58701" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "listSocket", path = CARTE_ALL_CSV )
  public void listSocketTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.listSocket( args.get( "host" ) );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58702" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "registerSlave", path = CARTE_ALL_CSV )
  public void registerSlaveTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.registerSlave();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58703" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "allocateSocket", path = CARTE_ALL_CSV )
  public void allocateSocketTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    String startPort = args.get( "startPort" );
    String host = args.get( "host" );
    String clusterId = args.get( "clusterId" );
    String transName = args.get( "transName" );
    String sourceSlave = args.get( "sourceSlave" );
    String sourceStep = args.get( "sourceStep" );
    String sourceCopy = args.get( "sourceCopy" );
    String targetSlave = args.get( "targetSlave" );
    String targetStep = args.get( "targetStep" );
    String targetCopy = args.get( "targetCopy" );

    request.allocateSocket( startPort, host, clusterId, transName, sourceSlave, sourceStep, sourceCopy, targetSlave,
        targetStep, targetCopy );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dependsOnMethods = { "removeTransTest", "removeJobTest" }, dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58704" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "stopCarte", path = CARTE_ALL_CSV )
  public void stopCarteTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.stopCarte();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58705" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "addTrans", path = CARTE_TRANS_CSV )
  public void addTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.addTrans();

    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58707" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "transImage", path = CARTE_TRANS_CSV )
  public void transImageTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.transImage( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58708" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "transStatus", path = CARTE_TRANS_CSV )
  public void transStatusTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.transStatus( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58709" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "prepareExec", path = CARTE_TRANS_CSV )
  public void prepareExecTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.prepareExec( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = { "addTransTest", "prepareExecTest" }, dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58710" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "startExec", path = CARTE_TRANS_CSV )
  public void startExecTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.startExec( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58711" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "startTrans", path = CARTE_TRANS_CSV )
  public void startTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.startTrans( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58712" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "runTrans", path = CARTE_TRANS_CSV )
  public void runTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    String transPath = args.get( "transPath" );
    String transName = args.get( "transName" );
    String logLevel = args.get( "logLevel" );
    request.runTrans( transPath, transName, logLevel );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58713" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "executeTrans", path = CARTE_TRANS_CSV )
  public void executeTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    String repoName = args.get( "repoName" );
    String user = args.get( "repoUser" );
    String password = args.get( "repoPassword" );
    String transPath = args.get( "transPath" );
    String transName = args.get( "transName" );
    String logLevel = args.get( "logLevel" );
    request.executeTrans( repoName, user, password, transPath, transName, logLevel );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58714" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "pauseTrans", path = CARTE_TRANS_CSV )
  public void pauseTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.pauseTrans( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58715" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "stopTrans", path = CARTE_TRANS_CSV )
  public void stopTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.stopTrans( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = { "transImageTest", "prepareExecTest", "startExecTest" }, dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58717" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "removeTrans", path = CARTE_TRANS_CSV )
  public void removeTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.removeTrans( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58716" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "cleanupTrans", path = CARTE_TRANS_CSV )
  public void cleanupTransTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.cleanupTrans( args.get( "transName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addTransTest", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58706" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "sniffStep", path = CARTE_TRANS_CSV )
  public void sniffStepTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.sniffStep( args.get( "transName" ), args.get( "stepName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58718" )
  @CsvDataSourceParameters( dsUid = "Positive", executeColumn = "TestMethod", executeValue = "addJob",
      path = CARTE_JOBS_CSV )
  public void addJobTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.addJob();
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58719" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "jobImage", path = CARTE_JOBS_CSV )
  public void jobImageTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.jobImage( args.get( "jobName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58720" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "jobStatus", path = CARTE_JOBS_CSV )
  public void jobStatusTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.jobStatus( args.get( "jobName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58721" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "runJob", path = CARTE_JOBS_CSV )
  public void runJobTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    String jobPath = args.get( "jobPath" );
    String jobName = args.get( "jobName" );
    String logLevel = args.get( "logLevel" );
    request.runJob( jobPath, jobName, logLevel );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58722" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "executeJob", path = CARTE_JOBS_CSV )
  public void executeJobTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    String repoName = args.get( "repoName" );
    String user = args.get( "repoUser" );
    String password = args.get( "repoPassword" );
    String jobPath = args.get( "jobPath" );
    String jobName = args.get( "jobName" );
    String logLevel = args.get( "logLevel" );
    request.executeJob( repoName, user, password, jobPath, jobName, logLevel );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58723" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "startJob", path = CARTE_JOBS_CSV )
  public void startJobTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.startJob( args.get( "jobName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58724" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "stopJob", path = CARTE_JOBS_CSV )
  public void stopJobTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.stopJob( args.get( "jobName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ) );
    request.verify( args.get( "expectedBody" ), false );
    request.softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58725" )
  @CsvDataSourceParameters( executeColumn = "TestMethod", executeValue = "removeJob", path = CARTE_JOBS_CSV )
  public void removeJobTest( Map<String, String> args ) {
    CarteRequest request = new CarteRequest( args );
    request.removeJob( args.get( "jobName" ) );
    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );
    request.softAssert.assertAll();
  }

}
