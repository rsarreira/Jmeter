package com.pentaho.services.tree;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
  private TreeNode<T> root;

  public static class TreeNode<T> {
    T item;
    List<TreeNode<T>> parent = new ArrayList<Tree.TreeNode<T>>(); // declared as list to support trash moving/restoring operations
    List<TreeNode<T>> children = new ArrayList<Tree.TreeNode<T>>();

    void addChild( TreeNode<T> child ) {
      children.add( child );
      child.parent.add( this );
    }

    void deleteChild( TreeNode<T> child ) {
      //child.parent.get( parent.size() - 1 ).children.remove( child );
      this.children.remove( child );
    }

    public TreeNode<T> getParent() {
      if ( this.parent == null ) {
        return null;
      }
      if (parent.size() == 0) {
        throw new RuntimeException("Zero parent size identified!");
      }
      TreeNode<T> curParent = this.parent.get( parent.size() - 1 );

      if ( curParent.getItem() == null )
        return null;
      else
        return curParent;
    }
    
    public TreeNode<T> getBackupParent() {
      if ( this.parent.size() < 2 )
        return null;
      else
        return this.parent.get( parent.size() - 2 );
    }
    
    public List<TreeNode<T>> getChildren() {
      return this.children;
    }

    public TreeNode() {
      item = null;
      parent = null;
    }

    public TreeNode( T value ) {
      item = value;
    }

    public T getItem() {
      return item;
    }

    /*
     * public List<String> getParents() { List<String> parents = new ArrayList<String>(); TreeNode node = this.parent;
     * while (node != null) { parents.add(node.getItemValue()); node = node.getParent(); }
     * 
     * return parents; }
     */

    public TreeNode<T> getFirstChild() {
      TreeNode<T> child = null;
      if ( children.size() > 0 ) {
        child = children.get( 0 );
      }

      return child;
    }

    public List<TreeNode<T>> getParents() {
      List<TreeNode<T>> parents = new ArrayList<TreeNode<T>>();

      TreeNode<T> node = this.parent.get( parent.size() - 1 );
      ///TreeNode<T> node = this;
      while ( node != null ) {
        parents.add(node);
        node = node.getParent();
      }

      return parents;
    }
  }

  public Tree() {
    root = new TreeNode<T>();
  }

  public Tree( T item ) {
    root = new TreeNode<T>( item );
  }

  public TreeNode<T> addNode( TreeNode<T> parent, TreeNode<T> newNode ) {
    parent.addChild( newNode );
    return newNode;
  }

  public void moveNode( TreeNode<T> nodeToMove, TreeNode<T> newParent ) {
    deleteNode( nodeToMove );
    addNode( newParent, nodeToMove );
  }

  public void deleteNode( TreeNode<T> node ) {
    node.getParent().deleteChild( node );
  }

  public void restoreNode( TreeNode<T> node ) {
    node.getParent().deleteChild( node );
    addNode( node.getBackupParent(), node );
    //node.restoreParent();
  }

  public TreeNode<T> getRoot() {
    return root;
  }
}
