package com.pentaho.qa.demo;

import org.apache.log4j.Logger;

import com.pentaho.services.api.http.ClientFactory;
import com.pentaho.services.api.http.CustomResponse;

public class ClientTest {
  protected static final Logger LOGGER = Logger.getLogger( ClientTest.class );

  public static void main( String[] args ) {
    CustomResponse customResponce =
        ClientFactory.getInstance().sendGet(
                "http://pubservicesv2.bamnetworks.com/lookup/named.schedule_vw_sponsors.bam?team_id=121&season=2014&start_date=20140401&end_date=20140430&game_type=%27E%27&game_type=%27S%27&game_type=%27R%27&game_type=%27F%27&game_type=%27D%27&game_type=%27L%27&game_type=%27W%27&ovrd_enc=utf-8&schedule_vw_complete.sort_column=%27game_time_et%27%20Calendar:%20http://pubservicesv2.bamnetworks.com/lookup/named.schedule_vw_sponsors.bam?team_id=121&season=2014&start_date=20140401&end_date=20140430&game_type=%27E%27&game_type=%27S%27&game_type=%27R%27&game_type=%27F%27&game_type=%27D%27&game_type=%27L%27&game_type=%27W%27&ovrd_enc=utf-8" );

    LOGGER.info( customResponce.getHeaders().length );
    LOGGER.info( customResponce.getUrl() );
    LOGGER.info( customResponce.getStatusCode() );
    LOGGER.info( customResponce.getRequestBody() );
    LOGGER.info( customResponce.getResponseBody() );
  }
}
