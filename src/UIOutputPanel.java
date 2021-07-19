/**
 * @(#)UIOutputPanel.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/5/7
 */


import java.awt.Dimension;
import java.awt.Font;

import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class UIOutputPanel extends JPanel  {


	private JTextArea infoArea;
//	private JTextPane printArea;
	private JScrollPane scrollArea;

	private int width, height;

    public UIOutputPanel() {
    	super();

    	this.width = 1680;
    	this.height= 1050;

    	this.setPreferredSize(new Dimension(width,height));

    	infoArea = new JTextArea();
        infoArea.setEditable(false);



        scrollArea = new JScrollPane(infoArea);
		scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		//scrollArea.setSize(new Dimension((int)(getWidth()), (int)(getHeight())));
		scrollArea.setMinimumSize(new Dimension(700, 700));//hint at size
        scrollArea.setPreferredSize(new Dimension(width-50, height-50));//hint at size

		Font font = new Font("Monospaced", Font.BOLD, 13);
		scrollArea.getViewport().getView().setFont(font);

		add(scrollArea);

    }

    public void msg(String message) {
        displayMessage(message);
    }

    public void line(String message) {
        displayMessageAddToLine( message);
    }

    public void displayMessage(String message) {
        infoArea.append("\n"+" "+message);
        toVis();
    }

    public void displayMessageAddToLine(String message) {
        infoArea.append(message);
        toVis();
    }

    private void toVis() {
    	Rectangle visible = infoArea.getVisibleRect();
	        visible.y = 0;
	        infoArea.scrollRectToVisible(visible);
	    }
}