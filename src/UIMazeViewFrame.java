/**
 * @(#)UIMazeViewFrame.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/5/5
 */

 import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class UIMazeViewFrame extends JFrame
{
	private UIMazeViewPanel m_uiMazeViewPanel;

	public UIMazeViewFrame(MazeModel mazeModel, UIOutputPanel out)
	{
		super("Happy");
		m_uiMazeViewPanel = new UIMazeViewPanel();
		m_uiMazeViewPanel.init(mazeModel, out);

		JScrollPane scrollFrame = new JScrollPane(m_uiMazeViewPanel);
		m_uiMazeViewPanel.setAutoscrolls(true);

		int dimensionWidth = m_uiMazeViewPanel.getPreferredSize().width+10;
		int dimensionHeight = m_uiMazeViewPanel.getPreferredSize().height+15;
		if (dimensionWidth>1920)dimensionWidth=1920;
		if (dimensionHeight>1200)dimensionHeight=1200;

		scrollFrame.setPreferredSize(new Dimension(dimensionWidth,dimensionHeight));
		this.add(scrollFrame);

		setVisible(true);
		pack();
	}

    public UIMazeViewPanel getUIMazeViewPanel()
    {
    	return m_uiMazeViewPanel;
    }
}