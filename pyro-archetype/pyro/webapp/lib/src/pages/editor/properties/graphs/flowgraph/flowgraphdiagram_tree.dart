import 'package:FlowGraphTool/src/model/flowgraph.dart' as flowgraph;
import 'package:FlowGraphTool/src/model/core.dart';
import 'package:FlowGraphTool/src/model/tree_view.dart';

class FlowGraphDiagramTreeBuilder
{
	Tree getTree(IdentifiableElement element)
	{
		Tree tree = new Tree();
		//for every complex attribute
		//for every type
		if(element!=null) {
			//instanceofs
			if(element.$type() == "flowgraph.FlowGraphDiagram"){
				tree.root = new FlowGraphDiagramTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.End'){
				tree.root = new EndTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.Swimlane'){
				tree.root = new SwimlaneTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.SubFlowGraph'){
				tree.root = new SubFlowGraphTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.Transition'){
				tree.root = new TransitionTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.Start'){
				tree.root = new StartTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.Activity'){
				tree.root = new ActivityTreeNode(element,element);
			}
			if(element.$type() == 'flowgraph.LabeledTransition'){
				tree.root = new LabeledTransitionTreeNode(element,element);
			}
		}
		return tree;
	}
}

/// node, edge, container, graphmodel type
class FlowGraphDiagramTreeNode extends TreeNode {
	String name;
	
	FlowGraphDiagramTreeNode(IdentifiableElement root, flowgraph.FlowGraphDiagram element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "FlowGraphDiagram";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class EndTreeNode extends TreeNode {
	String name;
	
	EndTreeNode(IdentifiableElement root, flowgraph.End element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "End";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class SwimlaneTreeNode extends TreeNode {
	String name;
	
	SwimlaneTreeNode(IdentifiableElement root, flowgraph.Swimlane element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "Swimlane";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class SubFlowGraphTreeNode extends TreeNode {
	String name;
	
	SubFlowGraphTreeNode(IdentifiableElement root, flowgraph.SubFlowGraph element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "SubFlowGraph";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class TransitionTreeNode extends TreeNode {
	String name;
	
	TransitionTreeNode(IdentifiableElement root, flowgraph.Transition element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "Transition";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class StartTreeNode extends TreeNode {
	String name;
	
	StartTreeNode(IdentifiableElement root, flowgraph.Start element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "Start";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class ActivityTreeNode extends TreeNode {
	String name;
	
	ActivityTreeNode(IdentifiableElement root, flowgraph.Activity element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "Activity";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

class LabeledTransitionTreeNode extends TreeNode {
	String name;
	
	LabeledTransitionTreeNode(IdentifiableElement root, flowgraph.LabeledTransition element, {String this.name,TreeNode parent})  : super(root)
	{
		if(name==null){
			name = "LabeledTransition";
		}
		if(parent!=null){
			super.parent = parent;
		}
		delegate = element;
	}
	
	@override
	TreeNode createChildren(String child) {
		// for all complex not list attributes
		return null;
	}
	
	@override
	List<String> getPossibleChildren() {
		List<String> possibleElements = new List<String>();
		//for all complex not list attributes
		//check upper bound for single value
		return possibleElements;
	}
  
	@override
	bool isChildRemovable(TreeNode node)
	{
		switch(node.name){
		}
		return true;
	}
	
	@override
	bool isSelectable() => true;
	
	@override
	bool isRemovable() {
		return canRemove();
	}
	
	@override
	void removeAttribute(String name) {
		//for each complex not list attribute
	}
}

