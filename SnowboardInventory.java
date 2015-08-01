/**
 *SnowboardInventory.java
 *
 *Myanna Harris
 *3-25-13
 *Kennewick High School
 *
 *Java
 *Keep Snowboard Shop's inventory records
 */

import java.util.*;
import java.lang.*;
import java.util.UUID;
import java.io.*;
import javax.swing.JOptionPane;

class Inventory
{
	//data structures holding item information
	private HashMap<UUID, Item> inventoryHashMap;
	private BinaryTree brandTree;
	private BinaryTree typeTree;

	//used to keep track of the number of item "records" in inventory (Items.dat)
	private int numFiles;
	private int numRecords;

	//Inventory file
	private RandomAccessFile ranFileItems;

	//brings in item information from ranFileItems and organizes it into the data structures
	public Inventory() throws IOException
	{
		inventoryHashMap = new HashMap<UUID, Item>();
		brandTree = new BinaryTree();
		typeTree = new BinaryTree();

		BufferedReader inStream;

		ranFileItems = new RandomAccessFile("Items.dat","rw");

		File f = new File("files.dat");
		if(f.exists())
		{
			inStream = new BufferedReader(new FileReader(f));
			String number = inStream.readLine();

			if(number == null)
			{
				numFiles = 0;
				numRecords = 0;
			}
			else
			{
				JOptionPane.showMessageDialog(null,"Incorrect input");
				numFiles = Integer.parseInt(number);
				numRecords = Integer.parseInt(inStream.readLine());
			}

			inStream.close();
		}
		else
		{
			f.createNewFile();
			numFiles = 0;
		}

		for(int x = 1; x <= numFiles; x++)
		{
			Item temp = new Item(SnowboardInventoryRAFile.readFromFile(ranFileItems,x));
			if(!temp.getBrand().equals("Skip"))
			{
				inventoryHashMap.put(temp.getBarcode(), temp);
				brandTree.add(brandTree.getRoot(), temp.getBrand(), temp);
				typeTree.add(typeTree.getRoot(), temp.getType(), temp);
			}
		}
	}

	public HashMap<UUID, Item> getHashMap(){return inventoryHashMap;}
	public int getNumFiles(){return numFiles;}
	public void incNumFiles(){numFiles++;}

	//sell
	//removes item from inventory and puts into records
	public void removeFromInventory(UUID removedUUID, String stat) throws IOException
	{
		Item removedItem = inventoryHashMap.get(removedUUID);
		inventoryHashMap.remove(removedUUID);
		brandTree.remove(removedItem.getBrand(), removedItem);
		typeTree.remove(removedItem.getType(), removedItem);

		removedItem.setStatus(stat);
		addItemToRecords(removedItem);
		removeItemFromInventory(removedItem);
	}

	//puts removed item into records
	public void addItemToRecords(Item removedItem) throws IOException
	{
		File r = new File("Records.dat");
		if(!r.exists())
		{
			r.createNewFile();
		}

		BufferedWriter outRecords = new BufferedWriter(new FileWriter(r));
		outRecords.write(removedItem.toString());
		outRecords.close();
	}

	//puts removed item into records
	public void removeItemFromInventory(Item removedItem) throws IOException
	{
		SnowboardInventoryRAFile.deleteFromFile(ranFileItems, removedItem.getNum());
	}

	//buy
	//adds new item to inventory
	public void addToInventory(Item addedItem) throws IOException
	{
		inventoryHashMap.put(addedItem.getBarcode(), addedItem);
		brandTree.add(brandTree.getRoot(), addedItem.getBrand(), addedItem);
		typeTree.add(typeTree.getRoot(), addedItem.getType(), addedItem);

		addItemToInventory(addedItem);
	}

	//puts removed item into records
	public void addItemToInventory(Item addedItem) throws IOException
	{
		SnowboardInventoryRAFile.writeToFile(ranFileItems, numFiles, addedItem);
	}

	//rent
	//sets status to rented out so it is known as unavailable to customers
	public void setFlag(Item rentedItem)
	{
		rentedItem.setStatus("Rented Out");
		SnowboardInventoryRAFile.writeStatus(ranFileItems, rentedItem.getNum(), "Rented Out");
	}

	//sets status to Available so it is known as available to customers
	public void removeFlag(Item returnedItem)
	{
		returnedItem.setStatus("Available");
		SnowboardInventoryRAFile.writeStatus(ranFileItems, returnedItem.getNum(), "Available");
	}

	//search
	//searches with the unique barcode by using the inventoryHashMap
	public String byBarcode(UUID searchUUID)
	{
		String tempString = "";
		if(inventoryHashMap.get(searchUUID) != null )
			tempString = (inventoryHashMap.get(searchUUID)).toString();
		else
			tempString = "No Items Found";
		return tempString;
	}

	//searches with the brand using the brandTree (which is a binary tree)
	public String byBrand(String searchBrand)
	{
		String tempString = "";
		ArrayList<Item> tempArray = brandTree.search(brandTree.getRoot(), searchBrand);
		if(tempArray == null)
		{
			tempString = "No Items Found";
		}
		else
		{
			for(int x = 0; x < tempArray.size(); x++)
				tempString += tempArray.get(x);
		}
		return tempString;
	}

	//searches with the type using the typeTree (which is a binary tree)
	public String byType(String searchType)
	{
		String tempString = "";
		ArrayList<Item> tempArray = typeTree.search(typeTree.getRoot(), searchType);

		if(tempArray == null)
		{
			tempString = "No Items Found";
		}
		else
		{
			for(int x = 0; x < tempArray.size(); x++)
				tempString += tempArray.get(x);
		}
		return tempString;
	}

	//returns a list of all items in inventory
	public String all() throws IOException
	{
		String tempString = "";

		if(numFiles != 0)
		{
			ArrayList<Item> tempArray = SnowboardInventoryRAFile.display(ranFileItems);
			for(int x = 0; x < tempArray.size(); x++)
				tempString += tempArray.get(x);
		}
		else
		{
			tempString = "No Items in Inventory";
		}

		return tempString;
	}

	//returns a list of all item information in the records
	public String records() throws IOException
	{
		String tempString = "";

		if(numRecords != 0)
		{
			File r = new File("Records.dat");
			if(!r.exists())
			{
				r.createNewFile();
			}
			BufferedReader inRecords = new BufferedReader(new FileReader(r));

			for(int x = 1; x <= numRecords; x++)
				tempString += inRecords.readLine() + "\n";

			inRecords.close();
		}
		else
			tempString = "No Items in Records";

		return tempString;
	}

	//writes all inventory to ranFileItems and closes all files being used before exiting
	public void close()
	{
		try
		{
			ranFileItems.close();

			File f = new File("files.dat");
			f.delete();

			f.createNewFile();

			BufferedWriter outStream = new BufferedWriter(new FileWriter(f));
	      outStream.write(Integer.toString(numFiles) + "\n");
	      outStream.write(Integer.toString(numRecords));
			outStream.close();

			System.exit(0);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error occured when saving");
		}
	}
}

//Item user-defined object
class Item
{
	//Item attributes
	private String brand;
	private String type;
	private UUID barcode;
	private String size;
	private double price;
	private String RentalSale;
	private String status;
	private int num;

	//create an empty Item object
	public Item()
	{
		brand = "";
		type = "";
		barcode = null;
		size = "";
		price = 0.0;
		RentalSale = "";
		status = "";
		num = 0;
	}

	//item from file
	public Item(Item copiedItem)
	{
		brand = copiedItem.getBrand();
		type = copiedItem.getType();
		barcode = copiedItem.getBarcode();
		size = copiedItem.getSize();
		price = copiedItem.getPrice();
		RentalSale = copiedItem.getRentalSale();
		status = copiedItem.getStatus();
		num = copiedItem.getNum();
	}

	//new item with new information
	public Item(String br, String t, String sz, double p, String rs, String st, int n)
	{
		brand = br;
		type = t;
		barcode = generateUUID();
		size = sz;
		price = p;
		RentalSale = rs;
		status = st;
		num = n;
	}

	//makes a UUID
	public UUID generateUUID()
	{
		return UUID.randomUUID();
	}

	//overwrite toString of Item object for easier display
	public String toString()
	{
		return "Brand: " + brand + "\nType: " + type + "\nBarcode: " + barcode + "\nSize: " + size
			+ "\nPrice: " + price + "\nRentalSale: " + RentalSale + "\nStatues: " + status + "\n";
	}

	//access attributes
	public String getBrand(){return brand;}
	public String getType(){return type;}
	public UUID getBarcode(){return barcode;}
	public String getSize(){return size;}
	public double getPrice(){return price;}
	public String getRentalSale(){return RentalSale;}
	public String getStatus(){return status;}
	public int getNum(){return num;}

	//alter attributes
	public void setBrand(String br){brand =br;}
	public void setType(String t){type = t;}
	public void setBarcode(UUID bc){barcode = bc;}
	public void setSize(String sz){size = sz;}
	public void setPrice(double p){price = p;}
	public void setRentalSale(String rs){RentalSale = rs;}
	public void setStatus(String st){status = st;}
	public void setNum(int n){num = n;}
}

//ADT
class BinaryTree
{
	//attributes
	private TreeNode root;

	//construct empty tree
	public void BinaryTree()
	{
		root = new TreeNode(null, null, null, null);
	}

	//access attribute
	public TreeNode getRoot(){return root;}

	//adds new node in order
	public void add(TreeNode node, String newString, Item newItem)
	{
		if(root == null)
		{
			root  = new TreeNode(newString, newItem, null, null);
		}
		else if(node == null)
		{
			node  = new TreeNode(newString, newItem, null, null);
		}
		else
		{
			if(newString.compareTo(node.getValue()) < 0)
			{
				if(node.getLeft() != null)
				{
					add(node.getLeft(), newString, newItem);
				}
				else
				{
					node.setLeft(new TreeNode(newString, newItem, null, null));
				}
			}
			else if(newString.compareTo(node.getValue()) > 0)
			{
				if(node.getRight() != null)
				{
					add(node.getRight(), newString, newItem);
				}
				else
				{
					node.setRight(new TreeNode(newString, newItem, null, null));
				}
			}
			else if(newString.compareTo(node.getValue()) == 0)
			{
				node.setRef(newItem);
			}
		}

	}

	//removes specific item reference
	public void remove(String removedString, Item removedItem)
	{
		if(root != null)
		{
			ArrayList<TreeNode> tempArray = searchToRemove(root, root, removedString);
			TreeNode tempNode = tempArray.get(0);
			TreeNode t = tempArray.get(1);

			if(tempNode.getLeft() == null && tempNode.getRight() == null)		// must be a leaf node
			{
				if(tempNode.getRef().size() > 1)
				{
					tempNode.removeRef(removedItem);
				}
				else
					deleteLeaf(tempNode,t);
			}
			else if(tempNode.getLeft() == null || tempNode.getRight() == null)	// must be a parent with one child
			{
				if(tempNode.getRef().size() > 1)
				{
					tempNode.removeRef(removedItem);
				}
				else
					deleteParent1(tempNode,t);
			}
			else													// must be a parent with two children
			{
				if(tempNode.getRef().size() > 1)
				{
					tempNode.removeRef(removedItem);
				}
				else
					deleteParent2(tempNode);
			}
		}
	}

	//USED BY REMOVE METHOD: searches tree for specific string and returns ArrayList of corresponding Items
	public ArrayList<TreeNode> searchToRemove(TreeNode node, TreeNode t, String searchString)
	{
		if(node == null)		//empty
			return null;
		else if(searchString.equals(node.getValue()))		//first
		{
			ArrayList<TreeNode> tempArray = new ArrayList<TreeNode>();
			tempArray.add(node);
			tempArray.add(t);
			return tempArray;
		}
		else if(searchString.compareTo(node.getValue()) < 0)		//less
			return searchToRemove(node.getLeft(), node, searchString);
		else														//greater
			return searchToRemove(node.getRight(), node, searchString);
	}

	//searches tree for specific string and returns ArrayList of corresponding Items
	public ArrayList<Item> search(TreeNode node, String searchString)
	{
		if(node == null)		//empty
			return null;
		else if(searchString.equals(node.getValue()))		//first
			return node.getRef();
		else if(searchString.compareTo(node.getValue()) < 0)		//less
			return search(node.getLeft(), searchString);
		else														//greater
			return search(node.getRight(), searchString);
	}

	//delets removed node if a leaf
	public void deleteLeaf(TreeNode p, TreeNode temp)
	{
		if(p == temp)		// one-node tree and leaf is also root
		{
			root = null;
		}
		else					// multi-node tree with regular leaf
		{
			if(temp.getLeft() == p)
				temp.setLeft(null);
			else
				temp.setRight(null);
		}
	}

	//delets removed node if has one child
	public void deleteParent1(TreeNode p, TreeNode temp )
	{
		if(p == temp)		// must delete root with one child
		{
			if(p.getLeft() == null)
				root = root.getRight();
			else
				root = root.getLeft();
		}
		else
		{
			if(temp.getLeft() == p)
				if(p.getLeft() == null)
					temp.setLeft(p.getRight());
				else
					temp.setLeft(p.getLeft());
			else
				if(p.getLeft() == null)
					temp.setRight(p.getRight());
				else
					temp.setRight(p.getLeft());
		}
	}

	//deletes removed node if has two children
	public void deleteParent2(TreeNode p)
	{
		TreeNode temp1 = p.getLeft();
		TreeNode temp2 = p;
		while (temp1.getRight() != null)
		{
			temp2 = temp1;
			temp1 = temp1.getRight();
		}
		p.setValue(temp1.getValue());
		if(p == temp2)
			temp2.setLeft(temp1.getLeft());
		else
			temp2.setRight(temp1.getLeft());
	}
}

//user-defined object
class TreeNode
{
	//attributes
	private String value;
	private ArrayList<Item> ref;
	private TreeNode left;
	private TreeNode right;

	//constructor
	public TreeNode(String initValue, Item initRef, TreeNode initLeft, TreeNode initRight)
	{
		value = initValue;
		ref = new ArrayList<Item>();
		if(initRef != null)
			ref.add(initRef);
		left = initLeft;
		right = initRight;
	}

	//access and set attributes
	public String getValue(){return value;}
	public ArrayList<Item> getRef(){return ref;}
	public TreeNode getLeft(){return left;}
	public TreeNode getRight(){return right;}
	public void setValue(String theNewValue){value = theNewValue;}
	public void setRef(Item theNewRef){ref.add(theNewRef);}
	public void removeRef(Item removedRef)
	{
		for(int x = 0; x < ref.size(); x++)
		{
			if(ref.get(x).getBarcode() == removedRef.getBarcode())
			{
				ref.remove(x);
			}
		}
	}
	public void setLeft(TreeNode theNewLeft){left = theNewLeft;}
	public void setRight(TreeNode theNewRight){right = theNewRight;}
}