package com.pentaho.services.api.http;

import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;

public class ClientFactory {

  private static Client instance;
  private static final String proxyHost = Configuration.get( Parameter.PROXY_HOST );
  private static final String proxyPort = Configuration.get( Parameter.PROXY_PORT );

  private ClientFactory() {
  }

  public static Client getInstance() {
    if ( instance == null ) {
      if ( proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && !proxyPort.isEmpty() ) {
        instance = getInstance( proxyHost, Integer.parseInt( proxyPort ), ProxyType.HTTP );
      } else {
        instance = new Client();
      }
    }
    return instance;
  }

  public static Client getInstance( String proxyHost, int proxyPort, ProxyType proxyType ) {
    if ( instance == null ) {
      synchronized ( Client.class ) {
        if ( instance == null ) {
          instance = new Client( proxyHost, proxyPort, proxyType );
        }
      }
    }
    return instance;
  }

  public static void close() {
    instance.close();
    instance = null;
  }

}
