/**
 * @(#)UIMazeView.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/5/5
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
//import java.awt.geom.Ellipse2D;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.Timer;

public class UIMazeViewPanel extends JPanel implements Scrollable
{
	public static final long serialVersionUID = 1L;
	public static final float MY_FPS = 60.0f;
	private AstarPathfinder m_pathfinder;

	// path print vars
	ArrayList<NodeLocalTree> closedList;
	NodeLocalTree start;
	Vector<Vector<NodeLocalTree>> structuredGraph;
	NodeModel nextNode = null;
	NodeLocalTree nextGraph = null;
	private int scaledX = 5, scaledY = 5;
	private int width = 1920, height = 1200;
	private MazeModel mr_mazeModel = null;
	UIOutputPanel out;

	// Color m_grey = new Color(255, 127, 127, 255);

	// private float m_deltaTime;
	private int m_frameTime;
	private final Timer m_gameTimer;
	private ActionListener m_timerHandler = new ActionListener()
	{
		public void actionPerformed(ActionEvent theEvent)
		{
			render();
		}
	};

	public UIMazeViewPanel()
	{
		super();

		this.width = 1920;
		this.height = 1200;

		this.setPreferredSize(new Dimension(width, height));
		this.setBackground(Color.WHITE); // black for testing, white for live.

		m_timerHandler = new TimerHandler();
		// m_deltaTime = 1000.0f/MY_FPS; // for now. this is 1 second.
		m_gameTimer = new Timer(m_frameTime, m_timerHandler);
		m_gameTimer.start(); // starts the timer.
	}

	public void init(MazeModel mazeModel, UIOutputPanel o)
	{
		out = o;
		m_pathfinder = new AstarPathfinder();
		m_pathfinder.init(mazeModel.getNodeList(), mazeModel.getRelationships(), mazeModel.getStructuredGraph(), o);
		setMazeModel(mazeModel);
		this.setVisible(true);

		// print out linked list we have been leaving in our closed list
		m_pathfinder.GeneratePath(mazeModel.mr_origin, mazeModel.mr_destination);
		closedList = m_pathfinder.GetClosedList();
		// NodeLocalTree start;

		out.msg("print list. Closed list size: " + closedList.size());

		if (closedList.size() == 0) return;
		else start = closedList.get(0);

		structuredGraph = m_pathfinder.GetStructuredGraph();
		nextNode = null;
		nextGraph = null;

		int count = 0;

		if (closedList.get(0).m_child != null)
		{
			nextNode = closedList.get(0).m_child;
			out.msg("" + count + "/" + nextNode.x + "," + nextNode.g + "/" + Math.round(nextNode.f) + "="
					+ Math.round(nextNode.g) + "+" + Math.round(nextNode.h));
			count++;
		} else
		{
			out.msg("render path: close list child null EXIT");
			return;
		}

		while (nextNode != null)
		{
			nextGraph = structuredGraph.get(nextNode.y).get(nextNode.x);
			nextNode = nextGraph.m_child;

			if (nextNode != null) {
				out.msg("" + count + "/" + nextNode.x + "," + nextNode.g + "/" + Math.round(nextNode.f) + "="
						+ Math.round(nextNode.g) + "+" + Math.round(nextNode.h));
			}

			count += 1;

			if (count > 20000)
			{
				out.msg("render path: max while loop EXIT");

			}
		}
		out.msg("done list");
	}

	public void paintComponent(Graphics g)
	{

		super.paintComponent(g);

		// draw rect width

		Iterator<NodeModel> itr = mr_mazeModel.getNodeList().iterator();

		while (itr.hasNext()) {
			NodeModel n = itr.next();
			g.setColor(Color.YELLOW);
			if (n.identity == 0)
				g.fillRect(n.x * scaledX, n.y * scaledY, scaledX, scaledY);
			g.setColor(Color.BLACK);
			if (n.identity == 1)
				g.fillRect(n.x * scaledX, n.y * scaledY, scaledX, scaledY);
		}

		// print closed List

		closedList = m_pathfinder.GetClosedList();

		ListIterator<NodeLocalTree> listiteratorClosed = closedList.listIterator(closedList.size());
		// Iterator<NodeLocalTree> itClosed = m_pathfinder.GetClosedList().iterator();

		while (listiteratorClosed.hasPrevious()) {
			NodeLocalTree tree = listiteratorClosed.previous();

			g.setColor(Color.GREEN);
			if (tree.m_node.identity == 0)
				g.fillRect(tree.m_node.x * scaledX, tree.m_node.y * scaledY, scaledX, scaledY);

		}

		// print start and end
		g.setColor(Color.RED);
		g.fillRect((int) (mr_mazeModel.mr_origin.x * scaledX), (int) (mr_mazeModel.mr_origin.y * scaledY), scaledX,
				scaledY);
		g.fillRect((int) (mr_mazeModel.mr_destination.x * scaledX), (int) (mr_mazeModel.mr_destination.y * scaledY),
				scaledX, scaledY);

		listiteratorClosed = closedList.listIterator(closedList.size());

		paintPath(g);// Function Below

		while (listiteratorClosed.hasPrevious())
		{
			NodeLocalTree tree = listiteratorClosed.previous();

			g.setColor(Color.YELLOW);
			g.setFont(new Font("default", Font.BOLD, ((int) (0.3 * scaledX))));
			if (scaledY > 15)
			{
				g.drawString(tree.m_node.x + "," + tree.m_node.y, tree.m_node.x * scaledX + ((int) (0.25 * scaledX)),
						tree.m_node.y * scaledY + ((int) (0.50 * scaledX)));
			}
		}
	}

	public void render()
	{
		this.repaint();
	}

	public void paintPath(Graphics g)
	{
		closedList = m_pathfinder.GetClosedList();
		// NodeLocalTree start;

		if (closedList.size() == 0)
			return;
		else
			start = closedList.get(closedList.size() - 1);

		structuredGraph = m_pathfinder.GetStructuredGraph();
		nextNode = null;
		nextGraph = null;

		g.setFont(new Font("default", Font.BOLD, (int) (Math.round(0.20 * scaledX))));

		int count = closedList.size() - 1;
		int countIncrement = 1;

		if (closedList.get(count).m_parent != null)
		{
			nextNode = closedList.get(count).m_parent;
			paintNode(g, nextNode, countIncrement);
			count -= 1;
			countIncrement++;
		}
		else
		{
			return;
		}

		while (nextNode != null)
		{
			nextGraph = structuredGraph.get(nextNode.y).get(nextNode.x);
			nextNode = nextGraph.m_parent;

			if (nextNode != null && structuredGraph.get(nextNode.y).get(nextNode.x).m_parent != null)
				paintNode(g, nextNode, countIncrement);
			else
				break;

			count -= 1;
			countIncrement++;

			if (count <= 0) break; //Dont while loop. timeout render looping when reached closed list size (probably not nessersary). 
		}

	}

	public void paintNode(Graphics g, NodeModel nextNode, int count)
	{
		g.setColor(Color.BLUE);
		g.fillRect((int) (nextNode.x * scaledX), (int) (nextNode.y * scaledY), scaledX, scaledY);
		g.setColor(Color.WHITE);
		if (scaledY > 15)
		{
			g.drawString("" + count, nextNode.x * scaledX + ((int) (0.25 * scaledX)),
					nextNode.y * scaledY + ((int) (0.20 * scaledX)));
			g.drawString("" + Math.round(nextNode.f) + "=" + Math.round(nextNode.g) + "+" + Math.round(nextNode.h),
					nextNode.x * scaledX + ((int) (0.03 * scaledX)), nextNode.y * scaledY + ((int) (0.8 * scaledY)));
		}
	}

	public void setMazeModel(MazeModel mazeModel)
	{
		mr_mazeModel = mazeModel;

		out.msg("maze size when ui loaded" + (int) (width / mazeModel.m_xSize) + " " + (int) (height / mazeModel.m_ySize));

		scaledX = (int) (width / mazeModel.m_xSize);
		scaledY = (int) (height / mazeModel.m_ySize);

		if (scaledX > scaledY)
			scaledX = scaledY;
		else if (scaledX <= scaledY)
			scaledY = scaledX;

		if (scaledX < 5) {
			scaledX = 5;
			scaledY = 5;
		}

		this.width = scaledX * mazeModel.m_xSize;
		this.height = scaledY *mazeModel.m_ySize;
		this.setPreferredSize(new Dimension(width, height));

	}

	private class TimerHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent theEvent)
		{
			render();
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return null;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 5;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	};
}