package com.pentaho.services.puc.browse;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.pentaho.qa.gui.web.puc.BrowseFilesPageEx;
import com.pentaho.services.BaseObject;
import com.pentaho.services.ObjectPool;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.File.Type;
import com.pentaho.services.tree.Tree;
import com.pentaho.services.tree.Tree.TreeNode;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

/*
 * Tricky cases:
 * 1. "Analysis" folder is present inside different folders so activation should be adjusted to use physical path like this path="/public/bi-developers/legacy-steel-wheels/steel-wheels-4.8/analysis"  
 */
public class BrowseService {

  protected static final Logger LOGGER = Logger.getLogger( BaseObject.class );
  private static Tree<BrowseObject> browseTree = new Tree<BrowseObject>();
  private static Folder trash = new Folder( "trash", false, "auto" );
  private static List<File> buffer = new ArrayList<File>();
  private static TreeNode<BrowseObject> nodeHome;

  private static ThreadLocal<BrowseFilesPageEx> browseFilesPage = new ThreadLocal<BrowseFilesPageEx>();

  public static BrowseFilesPageEx getBrowseFilesPageEx() {
    return browseFilesPage.get();
  }

  public static void setBrowseFilesPageEx( BrowseFilesPageEx browsePage ) {
    browseFilesPage.set( browsePage );
  }

  public static void addSampleContent() {
    // Home folder with default user homes
    nodeHome = new TreeNode<BrowseObject>( new Folder( "home", false, "auto" ) );
    TreeNode<BrowseObject> nodeAdmin = new TreeNode<BrowseObject>( new Folder( "admin", false, "auto" ) );
    TreeNode<BrowseObject> nodePat = new TreeNode<BrowseObject>( new Folder( "pat", false, "auto" ) );
    TreeNode<BrowseObject> nodeSuzy = new TreeNode<BrowseObject>( new Folder( "suzy", false, "auto" ) );
    TreeNode<BrowseObject> nodeTiffany = new TreeNode<BrowseObject>( new Folder( "tiffany", false, "auto" ) );

    browseTree.addNode( browseTree.getRoot(), nodeHome );
    browseTree.addNode( nodeHome, nodeAdmin );
    browseTree.addNode( nodeHome, nodePat );
    browseTree.addNode( nodeHome, nodeSuzy );
    browseTree.addNode( nodeHome, nodeTiffany );

    // Public folder with default Steel Wheels reports
    TreeNode<BrowseObject> nodePublic = new TreeNode<BrowseObject>( new Folder( "public", false, "auto" ) );
    TreeNode<BrowseObject> nodeSteelWheels = new TreeNode<BrowseObject>( new Folder( "Steel Wheels", false, "auto" ) );
    TreeNode<BrowseObject> nodeBuyerReport =
        new TreeNode<BrowseObject>( new File( "Buyer Report (sparkline report)", false, Type.PRPT, true, "auto" ) );
    TreeNode<BrowseObject> nodeCountryPerformance =
        new TreeNode<BrowseObject>( new PAReport( "Country Performance (heat grid)", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeDepartmentalSpending =
        new TreeNode<BrowseObject>( new PAReport( "Departmental Spending (bubble chart)", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeEuropeanSales =
        new TreeNode<BrowseObject>( new PAReport( "European Sales (geo map)", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeIncomeStatement =
        new TreeNode<BrowseObject>( new File( "Income Statement", false, Type.PRPT, true, "auto" ) );
    // Inventory List localization
    TreeNode<BrowseObject> nodeInventoryList =
        new TreeNode<BrowseObject>( new File( "inventoryListfile.title", false, Type.PRPT, true, "auto" ) );
    TreeNode<BrowseObject> nodeInvoice =
        new TreeNode<BrowseObject>( new File( "Invoice (report)", false, Type.PRPT, true, "auto" ) );
    TreeNode<BrowseObject> nodeLeadingProductLines =
        new TreeNode<BrowseObject>( new PAReport( "Leading Product Lines (pivot table)", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeProductLine =
        new TreeNode<BrowseObject>( new PAReport( "Product Line Share by Territory", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeProductSales =
        new TreeNode<BrowseObject>( new File( "Product Sales", false, Type.PRPT, true, "auto" ) );
    TreeNode<BrowseObject> nodeRegionalProductMix =
        new TreeNode<BrowseObject>( new File( "Regional Product Mix (dashboard)", false, Type.XDASH, false, "auto" ) );
    TreeNode<BrowseObject> nodeSalesPerformance =
        new TreeNode<BrowseObject>( new File( "Sales Performance (dashboard)", false, Type.XDASH, false, "auto" ) );
    TreeNode<BrowseObject> nodeSalesTrend =
        new TreeNode<BrowseObject>( new PAReport( L10N.getText( "SalesTrend_multi-chart" ), false, Type.XANALYZER,
            true, "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeTerritorySalesAnalysis =
        new TreeNode<BrowseObject>( new File( "Territory Sales Analysis", false, Type.XDASH, false, "auto" ) );
    TreeNode<BrowseObject> nodeTopCustomers =
        new TreeNode<BrowseObject>( new File( "Top Customers", false, Type.PRPT, true, "auto" ) );
    // Vendor Sales Report localization
    TreeNode<BrowseObject> nodeVendorSalesReport =
        new TreeNode<BrowseObject>( new PIRReport( "vendorSalesfile.title", false, Type.PRPTI, true,
            null, "Orders", "auto", "Product Name, Scale, Items Sold, Sales", "Territory, Product Vendor" ) );

    // BI Developer Examples
    TreeNode<BrowseObject> nodeBIDeveloperExamples =
        new TreeNode<BrowseObject>( new Folder( "bi-developers", false, "auto" ) );
    TreeNode<BrowseObject> nodeSteelWheelsLegacy =
        new TreeNode<BrowseObject>( new Folder( "legacy-steel-wheels", false, "auto" ) );
    TreeNode<BrowseObject> nodeSteelWheels4_8 =
        new TreeNode<BrowseObject>( new Folder( "steel-wheels-4.8", false, "auto" ) );
    TreeNode<BrowseObject> nodeAnalysis = new TreeNode<BrowseObject>( new Folder( "analysis", false, "auto" ) );

    TreeNode<BrowseObject> nodeCountrySalesHeatGrid =
        new TreeNode<BrowseObject>( new PAReport( "Country Sales Heat Grid", false, Type.XANALYZER, true, "SampleData",
            "auto" ) );
    TreeNode<BrowseObject> nodeDepartmentalSpending_VarianceBubble =
        new TreeNode<BrowseObject>( new PAReport( "Departmental Spending - Variance Bubble", false, Type.XANALYZER,
            true, "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeEMEASalesMap =
        new TreeNode<BrowseObject>( new PAReport( "EMEA Sales Map", false, Type.XANALYZER, true, "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeProductLineSalesTrend =
        new TreeNode<BrowseObject>( new PAReport( "Product Line Sales Trend", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeProductLineShareByTerritory =
        new TreeNode<BrowseObject>( new PAReport( "Product Line Share by Territory", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );
    TreeNode<BrowseObject> nodeSalesTrendsByLine =
        new TreeNode<BrowseObject>( new PAReport( "Sales Trends by Line", false, Type.XANALYZER, true, "SampleData",
            "auto" ) );
    TreeNode<BrowseObject> nodeTop_5_ProductLinesByTerritory =
        new TreeNode<BrowseObject>( new PAReport( "Top 5 Product Lines by Territory", false, Type.XANALYZER, true,
            "SampleData", "auto" ) );

    browseTree.addNode( browseTree.getRoot(), nodePublic );
    browseTree.addNode( nodePublic, nodeSteelWheels );

    browseTree.addNode( nodeSteelWheels, nodeBuyerReport );
    browseTree.addNode( nodeSteelWheels, nodeCountryPerformance );
    browseTree.addNode( nodeSteelWheels, nodeDepartmentalSpending );
    browseTree.addNode( nodeSteelWheels, nodeEuropeanSales );
    browseTree.addNode( nodeSteelWheels, nodeIncomeStatement );
    browseTree.addNode( nodeSteelWheels, nodeInventoryList );
    browseTree.addNode( nodeSteelWheels, nodeInvoice );
    browseTree.addNode( nodeSteelWheels, nodeLeadingProductLines );
    browseTree.addNode( nodeSteelWheels, nodeProductLine );
    browseTree.addNode( nodeSteelWheels, nodeProductSales );
    browseTree.addNode( nodeSteelWheels, nodeRegionalProductMix );
    browseTree.addNode( nodeSteelWheels, nodeSalesPerformance );
    browseTree.addNode( nodeSteelWheels, nodeSalesTrend );
    browseTree.addNode( nodeSteelWheels, nodeTerritorySalesAnalysis );
    browseTree.addNode( nodeSteelWheels, nodeTopCustomers );
    browseTree.addNode( nodeSteelWheels, nodeVendorSalesReport );

    browseTree.addNode( nodePublic, nodeBIDeveloperExamples );
    browseTree.addNode( nodeBIDeveloperExamples, nodeSteelWheelsLegacy );
    browseTree.addNode( nodeSteelWheelsLegacy, nodeSteelWheels4_8 );
    browseTree.addNode( nodeSteelWheels4_8, nodeAnalysis );

    browseTree.addNode( nodeAnalysis, nodeCountrySalesHeatGrid );
    browseTree.addNode( nodeAnalysis, nodeDepartmentalSpending_VarianceBubble );
    browseTree.addNode( nodeAnalysis, nodeEMEASalesMap );
    browseTree.addNode( nodeAnalysis, nodeProductLineSalesTrend );
    browseTree.addNode( nodeAnalysis, nodeProductLineShareByTerritory );
    browseTree.addNode( nodeAnalysis, nodeSalesTrendsByLine );
    browseTree.addNode( nodeAnalysis, nodeTop_5_ProductLinesByTerritory );

    TreeNode<BrowseObject> trashNode = new TreeNode<BrowseObject>( trash );
    browseTree.addNode( browseTree.getRoot(), trashNode );
  }

  public static Folder getTrash() {
    return trash;
  }

  public static void selectTrash() {
    trash.select();
  }

  // Get BrowseObject by path in browseTree
  public static BrowseObject getBrowseItem( String path ) {
    if ( path.equals( "/" ) ) {
      return browseTree.getRoot().getItem();
    }
    List<String> folders = Utils.parsePath( path );
    int match = 0, i = 1;
    TreeNode<BrowseObject> matchChild = new TreeNode<BrowseObject>();
    if ( getChild( browseTree.getRoot(), folders.get( 0 ) ) != null ) {
      match++;
      matchChild = getChild( browseTree.getRoot(), folders.get( 0 ) );
      while ( i < folders.size() && getChild( matchChild, folders.get( i ) ) != null ) {
        match++;
        matchChild = getChild( matchChild, folders.get( i ) );
        i++;
      }
    }

    if ( match == folders.size() ) {
      return matchChild.getItem();
    } else {
      throw new RuntimeException( "There is no BrowseObject item under path: " + path );
    }
  }

  // Get children of specified parent
  public static List<BrowseObject> getChildren( BrowseObject parent ) {
    TreeNode<BrowseObject> parentNode = getTreeNodeById( parent.getId() );
    List<BrowseObject> content = new ArrayList<BrowseObject>();

    for ( TreeNode<BrowseObject> childNode : parentNode.getChildren() ) {
      content.add( childNode.getItem() );
    }
    return content;
  }

  public static void getRoot() {
    browseTree.getRoot();
  }

  // Add item to browseTree
  public static void addItem( BrowseObject item, Folder parent ) {
    TreeNode<BrowseObject> parentNode = getTreeNodeById( parent.getId() );
    browseTree.addNode( parentNode, new TreeNode<BrowseObject>( item ) );
  }

  // Delete item from browseTree
  public static void deleteItem( BrowseObject item ) {
    TreeNode<BrowseObject> node = getTreeNodeById( item.getId() );
    node.getItem().setId( BaseObject.INVALID_ID );
    browseTree.deleteNode( node );
  }

  // Restore item to browseTree
  public static void restoreItem( BrowseObject item ) {
    TreeNode<BrowseObject> nodeToRestore = getTreeNodeById( item.getId() );
    browseTree.restoreNode( nodeToRestore );
  }

  // Move item to new parent
  public static void moveItem( BrowseObject item, BrowseObject parent ) {
    TreeNode<BrowseObject> nodeToMove = getTreeNodeById( item.getId() );
    TreeNode<BrowseObject> nodeParent = getTreeNodeById( parent.getId() );
    browseTree.moveNode( nodeToMove, nodeParent );
  }

  public static List<Folder> getParents( BrowseObject object ) {
    List<Folder> objectParents = new ArrayList<Folder>();
    for ( TreeNode<BrowseObject> parent : getTreeNodeById( object.getId() ).getParents() ) {
      BrowseObject folderObject = parent.getItem();
      objectParents.add( 0, (Folder) folderObject );
    }
    return objectParents;
  }

  // Get path of BrowseObject element in browseTree
  public static String getPath( BrowseObject object ) {
    List<String> parents = new ArrayList<String>();
    String path = "";

    if ( object == trash ) {
      return ".trash";
    }

    parents.add( object.getName() );
    for ( TreeNode<BrowseObject> parent : getTreeNodeById( object.getId() ).getParents() ) {
      if ( parent.equals( browseTree.getRoot() ) ) {
        break;
      }
      parents.add( parent.getItem().getName() );
    }

    for ( int i = parents.size() - 1; i >= 0; i-- ) {
      path = path + "/" + parents.get( i );
    }

    return path;
  }

  // Get parent of specific BrowseObject
  public static Folder getParent( BrowseObject object ) {
    TreeNode<BrowseObject> nodeObject = getTreeNodeById( object.getId() );
    if ( nodeObject.getParent() == null ) {
      return null;
    } else {
      return (Folder) nodeObject.getParent().getItem();
    }
  }

  // Get node by Id
  public static TreeNode<BrowseObject> getTreeNodeById( int id ) {
    if ( getChildRecursively( browseTree.getRoot(), id ) != null ) {
      return getChildRecursively( browseTree.getRoot(), id );
    }
    LOGGER.error( "Object with id '" + id + "' doesn't exist in browseTree!" );
    return null;
  }
  
  //Get node by name
  public static TreeNode<BrowseObject> getTreeNodeByName( String name ) {
    if ( getChildRecursivelyByName( browseTree.getRoot(), name ) != null ) {
      return getChildRecursivelyByName( browseTree.getRoot(), name );
    }
    LOGGER.error( "Object with name '" + name + "' doesn't exist in browseTree!" );
    return null;
  }

  // Recursive search from base for node with exact Id
  private static TreeNode<BrowseObject> getChildRecursively( TreeNode<BrowseObject> base, int id ) {
    if ( base.getChildren().size() != 0 ) {
      for ( TreeNode<BrowseObject> child : base.getChildren() ) {
        if ( !child.getItem().isValid() ) {
          continue;
        }
        if ( child.getItem().getId() == id ) {
          return child;
        }
        if ( getChildRecursively( child, id ) != null ) {
          return getChildRecursively( child, id );
        }
      }
    }
    return null;
  }
  
  //Recursive search from base for node with exact name
  private static TreeNode<BrowseObject> getChildRecursivelyByName( TreeNode<BrowseObject> base, String name ) {
    if ( base.getChildren().size() != 0 ) {
      for ( TreeNode<BrowseObject> child : base.getChildren() ) {
        if ( !child.getItem().isValid() ) {
          continue;
        }
        if ( child.getItem().getName().equals( name ) ) {
          return child;
        }
        if ( getChildRecursivelyByName( child, name ) != null ) {
          return getChildRecursivelyByName( child, name );
        }
      }
    }
    return null;
  }

  // Get child node by Name
  private static TreeNode<BrowseObject> getChild( TreeNode<BrowseObject> parent, String name ) {
    if ( parent.getChildren().size() != 0 ) {
      for ( TreeNode<BrowseObject> child : parent.getChildren() ) {
        if ( !child.getItem().isValid() ) {
          continue;
        }
        if ( child.getItem().getName().equals( name ) ) {
          return child;
        }
      }
    }
    return null;
  }

  // Set object's children removed (invalid)
  public static void removeChildren( BrowseObject parent ) {
    TreeNode<BrowseObject> parentNode = getTreeNodeById( parent.getId() );
    removeChildren( parentNode );
  }

  // Set children removed (invalid) recursively
  private static void removeChildren( TreeNode<BrowseObject> parent ) {
    if ( parent.getChildren().size() != 0 ) {
      for ( TreeNode<BrowseObject> child : parent.getChildren() ) {
        removeChildren( child );
        child.getItem().setId( BaseObject.INVALID_ID );
      }
    }
  }

  // Set object's children restored (valid)
  public static void restoreChildren( BrowseObject parent ) {
    TreeNode<BrowseObject> parentNode = getTreeNodeById( parent.getId() );
    restoreChildren( parentNode );
  }

  // Set children not removed (valid) recursively
  private static void restoreChildren( TreeNode<BrowseObject> parent ) {
    if ( parent.getChildren().size() != 0 ) {
      for ( TreeNode<BrowseObject> child : parent.getChildren() ) {
        restoreChildren( child );
        child.getItem().setId( ObjectPool.getUniqueId() );
      }
    }
  }

  // Empty Trash from all items
  public static void emptyTrash() {
    BrowseFilesPageEx browseFilesPage = getBrowseFilesPageEx();

    // UI actions
    getTrash().select();
    browseFilesPage.emptyTheTrash();

    // Removing objects from browseTree
    for ( BrowseObject object : getChildren( getTrash() ) ) {
      deleteItem( object );
    }

    // Verification part
    if ( !browseFilesPage.isFolderEmpty() ) {
      Assert.fail( "Trash is not empty in spite of it was cleared!" );
    }
  }

  // Select files holding CTRL button
  public static void selectFiles( List<BrowseObject> files ) {
    // All files should has the same parent
    files.get( 0 ).getParent().select();
    for ( BrowseObject file : files ) {
      getBrowseFilesPageEx().activateFile( file.getName(), true );
    }
  }

  // Put object to buffer
  public static void setBuffer( File file ) {
    buffer.add( file );
  }

  // Retrieve object from buffer
  public static File getBuffer() {
    return buffer.get( 0 );
  }

  // Clear buffer
  public static void clearBuffer() {
    buffer.clear();
  }
  
  public static void addNodeToTree( TreeNode<BrowseObject> newNode ) {
    browseTree.addNode( nodeHome, newNode );
  }
  
  public static void deleteNodeFromTree( TreeNode<BrowseObject> node ) {
    browseTree.deleteNode( node );
  }
}
