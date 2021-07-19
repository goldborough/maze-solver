/**
 * @(#)BaseUI.java
 * @author Michael Goldborough
 * @version
 */
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import java.awt.Dimension;
import java.awt.GridLayout;


import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//import java.io.*;
import java.util.Scanner;

import java.text.DecimalFormat;

public class BaseUI extends JFrame {

	private Scanner scan = new Scanner(System.in);
	private char charArray[] = new char[125];

	private JLabel fieldCurrentTitle;

	private JButton readMapButton, showMapViewButton, findPathButton, exitButton;

	private ReadMapButtonHandler readMapButtonHandler;
	private ShowMapViewButtonHandler m_showMapViewButtonHandler;
	private ExitButtonHandler exitButtonHandler;

	private MazeModel m_mazeModel;
	private MapReader m_mapReader;

	UIOutputPanel m_outputPanel = null;


	public BaseUI() {
		super("Maze Application by Michael Goldborough");
		JPanel northPanel =createButtonSection();
		JPanel southPanel =createContentsSection();


		 this.setLayout(new BorderLayout());

		add(northPanel,BorderLayout.NORTH);
		add(southPanel,BorderLayout.SOUTH);

		setSize(1920,1400);
		pack();//allows manager (to manage better)
		setResizable(true);
		setLocationRelativeTo(null);//centers frame
		//setDefaultLookAndFeelDecorated(true);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Use and change later for 'save function' to occur

		m_mazeModel = new MazeModel();
		m_mapReader = new MapReader(m_mazeModel, m_outputPanel);//must init only after call to createDisplaySection() as it needs infoArea

		pack();
		setVisible(true);
		//setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

		m_outputPanel.displayMessage("Michael Goldorough\n a map program\n\n Begin by selecting Read Maze file above.\n\nYellow are walkable.\nGreen are tiles searched to find the destination.\nBlue is the path.\n\n\n");
		ActionEvent e=null;
		readMapButtonHandler.actionPerformed(e);

	}

	private JPanel createContentsSection(){
		fieldCurrentTitle = new JLabel("Map Text View");

		//fieldCurrentTitle

		m_outputPanel= new UIOutputPanel();

		JPanel miniLeft = new JPanel();
		miniLeft.setLayout(new GridLayout(30, 1));
		miniLeft.add(new JButton(""));
		miniLeft.setSize(40,1300);
		miniLeft.setVisible(false);

		JPanel allContent = new JPanel();
		allContent.setLayout(new BorderLayout());
		allContent.add(miniLeft,BorderLayout.WEST);
		allContent.add(m_outputPanel,BorderLayout.EAST);
		allContent.setBorder(new EmptyBorder(10, 10, 10, 10));

		return allContent;
	}


	private JPanel createButtonSection(){
		readMapButton = new JButton("Read Maze File");
		readMapButton.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(),new EmptyBorder(10, 10, 10, 10)));

		showMapViewButton = new JButton("Start Path Render UI");
		showMapViewButton.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(),new EmptyBorder(10, 10, 10, 10)));

		exitButton = new JButton("Exit");
		exitButton.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(),new EmptyBorder(10, 10, 10, 10)));

		readMapButtonHandler = new ReadMapButtonHandler();
		m_showMapViewButtonHandler = new ShowMapViewButtonHandler();
		exitButtonHandler = new ExitButtonHandler();


		readMapButton.addActionListener(readMapButtonHandler);
		showMapViewButton.addActionListener(m_showMapViewButtonHandler);
		exitButton.addActionListener(exitButtonHandler);

		JPanel thePane = new JPanel();
		FlowLayout f= new FlowLayout(FlowLayout.LEFT, 10, 3);
		thePane.setLayout(f);
		thePane.add(readMapButton);
				thePane.add(showMapViewButton);

		thePane.add(exitButton);

		thePane.setPreferredSize(new Dimension(1680,60));
		thePane.setBorder(new EmptyBorder(10, 10, 10, 10));
		return thePane;
    }

	public void displayMessage(String message) {
		if (m_outputPanel!=null ) m_outputPanel.displayMessage(message);
	}

	private class ReadMapButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			m_mazeModel.init();
			ReadMapFrame readMapFrame =new ReadMapFrame(m_mapReader);
		}
	}

	private class ShowMapViewButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){

			if  (m_mazeModel.getState() == MazeInitState.maze_initialised)
			{
				UIMazeViewFrame uiMazeViewFrame = new UIMazeViewFrame(m_mazeModel,m_outputPanel);

			}
			else JOptionPane.showMessageDialog(null,"Please read a map first");
		}
	}

	private class ExitButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			dispose();
			System.exit(0);
		}
	}
}