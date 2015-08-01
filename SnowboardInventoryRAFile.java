/**
 *SnowboardInventoryRAFile.java
 *
 *Myanna Harris
 *3-25-13
 *Kennewick High School
 *
 *Java
 *Keep Snowboard Shop's inventory records
 */

//contains all file-oriented classes and methods
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class SnowboardInventoryRAFile
{
	final static int NameLength = 36;
	final static int RecordSize = (NameLength*2)*6 + 12; //constant used to calculate SEEK value

	//Writes one record into ranFile, which must already be open
	//IOExceptions are detected and reported
	public static void writeToFile(RandomAccessFile ranFile, long recordNum, Item recordedItem)
	{
		try
		{
			ranFile.seek(recordNum * RecordSize);
			ranFile.writeChars(setLength(recordedItem.getBrand(), NameLength));
			ranFile.writeChars(setLength(recordedItem.getType(), NameLength));
			ranFile.writeChars(setLength(recordedItem.getBarcode().toString(), NameLength));
			ranFile.writeChars(setLength(recordedItem.getSize(), NameLength));
			ranFile.writeDouble(recordedItem.getPrice());
			ranFile.writeChars(setLength(recordedItem.getRentalSale(), NameLength));
			ranFile.writeChars(setLength(recordedItem.getStatus(), NameLength));
			ranFile.writeInt(recordedItem.getNum());
		}
		catch(IOException exc)
		{
			JOptionPane.showMessageDialog(null,"Error: While writing " + exc.toString());
		}
	}

	public static void writeStatus(RandomAccessFile ranFile, long recordNum, String newStatus)
	{
		try
		{
			ranFile.seek((recordNum * RecordSize) + (NameLength*2)*5 + 8);
			ranFile.writeChars(setLength(newStatus, NameLength));
		}
		catch(IOException exc)
		{
			JOptionPane.showMessageDialog(null,"Error: While writing " + exc.toString());
		}
	}

	//Forces length of string to a specific value
	//Necessary before writing into a random-access file
	static String setLength(String s, int len)
	{
		StringBuffer sb = new StringBuffer(s);
		sb.setLength(len);
		return sb.toString();
	}

	//Reads 1 record from ranFile, which must already be open
	//Reads each field - use TRIM to remove padding spaces
	//IOExceptions are detected and reported
	public static Item readFromFile(RandomAccessFile ranFile, long recordNum)
	{
		try
		{
			ranFile.seek(recordNum * RecordSize);
			StringBuffer nameBuffer = new StringBuffer(NameLength);
			nameBuffer.setLength(NameLength);

			Item temp = new Item();

			for(int c = 0; c < NameLength; c++)
			{
				nameBuffer.setCharAt(c, ranFile.readChar());
			}
			temp.setBrand(nameBuffer.toString().trim());

			if(!temp.getBrand().equals("Skip"))
			{
				for(int c = 0; c < NameLength; c++)
				{
					nameBuffer.setCharAt(c, ranFile.readChar());
				}
				temp.setType(nameBuffer.toString().trim());

				for(int c = 0; c < NameLength; c++)
				{
					nameBuffer.setCharAt(c, ranFile.readChar());
				}
				temp.setBarcode(UUID.fromString(nameBuffer.toString().trim()));

				for(int c = 0; c < NameLength; c++)
				{
					nameBuffer.setCharAt(c, ranFile.readChar());
				}
				temp.setSize(nameBuffer.toString().trim());

				temp.setPrice(ranFile.readDouble());

				for(int c = 0; c < NameLength; c++)
				{
					nameBuffer.setCharAt(c, ranFile.readChar());
				}
				temp.setRentalSale(nameBuffer.toString().trim());

				for(int c = 0; c < NameLength; c++)
				{
					nameBuffer.setCharAt(c, ranFile.readChar());
				}
				temp.setStatus(nameBuffer.toString().trim());

				temp.setNum(ranFile.readInt());
			}

			return temp;
		}
		catch(IOException exc)
		{
			JOptionPane.showMessageDialog(null,"Error: While reading record # " + recordNum + "\n" + exc.toString());
			return null;
		}
	}

	public static void deleteFromFile(RandomAccessFile ranFile, long recordNum)
	{
		try
		{
			ranFile.seek(recordNum * RecordSize);
			ranFile.writeChars(setLength("Skip", NameLength));
		}
		catch(IOException exc)
		{
			JOptionPane.showMessageDialog(null,"Error: While deleting record # " + recordNum + "\n" + exc.toString());
		}
	}

	//Reads all records from ranFile and prints the fields
	static ArrayList<Item> display(RandomAccessFile ranFile)
	{
		try
		{
			ArrayList<Item> ItemList = new ArrayList<Item>();
			long recordCount = ranFile.length() / RecordSize;

			for(int c = 1; c < recordCount; c++)
			{
				Item temp = readFromFile(ranFile, c);
				if(!temp.getBrand().equals("Skip"))
				{
					ItemList.add(temp);
				}
			}

			return ItemList;
		}
		catch(IOException exc)
		{
			JOptionPane.showMessageDialog(null,"Error: When displaying inventory " + exc.toString());
			return null;
		}
	}
}