/**
 * @(#)MapReader.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/4/26
 */

import java.awt.Dimension;
import java.awt.Rectangle;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;


import javax.swing.JTextArea;

public class MapReader {
	private MazeModel mr_mazeModel;
	private UIOutputPanel mr_outputPanel;

	private Scanner m_scan = new Scanner(System.in);

	public boolean isDiagonalAllowed=false;

    public MapReader() {
    }

    public MapReader(MazeModel mazeModel, UIOutputPanel outputPanel) {
    	mr_mazeModel = mazeModel;
    	mr_outputPanel = outputPanel;
    }

    public boolean accessFile(String path)
    {
    	displayMessage("Attempting to access file: "+path + "\n");

    	FileInputStream inStream = null;
        Scanner inFile = null;
		String stringLine = "";
		int lengthFirstLine;

		try
		{
			boolean isStreamOn=false;
			inStream=new FileInputStream(path);
			inFile=new Scanner(inStream);

			stringLine = inFile.nextLine();

			if ( stringLine.length() <= 0 ) isStreamOn=false;
			else isStreamOn=true;

			inStream.close();

			return isStreamOn;
		}
		catch (FileNotFoundException ex)
		{
			System.err.println(ex);
			displayMessage("A File was not found with this path.");
			return false;
		}
		catch(IOException ex)
		{
			System.err.println(ex);
			return false;
		}
    }

	public String nextLine(Scanner inFile)
	{
		String sNextLine = "";

		try{sNextLine = inFile.nextLine();}
		catch (NoSuchElementException ex)
		{
			System.err.println(ex);
			displayMessage("There was no next line in this file.");
			return null;
		}
		//displayMessage("next | "+sNextLine);

		return sNextLine;
	}

    public boolean validateFile(String path, boolean isWriteRun)
    {
    	Boolean isVerbose = true;

		Boolean sucessfulRead=false;
		String fileName = null;
        String oneLine;
        FileInputStream inStream = null;
        Scanner inFile = null;
        StringTokenizer stringTokenizer = null;
		int ii=0,jj=0,kk=0,ll=0,mm=0,nn=0;

		if (isWriteRun) mr_mazeModel.init();

        //checks if a file name is provided
        try {

        	inStream=new FileInputStream(path);
			inFile=new Scanner(inStream);

			String sNextLine = "";

			//Read first line of integers
			try
			{
				//line one ----------
				if ((sNextLine = nextLine(inFile)) == null) {return false;}
				else { stringTokenizer = new StringTokenizer(sNextLine);}


				if (stringTokenizer.hasMoreTokens())
				{ ii = Integer.parseInt(stringTokenizer.nextToken());}//number of collumns, x-coord
				else { displayMessage("ii"); return false;}

				if ( stringTokenizer.hasMoreTokens())
				{ jj = Integer.parseInt(stringTokenizer.nextToken()); }//number of rows, y-coord
				else { displayMessage("jj"); return false;}


				//line two ----------
				if ((sNextLine = nextLine(inFile)) == null) {return false;}
				else { stringTokenizer = new StringTokenizer(sNextLine);}


				if (stringTokenizer.hasMoreTokens())
				{ kk = Integer.parseInt(stringTokenizer.nextToken());}
				else { displayMessage("kk"); return false;}

				if ( stringTokenizer.hasMoreTokens())
				{ ll = Integer.parseInt(stringTokenizer.nextToken()); }
				else { displayMessage("ll"); return false;}


				//line three ----------
				if ((sNextLine = nextLine(inFile)) == null) {return false;}
				else { stringTokenizer = new StringTokenizer(sNextLine);}


				if (stringTokenizer.hasMoreTokens())
				{ mm = Integer.parseInt(stringTokenizer.nextToken());}
				else { displayMessage("mm"); return false;}

				if ( stringTokenizer.hasMoreTokens())
				{ nn = Integer.parseInt(stringTokenizer.nextToken()); }
				else { displayMessage("nn"); return false;}

			}
			catch (NumberFormatException ex)
			{
				System.err.println(ex);
				return false;
			}

			NodeModel newNode = null;

			Vector<Vector<NodeModel>> rows;
			Vector<NodeModel> nodes;

			try
			{
				rows = mr_mazeModel.getMaze();
				nodes = mr_mazeModel.getNodeList();

			}
			catch (NullPointerException ex){ System.err.println(ex);displayMessage("VERY NULL\n");return false;}



			if (isWriteRun) displayMessage("# begin file read\n");
			else displayMessage("# begin validation");

			if (isVerbose) displayMessage("vars: "+ii+" "+jj+" "+kk+" "+ll+" "+mm+" "+nn+"\n");



	        for (int rowCount=0; rowCount < jj; rowCount++)
	        {
	        	if ((sNextLine = nextLine(inFile)) == null) {return false;}
	        	else { stringTokenizer = new StringTokenizer(sNextLine);}

	        	//if (sNextLine.length() != jj) {displayMessage("unexpected string length in map");return false;}

	        	Vector<NodeModel> row = new Vector<NodeModel>();

	        	if (isWriteRun) row.setSize(ii);

				for (int collumnCount=0; collumnCount < ii;collumnCount++)
	        	{
	        		newNode = null;

					int i = Integer.parseInt(stringTokenizer.nextToken());
					if ( i<0 || i>1) {displayMessage("map data contained an invalid integer."); return false;}

		        	if (isWriteRun){
		        		 newNode = new NodeModel(i,collumnCount,rowCount);
		        		 row.setElementAt(newNode,collumnCount);
		        		 nodes.add(newNode);
		        	}
		        	if (isVerbose) displayMessageAddToLine(Integer.toString(i)+" ");
	        	}
	        	displayMessageAddToLine("\n");
	        	if (isWriteRun) rows.add(row);
	        }

	        if (isWriteRun) displayMessage("# file read appears to be complete\n");
	        else displayMessage("# completed validation");


			if (isWriteRun)
			{
				mr_mazeModel.m_xSize = ii;
				mr_mazeModel.m_ySize = jj;
				mr_mazeModel.mr_origin = rows.get(ll).get(kk);
				mr_mazeModel.mr_destination = rows.get(nn).get(mm);
			}

			Vector<NodeModel> thisRow = null;


			if (isVerbose)
			{

				if (isWriteRun) displayMessage("# begin read back from Vector\n");

		        for (int rowCount=0; rowCount < jj; rowCount++)
		        {
					if (isWriteRun) thisRow = rows.get(rowCount);
					for (int collumnCount=0; collumnCount < ii;collumnCount++)
		        	{
			        	if (isWriteRun)
			        	{
			        		if (thisRow.get(collumnCount).identity == 1 )
			        		displayMessageAddToLine("O ");
			        		else displayMessageAddToLine("  ");
			        	};
		        	}
		        	if (isWriteRun) displayMessageAddToLine("\n");
		        }

		       if (isWriteRun) displayMessage("# completed read back from Vector");
			}

			mr_mazeModel.setState(MazeInitState.maze_initialised);

			sucessfulRead = true;
        }
		catch(FileNotFoundException ex) { System.err.println(ex); }

 		return sucessfulRead;
    }


    public boolean readInText(String path)
    {
    	Boolean sucessfulRead=false;

    	//We will first run through the validation logic in our read code
    	//Secondly we will write with that same code to our MazeModel object

		if (!validateFile(path, false))
		{
			//validate a map but dont write it to our MazeModel.
			displayMessage("Result: Validation failed");
			return false;
		}
		else
		{
			//validation did not fail run function with write switch on

			if (!validateFile(path, true) && false )
			{
				displayMessage("Result: Write failed");
				return (sucessfulRead=false);
			}
			else
			{
				//Lets just access our MapModel ref to generate our graph at this point
				displayMessage("\nStart graph");
				mr_mazeModel.buildGraphForManhattanSystem(true, isDiagonalAllowed, 1, 0 );//Toroidal
				displayMessage("\nEnd graph");

				sucessfulRead=true;
			}
		}

 		return sucessfulRead;
    }

    public boolean displayMessage(String message)
    {
		mr_outputPanel.displayMessage(message);
		return true;
    }

    public boolean displayMessageAddToLine(String message)
    {
		mr_outputPanel.displayMessageAddToLine(message);
		return true;
    }

    public String getLastError()
    {
    	return "The Error Message";
    }

}