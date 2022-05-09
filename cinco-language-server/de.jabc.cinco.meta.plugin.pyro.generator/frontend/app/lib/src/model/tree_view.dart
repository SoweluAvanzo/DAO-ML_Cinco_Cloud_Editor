import 'core.dart';

class Tree {
  TreeNode root;
}

abstract class TreeNode {
  List<TreeNode> children;
  String symbol;
  bool open;
  dynamic delegate;
  TreeNode parent;
  final IdentifiableElement root;

  TreeNode(this.root)
  {
    children = new List<TreeNode>();
  }

  String get name;

  List<String> getPossibleChildren();

  TreeNode createChildren(String child);

  void removeChild(TreeNode node)
  {
    children.remove(node);
    removeAttribute(node.name);
  }

  void removeAttribute(String name);

  bool isSelectable();

  bool canRemove(){
    if(parent!=null) {
      return parent.isChildRemovable(this);
    }
    return false;
  }

  bool isRemovable();

  bool isChildRemovable(TreeNode node)
  {
    return true;
  }

  merge(TreeNode t,  Map<dynamic, TreeNode> cache, Set<TreeNode> removed) {
    if(cache.containsKey(this.getId()))
      return;
    cache[this.getId()] = this;

    if(t.equals(this)) {
      var deprecatedChildren  = new List<TreeNode>();
      deprecatedChildren.addAll(this.children);
      var newChildren  = new List<TreeNode>();
      if(this.children != null) {
        newChildren.addAll(t.children);
      }
      
      // merge all children which are in both sets
      // and identify those who are not
      for(var c1 in this.children) {
        for(var c2 in t.children) {
          if(c1.equals(c2)) {
            c1.merge(c2, cache, removed);
            deprecatedChildren.remove(c1);
            newChildren.remove(c2);
            if(removed.contains(c1)) {
              removed.remove(c1);
            }
          }
        }
      }
      // add missing new children
      for(var u in newChildren) {
        u.parent = this;
        if(cache.containsKey(u.getId())) {
          this.children.add(cache[u.getId()]);
        } else {
          children.add(u);
        }
      }
      // remove deprecated children
      for(var u in deprecatedChildren) {
        this.children.remove(u);
        if(!cache.containsKey(u.getId())) {
          removed.add(u);
        }
      }
    }
  }
  
  getId() {
    if(this.delegate is IdentifiableElement) {
      return (this.delegate as IdentifiableElement).id;
    } else {
      return this.delegate.hashCode;
    }
  }
  
  equals(TreeNode t) {
    return this.getId() != null
      && t.getId() != null
      && this.getId() == t.getId();
  }
}

