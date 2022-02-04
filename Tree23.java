

import java.util.Random;

class Tree23 extends Util {
	// MEMBERS:=========================================
	/////////////////////////////////////////
		InternalNode root = new InternalNode();
		int ht; //height
	// CONSTRUCTORS:====================================
	/////////////////////////////////////////
		Tree23() {// Note: ht=0 iff n=0 (where n is number of items)
			ht = 0; } //  ht=1 implies n=1,2 or 3 
	// METHODS:=========================================
	/////////////////////////////////////////
	InternalNode find (String x){
		// find(x) returns the Internal Node u such that either contains x
		//		or where x would be inserted.  Thus u is a pseudo-leaf.
		//		The node u is NEVER null.
		// 	This is the common helper for search/insert/delete!!
		int count =0;
		InternalNode u = root;
		while(count<ht-1) { 	//height-1 to find within the internal nodes
			if (root.child[0] == null) {
				return root;
			}
			else if (x.compareTo(root.child[0].guide) <= 0) {
				u = (InternalNode) root.child[0];
				count++;
			}
			else if (x.compareTo(root.child[1].guide) <= 0) {
				u = (InternalNode) root.child[1];
				count++;

			} else if ((root.child[2] == null) && (x.compareTo(root.child[1].guide) >= 0)) {
				// System.out.println("1");
				u = (InternalNode) root.child[1];
				count++;
				
			} else {
				u = (InternalNode) root.child[2];
				count++;

				// System.out.println("11");
			}
		}
		return u;
		}//find
	LeafNode search (String x){
		// Returns a null node if failure;
		// 		otherwise, return a LeafNode with the key x.
		InternalNode u = find(x);

		
		//search within the root's child
		if (root == null) { 
			return null; 
			} 
		if((root.child[0]!=null)&&(x.compareTo(root.child[0].guide)) == 0) { 
			//System.out.println((LeafNode)u.child[0]);
			return (LeafNode)u.child[0]; 
			}
		if((root.child[1]!=null)&&(x.compareTo(root.child[1].guide)) == 0) {
			//System.out.println((LeafNode)u.child[1]);
			return (LeafNode)u.child[1]; 
		}
		if((root.child[2]!=null)&&(x.compareTo(root.child[2].guide)) == 0) {
			//System.out.println((LeafNode)u.child[2]);
			return (LeafNode)u.child[2]; 
		}
		//search within the internal node
		for(int i =0; i<4; i++) {
			if((u.child[i] !=null) &&(x.compareTo(u.child[i].guide)==0)) {
				//System.out.println((LeafNode)u.child[i]);
				return (LeafNode)u.child[i]; 
			}
		}
		//System.out.println("null");
		return null; // x is not found
	}
	boolean insert (Item it){
		// insert(it) returns true iff insertion is successful.
		InternalNode u = find(it.key);
		LeafNode leaf = new LeafNode(it);
		// if height =0, make new root
		if (ht == 0) {
			root.child[0] = leaf;
			ht++;
			return true;
		}
		// if insert cannot happen, return false
		else if (u.addLeaf(it) == 0) {
			return false;
		} 
		else {
			if(u == root && u.degree() == 1) {
				u.guide = u.child[0].guide;}
			while (u.newGuide()==true) {
				if (u != root) {
					u = u.parent;
				} else {
					break;
				}
			}
			u = find(it.key);
			//split will be needed if degree > 3
			if (u.degree() == 4) {
					if (u.parent == null) {
						InternalNode u2 = new InternalNode();
						InternalNode u3 = new InternalNode();
						//update parent
						u2.newParent(u, u3, u2);
						u3.newParent(u.child[2], u.child[3], u3);
						//update internal node
						u.child[2] = null;
						u.child[3] = null;
						u.guide = u.child[1].guide;
						u3.guide = u3.child[1].guide;
						root=u2;
						ht++;
						u = u.parent;
					} else {
						InternalNode u2 = new InternalNode();
						//update parent
						u2.newParent(u.child[2], u.child[3], u2);
						u2.parent = u.parent;
						u.parent.child[u.parent.degree()] = u2;
						//update internal node
						u.child[2] = null;
						u.child[3] = null;
						u.guide = u.child[1].guide;
						u2.guide = u2.child[1].guide;
						u.parent.sortNode();
						u = u.parent;
					}
				}
			return true;
		}
		//}
		}//insert
	Item delete (String x){
		// delete(x) returns the deleted item
		// returns null if nothing is deleted.
		InternalNode u = find(x);
		int index = u.parent.getIndexOf(u);
		InternalNode u1 = null;
		Item it = u.removeLeaf(x);
		//if tree or leaf does not exist, deletion fails
		if ((ht == 0)|| (it == null)){
			return null;
		}
		//if degree =3, no merge or adopt is needed
		if(u.degree()==3) {
			u.removeLeaf(x);
			return it;
		}
		//deal with when the internal node only has one children
		while (u.degree() == 1) {
			if (u == root) {
				//System.out.println("u==root");
				root = (InternalNode) u.child[0];
				//update height
				ht --;
				return it;// returns deleted item
			} else {
				if (index == 0) {
					//leftmost child
					u1 = (InternalNode) u.parent.child[1];
					u.parent.proposeRight(index);
				} else if (index!=999){
					u1 = (InternalNode) u.parent.child[0];
					u.parent.proposeLeft(index);
				}
				else {
				return null;
				}
			}
			//updates the internal node
			u = u1.parent;
		}
		return it; 
		}//delete
	// HELPERS:=========================================
	/////////////////////////////////////////
	// DO NOT CHANGE unitTest
	void unitTest() {
		// unit test for Insert+Search+Delete
		// First input: //////////////////////////////////////
		debug("\n======> Inserting Fruits:");
		String[] fruits = { "banana", "apple", "peach", "orange", "apple", "pear", "plum" };
		for (String x : fruits) {
			boolean in = insert(new Item(x));
			debug("insert(" + x + ") = " + String.valueOf(in));
		}
		debug("Here is the final Fruit Tree:");

		showTree();

		debug("\n======> Inserting sqrt(3) digits:");
		// Second input: /////////////////////////////////////
		int[] input = { 1, 7, 3, 2, 0, 5, 0, 8, 0 };
		Tree23 t = new Tree23();
		for (int x : input) {
			Item it = new Item(x);
			boolean in = t.insert(it);
			debug("insert(" + x + ") = " + String.valueOf(in));
		}
		debug("Here is the final sqrt(3) Tree:");
		t.showTree();

		// SEARCHES: //////////////////////////////////////
		debug("\n======> Searching Fruits");
		LeafNode v = search("banana");
		if (v == null)
			debug("Fruit Tree: search(banana) fails");
		else
			debug("Fruit Tree: search(banana) succeeds");
		v = search("cherry");
		if (v == null)
			debug("Fruit Tree: search(cherry) fails");
		else
			debug("Fruit Tree: search(cherry) succeeds");

		debug("\n======> Searching Digits");
		v = t.search(String.valueOf(3));
		if (v == null)
			debug("Sqrt3 Tree: search(3) fails");
		else
			debug("Sqrt3 Tree: search(3) succeeds");
		v = t.search(String.valueOf(4));
		if (v == null)
			debug("Sqrt3 Tree: search(4) fails");
		else
			debug("Sqrt3 Tree: search(4) succeeds");

		// DELETES: //////////////////////////////////////
		debug("\n=============== deleting fruits");
		delete("banana");
		debug("Fruit Tree after DELETE(banana):");
		showTree();
		delete("plum");
		debug("Fruit Tree after DELETE(plum):");
		showTree();
		delete("apricot");
		debug("Fruit Tree after DELETE(apricot):");
		showTree();
		delete("apple");
		debug("Fruit Tree after DELETE(apple):");
		showTree();
		debug("\n=============== deleting digits");
		t.delete(String.valueOf(3));
		debug("Sqrt3 Tree after DELETE(3):");
		t.showTree();
		t.delete(String.valueOf(0));
		debug("Sqrt3 Tree after DELETE(0):");
		t.showTree();
		debug("\n=============== THE END");

	}// unitTest
	
	void unitTest2() {
		//my test 2
		String[] case3 =
			{"f","d","x"};
		for (String x: case3){
			boolean in = insert( new Item(x));
			debug("insert(" + x + ") = " + String.valueOf(in));
		}
		debug("Here is the final Fruit Tree:");
		showTree();
	}

	
	boolean multiInsert (Random rg, int n, int m){
		// Insert n times and then search for m.
		// returns true if m is found.
		
		int[] myArray = new int[n];
		//generate random numbers
		for (int i = 1; i < n; i++) {
			int a=rg.nextInt(20);
			myArray[i] = a;
		}
		//generate the tree
		Tree23 t = new Tree23();
		for (int x : myArray){
			Item it = new Item(x);
			boolean in = t.insert(it);
		}
		debug("\n======> Searching Digits");
		LeafNode v;
		//search tree
		v = t.search(String.valueOf(m));
		if (v==null) {
			debug("search(m) fails");
		return false;
		}
		else {
			debug("search(m) succeeds");
			return true;
		}		
    // Appending new elements at
    // the end of the list
		}//multiInsert
	// MAIN METHOD:=========================
	/////////////////////////////////////////
	public static void main (String[] args) {
		int ss = (args.length > 0) ? Integer.valueOf(args[0]) : 0;
		int nn = (args.length > 1) ? Integer.valueOf(args[1]) : 10;
		int mm = (args.length > 2) ? Integer.valueOf(args[2]) : 2;
   
		Random rg = (ss == 0) ? new Random() : new Random(ss);
		Tree23 tt = new Tree23();
   
		switch (mm) {
			case 0: // unit test for insert+search
				debug("==> mode 0: unit test\n");
				tt.unitTest();
				// tt.test();
				break;
			case 1: // search for "10" once
				debug("==> mode 1: random insert+search once\n");
				tt.multiInsert(rg, nn, 10);
				break;
			case 2: // search for "10" until succeeds
				debug("==> mode 2: random insert+search till success\n");
				while (!tt.multiInsert(rg, nn, 10))
					debug("\n================ Next Trial\n");
				break;
			case 3: // you may add as many cases as you want
					// for your own testing.
				debug("==> mode 2\n");
                tt.unitTest2();
                // tt.test();
                break;
		   
			case 101: // create a random tree with nn random insertions
				debug("==> mode 101: create a random tree\n");
				tt = tt.randomTree(rg, nn, 2 * nn);
				debug("Randomly generated tree of is:");
				tt.showTree();
				break;
			case 102: // create a random tree and randomly delete 100 times until it's empty
				debug("==> mode 102: create a random tree and delete till tree is empty\n");
				tt = tt.randomTree(rg, nn, 2 * nn);
				debug("Randomly generated tree of is:");
				tt.showTree();
				int count = tt.randomDelete(rg, tt, 2 * nn);
				if (tt.ht >= 1) {
					debug("Tree non-empty after 100 random deletes, here is what's left:");
					tt.showTree();
				} else
					debug("After " + String.valueOf(count) + " deletes, tree is empty");
				break;
		}
	}// main
	void showTree () {
		// print all the keys in 23tree:
		int h = ht;
		InternalNode u = root;
		showTree(u, h, "");
		dbug("\n");
	}// showTree

	void showTree (Node u, int h, String offset) {
		// internal recursive call for showTree
		if (h == 0) {
			debug("()");
			return;
		}
		int d = ((InternalNode) u).degree();
		String increment = "G=" + String.valueOf(u.guide) + ":(";
		// Note: "G=" refers to the "guide"
		dbug(increment);
		offset = offset + tab(increment.length() - 1, '-') + "|";
		for (int i = 0; i < d; i++)
			if (h == 1) {
				Node w = ((InternalNode) u).child[i];
				LeafNode v = (LeafNode) w;
				(v.item()).dump();
				if (i == d - 1)
					debug(")");
			} else {
				if (i > 0)
					dbug(offset);
				showTree(((InternalNode) u).child[i], h - 1, offset);
			}
		// dbug(")");
	}// showTree

	

	Tree23 randomTree (Random rg, int n, int N) {
		// Insert n times into empty tree, and return the tree.
		// Use rg as random number generator keys in range [0,N)
		// Keep the size of tree in Util.COUNT
		Tree23 t = new Tree23();
		for (int i = 0; i < n; i++) {
			int x = rg.nextInt(N);
			Item it = new Item(x);
			boolean b = t.insert(it);
			if (b)  COUNT++;
		} // for
		return t;
	}// randomTree

	int randomDelete (Random rg, Tree23 tt, int N) {
		// delete a random element in the tree 100 times until it's empty
		// Use rg as random number generator keys in range [0,N)
		// return the delete count if tree is empty
		int count = 0;
		Item it;
		while (count < 100 && tt.ht > 0) {
			it = tt.delete(String.valueOf(rg.nextInt(N)));
			count++;
		}
		return count;
	}// randomDelete

	void messUpTree (String key) {
		InternalNode u = find(key);
		int deg = u.degree();
		u.swapNodes(0, deg - 1);
	}// messUpTree

	String checkTree () {          
		// returns error message iff the keys are in NOT in sorted order!
		// CAREFUL: we do not check the guides
		int h = ht;
		InternalNode u = root;
		String s = checkTree(u, h, ""); // "" is the globally least key!
		// if (s==null) // May NOT be error! let the caller decide.
		// return "CHECKTREE ERROR";
		return s;
	}
	String checkTree (Node u, int h, String maxkey) {
		// internal recursive call for checkTree
		// returns null if fail; else it is the maximum seen so far!
		if (h == 0)
			return "OK, EMPTY TREE";
		int d = ((InternalNode) u).degree();
		for (int i = 0; i < d; i++)
			if (h == 1) {
				Node w = ((InternalNode) u).child[i];
				LeafNode v = (LeafNode) w;
				if (maxkey.compareTo(v.item().key) >= 0) { // error!
					debug("CHECKTREE ERROR at leaf " + v.item().key);
					debug(maxkey);
					return null;
				} else
					maxkey = v.item().key;
			} else {
				String s1 = checkTree(((InternalNode) u).child[i], h - 1, maxkey);
				if (s1 == null || maxkey.compareTo(s1) >= 0) {
					return null;
				} else
					maxkey = s1;
			}
		return maxkey;
	}// checkTree

}//class Tree23


/*****************************************
HELPER CLASSES: Item and Node
	Item class:
		this is the data of interest to the user
		it determines the remaining details.
	Node class:
		This is the superclass of the two main work horses:
			InternalNode class
		and
			LeafNode class
		The nodes of the Tree23 is made up of these!
	We provide the InternalNode class with useful methods such as
		degree()	-- determine the degree of this node
		addLeaf		-- assumes the children are LeafNodes, and
						we want to add a new leaf.
		removeLeaf	-- converse of addLeaf
		sortNode	-- sort the children of this node
***************************************** */
// Class Item %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
class Item {
		String key;
		int data;
		Item(String k, int d){
			key = k; data = d; }
		Item(String k){// Randomly generate the int data!!!
			key = k; data = (int)(10*Math.random()); }
		Item(int k){ // Use string value of k as key!!!
			key = String.valueOf(k); data = k; }
		Item(Item I){
			key = I.key; data = I.data; }
		// METHODS:
		void dump(){
			System.out.printf("<%s:%d>", key, data); }
		String stringValue(){
			return String.format("<%s:%d>", key, data); }
	}//class Item
	
// Class Node %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
class Node extends Util {
		InternalNode parent;
		String guide;
	}//class Node
	
// Class LeafNode %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
class LeafNode extends Node {
	int data;
	LeafNode(Item I){
		parent=null; guide=I.key; data=I.data;}
	LeafNode(Item I, InternalNode u){
		parent=u; guide=I.key; data=I.data;}
	//MEMBER:
	//////////////////////////////////////////
	Item item(){
		Item it=new Item(guide, data);
		return it; }
	}//class LeafNode

// Class InternalNode %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
class InternalNode extends Node {
	//MEMBERS:
		Node[] child = new Node[4];
	//CONSTRUCTORS:
		InternalNode(){
			this(null,null,null); }
		InternalNode(Node u0, Node u1, InternalNode p){
			this(u0, u1); 
			if (u0!=null) u0.parent=this;
			if (u1!=null) u1.parent=this;}
		//======================================================
		InternalNode(InternalNode u0, InternalNode u1, InternalNode p){
			this((Node)u0, (Node)u1, p); }
		InternalNode(Node u0, Node u1){
			assert(u0==null || u1==null || u0.guide.compareTo(u1.guide)<0);
			if (u1!=null) guide = u1.guide;	
			child[0] = u0; child[1] = u1;
			} // REMEMBER to update u0.parent and u1.parent.
		InternalNode(InternalNode u0, InternalNode u1){
			this((Node)u0, (Node)u1); }
	//METHODS:
	//////////////////////////////////////////
	int degree(){ 
		// get the degree of this InternalNode
		int d = 0;
		for (int i = 0; i < this.child.length; i++) {
			if (child[i] != null) {
				d += 1;
			}
		}
		return d;
	}//degree
	void newParent (InternalNode u0, InternalNode u1, InternalNode p){
		// newParent(u0, u1, p) sets up p as parent of u0, u1.
		newParent((Node)u0, (Node)u1, p); }
	void newParent (Node u0, Node u1, InternalNode p){
		//assert(u0.key < u1.key)
		p.guide = u1.guide;
		p.child[0] = u0;
		p.child[1] = u1; 
		u0.parent = u1.parent = p; }//newParent

	int addLeaf(Item it) {
		// addLeaf(it) returns d:{0,1,2,3,4}
		// where d=0 means addLeaf failed
		// (either it.key is a duplicate or this->degree is 4)
		// else d is the new degree of this InternalNode.
		// ALSO: this InternalNode is sorted.
		// assert(this.child[] are leaves)
		LeafNode le = new LeafNode(it);
		if (this.degree() == 4) {
			return 0; // this->degree is 4, addleaf failed
		} else if (this.degree() !=4){
			for (int i = 0; i < this.degree(); i++) {
				if (child[i].guide != null && (it.key).compareTo(child[i].guide) == 0) {
					return 0;
					// it.key is a duplicate, addleaf failed
				}
			}
			this.child[this.degree()] = le;
			//System.out.println(this.degree());
			this.sortNode();
			return this.degree(); // returns degree, addleaf worked
		}
		return 0;
	}// addLeaf

	Item removeLeaf(String x) {
		// removeLeaf(x) returns the deleted item whose key is x
		// or it returns null if no such item.
		// assert(this.child[] are leaves)
		boolean checker = false;
		int index = 0;
		// check for item whose key is x
		for (int i = 0; i < this.degree(); i++) {
			if (x.compareTo(this.child[i].guide) == 0) {
				//System.out.println(checker);
				checker = true;// item found
				index = i;
			}
		}
		if (checker == false) {
			// item not found, deletion failed
			return null;
		}
		// remove item
		Item removeItem = ((LeafNode) this.child[index]).item();
		this.child[index] = null;
		// shift the child
		this.shiftLeft(index);
		// returns the deleted item
		return removeItem;
	}// removeLeaf
	
	boolean newGuide() {
		//updates the guide
		int i = this.degree() - 1;
		//searches the child
		while (i>0) {
			if (this.child[i] != null) {
				this.guide = child[i].guide;
				return true;
			}
			i--;
		}
		return false;
	}
	int getIndexOf(InternalNode u){
		//		returns the index c such that this->child[c]==u;
		// assert(u is a child of "this")
		InternalNode u1 = u.parent;
		int i =0;
		while(i < u1.child.length - 1) {
			if (child[i].guide != null && child[i].guide.compareTo(u.guide) == 0) {
				//System.out.println(i);
				return i;
			}
			i++;
		}
		//System.out.println("999");
		return 999;
	}//getIndexOf
	void shiftLeft (int c){ // don't forget to delete the last child
		//		this->child[c] is a hole which we must fill up;
		//		so for each i>c: child[i-1] = child[i]
		for (int i = c; i < this.degree(); i++) {
			this.child[i] = this.child[i+1];
		}
		}//shiftLeft
	void shiftRight (int c){
		// shiftRight(c)
		//		create a hole at child[c], so for each i>c,
		//				child[i] = child[i-1]
		//		(but start with i=degree down to i=c+1)
		// ASSERT("c < this->degree < 4");
		for (int i = this.degree(); i>c; i--) {
			child[i] = child[i-1];
		}
		}//shiftRight
	boolean proposeLeft (int c){
		// 			ASSERT("c>0 and child[c].degree=1")
		//		return TRUE if the child[c] merges into child[c-1]
		//		return FALSE child[c] adopts a child of child[c-1].
		//	REMARK:	return TRUE means this is a non-terminal case
		//merge if degree==2
		if (((InternalNode) this.child[c-1]).degree() == 2) { 
			//child[c]'s child[0] become child[2]'s child[c-1]
			((InternalNode) this.child[c-1]).child[2] = ((InternalNode) this.child[c]).child[0];
			//update child[c], as it is now empty
			this.child[c] = null;
			//merge left
			this.shiftLeft(c);
			return true; //merge
		}
		//adopt if degree ==3
		if (((InternalNode) this.child[c-1]).degree() == 3) {
			//temporary storage for child[c]'s child[0]
			Node temp = ((InternalNode) this.child[c]).child[0];
			//child[c-1]'s child[2] become child[c]'s child[0]
			((InternalNode) this.child[c]).child[0] = ((InternalNode) this.child[c-1]).child[2];
			((InternalNode) this.child[c]).child[1] = temp;
			//update the removed child
			((InternalNode) this.child[c-1]).child[2] = null;
			return false; //adopt
		}
		return true;
		}//proposeLeft
	boolean proposeRight (int c){
		// 			ASSERT("c+1<degree and child[c].degree=1")
		//		returns TRUE if the child[c] and child[c+1] are merged.
		//		returns FALSE if child[c] adopts a child of child[c+1].
		// 	REMARKS: under our policy, we KNOW that c==0!
		//		Also TRUE means this is a non-terminal case
		//merge if degree==2
		if (((InternalNode) this.child[c+1]).degree() == 2) { 
			//temporary storage for child[c+1]
			Node temp = ((InternalNode) this.child[c+1]).child[1];
			//child[c+1]'s child[0] becomes child[c+1]'s child[1]
			((InternalNode) this.child[c+1]).child[1] = ((InternalNode) this.child[c+1]).child[0];
			((InternalNode) this.child[c+1]).child[2] = temp;
			//adopts child of child[c+1]
			((InternalNode) this.child[c+1]).child[0] = ((InternalNode) this.child[c]).child[0];
			int i=0;
			while(i<3) {
				this.child[i] = this.child[i+1];
				i++;
			}
			return true;
		}
		//adopt if degree ==3
		if (((InternalNode) this.child[c+1]).degree() == 3) { 
			//child[c+1]'s child[0] becomes child[c]'s child[1]
			((InternalNode) this.child[c]).child[1] = ((InternalNode) this.child[c+1]).child[0];
			((InternalNode) this.child[c+1]).shiftLeft(c);
			return false;
		}
		return true;
		}//proposeRight

	void dump (){// print this node
	}//dump

	void swapNodes(int u0, int u1) {// (u0,u1) <- (u1,u0)
		Node tmp = child[u1];
		child[u1] = child[u0];
		child[u0] = tmp;
	}

	void swapNodes(int u0, int u1, int u2) {// (u0,u1,u2) <- (u2,u0,u1)
		Node tmp = child[u2];
		child[u2] = child[u1];
		child[u1] = child[u0];
		child[u0] = tmp;
	}

	void swapNodes(int u0, int u1, int u2, int u3) {
		// (u0,u1,u2,u3) <- (u3,u0,u1,u2)
		Node tmp = child[u3];
		child[u3] = child[u2];
		child[u2] = child[u1];
		child[u1] = child[u0];
		child[u0] = tmp;
	}

	void sortNode() {
		// We use swapNodes to sort the keys in an InternalNode:
		if (child[1] == null)
			return;
		if (child[0].guide.compareTo(child[1].guide) > 0)
			swapNodes(0, 1);
		// assert(child0 < child1)
		if (child[2] == null)
			return;
		if (child[0].guide.compareTo(child[2].guide) > 0)
			swapNodes(0, 1, 2);
		else if (child[1].guide.compareTo(child[2].guide) > 0)
			swapNodes(1, 2);
		// assert(child0 < child1 < child2)
		if (child[3] == null)
			return;
		if (child[0].guide.compareTo(child[3].guide) > 0)
			swapNodes(0, 1, 2, 3);
		else if (child[1].guide.compareTo(child[3].guide) > 0)
			swapNodes(1, 2, 3);
		else if (child[2].guide.compareTo(child[3].guide) > 0)
			swapNodes(2, 3);
	}// SortNode


}//class InternalNode
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

