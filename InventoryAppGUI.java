/**
 *InventoryAppGUI.java
 *
 *Myanna Harris
 *3-25-13
 *Kennewick High School
 *
 *Java
 *Keep Snowboard Shop's inventory records
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.UUID;

public class InventoryAppGUI extends JFrame
{
	private static Inventory mainInventory;

	private Container contents;
	private JLabel soldLabel;
	private JTextField inputTextField;
	private JButton soldButton, defectiveButton, searchButton, buyButton, rentButton, returnButton;
	private JComboBox searchComboBox;
	private JTextArea outputTextArea;
	private String[] searchOptions = {"None","Barcode","Brand","Type", "All", "Records"};
	private String searchType = "";

	//Constructor
	public InventoryAppGUI()
	{
		//call JFrame constructor with text for title bar
		super("Snowboard Shop Inventory App");

		//get container for components
		contents = getContentPane();

		//set layout manager
		contents.setLayout(new FlowLayout());

		//instantiate GUI components and other instance variables
			//use the JLabel constructor with a String argument
		soldLabel = new JLabel("Enter information of merchandise: ");
			//instantiate text field
		inputTextField = new JTextField(36);
			//instantiate buttons
		soldButton = new JButton("Sold");
		defectiveButton = new JButton("Defective");
		searchButton = new JButton("Search");
			//instantiate combobox
		searchComboBox = new JComboBox(searchOptions);
		searchComboBox.setMaximumRowCount(7);
			//instantiate buttons
		buyButton = new JButton("Buy");
		rentButton = new JButton("Rent");
		returnButton = new JButton("Return");
			//instantiate text area
		outputTextArea = new JTextArea();
		outputTextArea.setEditable(false);

		//add GUI components to content frame
		contents.add(soldLabel);
		contents.add(inputTextField);
		contents.add(soldButton);
		contents.add(defectiveButton);
		contents.add(searchButton);
		contents.add(searchComboBox);
		contents.add(buyButton);
		contents.add(rentButton);
		contents.add(returnButton);
		contents.add(outputTextArea);

		//declare and instantiate event handler objects
		ButtonHandler bh = new ButtonHandler();
		ItemListenerHandler ilh = new ItemListenerHandler();

		//register event handlers on components
		soldButton.addActionListener(bh);
		defectiveButton.addActionListener(bh);
		searchButton.addActionListener(bh);
		buyButton.addActionListener(bh);
		rentButton.addActionListener(bh);
		returnButton.addActionListener(bh);
		searchComboBox.addItemListener(ilh);

		//set original size of window
		setSize(600, 400);

		//make window visible
		setVisible(true);
	}

	public static void main(String[] args)
	{
		try
		{
			mainInventory = new Inventory();
			InventoryAppGUI appGUI = new InventoryAppGUI();
			appGUI.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent evt)
					{
						mainInventory.close();
					}
				});
		}
		catch(IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error occured when opening application");
		}
	}

	//handles when a button is clicked
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				if(ae.getSource() == soldButton)
				{
					if(inputTextField.getText().trim().length() != 0 && mainInventory.getHashMap().get(UUID.fromString(inputTextField.getText())) != null
						&& mainInventory.getHashMap().get(UUID.fromString(inputTextField.getText())).getRentalSale().equals("S"))
					{
						mainInventory.removeFromInventory(UUID.fromString(inputTextField.getText()), "Sold");
						JOptionPane.showMessageDialog(null,"Item removed from inventory");
					}
					else
						JOptionPane.showMessageDialog(null,"Incorrect input");
				}
				else if(ae.getSource() == defectiveButton)
				{
					if(inputTextField.getText().trim().length() != 0 && mainInventory.getHashMap().get(UUID.fromString(inputTextField.getText())) != null)
					{
						mainInventory.removeFromInventory(UUID.fromString(inputTextField.getText()), "Defective");
						JOptionPane.showMessageDialog(null,"Item removed from inventory");
					}
					else
						JOptionPane.showMessageDialog(null,"Incorrect input");
				}
				else if(ae.getSource() == searchButton)
				{
					if(inputTextField.getText().trim().length() != 0)
					{
						if(searchType.equals("Barcode"))
						{
							if(inputTextField.getText().length() == 36)
								outputTextArea.setText(mainInventory.byBarcode(UUID.fromString(inputTextField.getText())));
							else
								JOptionPane.showMessageDialog(null,"Incorrect input");
						}

						else if(searchType.equals("Brand"))
							outputTextArea.setText(mainInventory.byBrand(inputTextField.getText()));
						else if(searchType.equals("Type"))
							outputTextArea.setText(mainInventory.byType(inputTextField.getText()));
						else
							JOptionPane.showMessageDialog(null,"Incorrect input");
					}
					else if(searchType.equals("All"))
						outputTextArea.setText(mainInventory.all());
					else if(searchType.equals("Records"))
						outputTextArea.setText(mainInventory.records());
					else
						JOptionPane.showMessageDialog(null,"Incorrect input");
				}
				else if(ae.getSource() == buyButton)
				{
					mainInventory.incNumFiles();
					mainInventory.addToInventory(new Item(JOptionPane.showInputDialog(null, "Enter the BRAND (Ex: Burton) of the new Item"),
							JOptionPane.showInputDialog(null, "Enter the TYPE (Ex: Boots) of the new Item"),
							JOptionPane.showInputDialog(null, "Enter the SIZE of the new Item"),
							Double.parseDouble(JOptionPane.showInputDialog(null, "Enter the PRICE (Ex: 00.00) of the new Item")),
							JOptionPane.showInputDialog(null, "Enter the RENTALSALE (R or S) of the new Item"),
							JOptionPane.showInputDialog(null, "Enter the STATUS (Ex: Available) of the new Item"), mainInventory.getNumFiles()));
					JOptionPane.showMessageDialog(null,"Item added to inventory");
				}
				else if(ae.getSource() == rentButton)
				{
					if(mainInventory.getHashMap().get(UUID.fromString(inputTextField.getText())).getRentalSale().equals("R"))
					{
						mainInventory.setFlag(mainInventory.getHashMap().get(UUID.fromString(inputTextField.getText())));
					}

					else
						JOptionPane.showMessageDialog(null,"Item not rentable");
				}
				else if(ae.getSource() == returnButton)
				{
					mainInventory.removeFlag(mainInventory.getHashMap().get(UUID.fromString(inputTextField.getText())));
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Error occured when doing action corresponding with button click");
			}
		}
	}

	//handles when a combobox option is chosen
	private class ItemListenerHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent ie)
		{
			int index = searchComboBox.getSelectedIndex();
			searchType = searchOptions[index];
		}
	}
}