/**
 * @(#)ReadMapFrame.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/4/29
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Cursor;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class ReadMapFrame extends JFrame
{
	private ReadMapHandler m_readMapButtonHandler;
	private CancelButtonHandler m_cancelButtonHandler;

	private EnterPathHandler m_enterPathHandler;
	private SelectPathHandler m_selectPathHandler;

	private JButton m_readMapButton, m_cancelButton;
	private JRadioButton m_selectPathOption, m_enterPathOption;

	private JCheckBox m_checkboxDiagonal;

	private ArrayList<String> m_SampleStrings;
	private JComboBox m_pathCombo;

	private JTextField m_pathField;
	private JLabel m_instructionLabel, m_expandedInfoLabel;
	private MapReader mr_mapReader;

		//private JPanel instructionPanel;
		private JPanel radioPanel;
		JPanel inputPanel;
		private JPanel extendedInstructionPanel;
		private JPanel buttonPanel;


    public ReadMapFrame(MapReader mapReader)
	{
    	super("Open Map File");

    	mr_mapReader = mapReader;

		//instructionPanel=createInstructionPanel();
		radioPanel=createRadioPanel();
		inputPanel=createInputPanel();
		extendedInstructionPanel=createExtendedInstructionPanel();
		buttonPanel=createButtonPanel();

		setLayout(new GridLayout(5,1));
	    this.add(radioPanel);
		//this.add(instructionPanel);
	    this.add(inputPanel);
	    this.add(buttonPanel);
	    this.add(extendedInstructionPanel);

		setSize(500,325);
		setResizable(false);
		setLocationRelativeTo(null);//centers frame

		m_enterPathOption.setSelected(false);
		m_selectPathOption.setSelected(true);

		m_pathField.setVisible(false);
		m_pathCombo.setVisible(true);

		extendedInstructionPanel.setVisible(false);
		setVisible(true);
    	m_pathCombo.setSelectedIndex(2);
	}

	public void WAIT()
	{
		super.setCursor(WAIT_CURSOR);
	}

	public void FINISHED()
	{
		super.setCursor(DEFAULT_CURSOR);
	}

	private JPanel createInstructionPanel()
	{
		m_instructionLabel=new JLabel("Enter your map file name.");
		JPanel panel =new JPanel();
		panel.add(m_instructionLabel);
    	return panel;
    }

    private JPanel createRadioPanel()
	{
		m_selectPathOption = new JRadioButton("Select a Path");
		m_enterPathOption = new JRadioButton("Enter a path");

		m_enterPathHandler = new EnterPathHandler();
		m_selectPathHandler = new SelectPathHandler();

		m_selectPathOption.addActionListener(m_selectPathHandler);
		m_enterPathOption.addActionListener(m_enterPathHandler);

		ButtonGroup pathChoice = new ButtonGroup();
		pathChoice.add(m_selectPathOption);
		pathChoice.add(m_enterPathOption);

		m_checkboxDiagonal = new JCheckBox("Allow diagonal neighbours");
		m_checkboxDiagonal.setSelected(false);

		JPanel panel=new JPanel();
		panel.add(m_selectPathOption);
		panel.add(m_enterPathOption);
		panel.add(m_checkboxDiagonal);
		return panel;
    }

	private JPanel createInputPanel()
	{
		m_pathField=new JTextField(30);
    	JPanel panel=new JPanel();
    	panel.add(m_pathField);
    	m_pathField.setText("Samples/input.txt");

		m_SampleStrings = new ArrayList<String>();
		m_SampleStrings.add("Samples/input.txt");
		m_SampleStrings.add("Samples/small_input.txt");
		m_SampleStrings.add("Samples/medium_input.txt");
		m_SampleStrings.add("Samples/large_input.txt");
		m_SampleStrings.add("Samples/small_wrap_input.txt");
		m_SampleStrings.add("Samples/sparse_large.txt");
		m_SampleStrings.add("Samples/sparse_medium.txt");

    	m_pathCombo=new JComboBox();

		for (int i = 0; i < m_SampleStrings.size(); i++)
		{
			String current = m_SampleStrings.get(i);
			m_pathCombo.addItem("> "+(i+1)+": "+ current);
		}

    	panel.add(m_pathCombo);

		return panel;
    }

	private JPanel createExtendedInstructionPanel()
	{
		m_expandedInfoLabel=new JLabel("You may have to wait while a graph of the maze is produced.");
		JPanel panel =new JPanel();
		panel.add(m_expandedInfoLabel);
    	return panel;
    }

	private JPanel createButtonPanel(){
		m_readMapButton=new JButton("Read Map");
		m_cancelButton=new JButton("Cancel");

		m_readMapButtonHandler = new ReadMapHandler();
		m_cancelButtonHandler= new CancelButtonHandler();

		m_readMapButton.addActionListener(m_readMapButtonHandler);
		m_cancelButton.addActionListener(m_cancelButtonHandler);

		JPanel panel=new JPanel();
		panel.add(m_readMapButton);
		panel.add(m_cancelButton);
		return panel;
    }

    private class ReadMapHandler implements ActionListener
	{
		private boolean isTrue;

		public void actionPerformed(ActionEvent event){
			boolean fileAccesable = false;
			boolean isValidMap = false;

			extendedInstructionPanel.setVisible(true);

			String thePath;

			if (m_enterPathOption.isSelected())
			{
				thePath = m_pathField.getText();
			}
			else {//m_enterPathOption.isSelected

				thePath = m_SampleStrings.get(m_pathCombo.getSelectedIndex());
				//System.out.println(m_pathCombo.getSelectedIndex());

			};

			mr_mapReader.isDiagonalAllowed = m_checkboxDiagonal.isSelected();

			try
			{
				fileAccesable =  mr_mapReader.accessFile(thePath);
			}
			catch (NullPointerException e)
			{
				JOptionPane.showMessageDialog(null,"Sorry. Map result was is not accessable.");
			}

			try
			{
				WAIT();
				isValidMap = mr_mapReader.readInText(thePath);
				FINISHED();
			}
			catch (NullPointerException e)
			{
				JOptionPane.showMessageDialog(null,"Map validation failed.");
			}


			if (fileAccesable)
			{
				m_cancelButtonHandler.invisible();

				JOptionPane.showMessageDialog(null,"File is a valid map. It has been selected. \n\n The next step is open the Path Render UI to begin pathfinding.");
			}
			else
			{
				m_cancelButtonHandler.invisible();
				JOptionPane.showMessageDialog(null,mr_mapReader.getLastError());
			}
		}
	}
	private class  CancelButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			invisible();
		}

		public void invisible()
		{
			setVisible(false);//is this a good idea? Seems to meet requirements
		}
	}

	private class EnterPathHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			m_pathField.setVisible(true);
			m_pathCombo.setVisible(false);
		}
	}

	private class SelectPathHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			m_pathField.setVisible(false);
			m_pathCombo.setVisible(true);
		}
	}
}