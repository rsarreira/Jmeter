package com.pentaho.services.kettle;

import org.apache.log4j.Logger;
import org.pentaho.di.cluster.SlaveServer;

public class PDISlave extends PDIBaseObject {

  protected static final Logger LOGGER = Logger.getLogger( PDISlave.class );
  private SlaveServer slaveServer = null;
  private Repo repo = null;

  public PDISlave( String name, String hostname, String port, String webAppName, String username, String password ) {
    this.name = name;

    slaveServer = new SlaveServer( name, hostname, port, username, password );
    if ( webAppName != null ) {
      slaveServer.setWebAppName( webAppName );
    }
  }

  public void save( Repo repo ) {
    this.repo = repo;
    repo.save( slaveServer );
    LOGGER.info( "Slave server " + name + " (hostname: " + slaveServer.getHostname() + ", port:"
        + slaveServer.getPort() + ", username: " + slaveServer.getUsername() + ", password: "
        + slaveServer.getPassword() + ") created" );
  }

  public SlaveServer getSlaveServer() {
    return slaveServer;
  }

  public Repo getRepo() {
    return repo;
  }
}
