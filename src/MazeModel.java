/**
 * @(#)MazeModel.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/4/26
 */

import java.awt.Dimension;
//import java.awt.geom.Point2D;

import java.util.Vector;

 enum MazeInitState
 {
	maze_not_initialised,
	maze_vectorCreated,
	maze_initialised,
	maze_init_state_count
};

public class MazeModel {
	private MazeInitState m_state = MazeInitState.maze_not_initialised;

	//public int x, y; coordinates if the maze was a subset in a larger space

	//maze size, all vectors are assuming a complete and rectangular set of data using this width and height
	public int m_xSize = 0;
	public int m_ySize = 0;

	public NodeModel mr_origin;//ref to origin node
	public NodeModel mr_destination;//ref to target node

	private int neighbourRelY;
	private int neighbourRelX;

	//m_maze
	////Only an array of nodemodel elements but kept as a structure, might fit more in cache but be more useful for toroidal  space trigonomety calculations
	//Quadrant 4 coordiate space when printed
	private Vector<Vector<NodeModel>> m_maze;

	////Only an array of nodemodel elements as read in from text file (might fit more nodes into cache if applicable, for example render passes);
	private Vector<NodeModel> m_nodes;

	//m_relationships
	//keeping a list like this could be used to search each element for a lowest value
	//or to peform a sort operation on as needed without ruining a structure as the other vectors have (make a temp sort value integer in nodelocal tree
	private Vector<NodeLocalTree> m_relationships;

	//Most useful for selecting items to add to an Openlist in an A* search
	private Vector<Vector<NodeLocalTree>> m_structuredGraph; //Quadrant 4 coordiate space when printed

	public MazeModel() {
		m_xSize = 0;
		m_ySize = 0;
		m_maze = new Vector<Vector<NodeModel>>();
		m_nodes = new Vector<NodeModel>();
		m_state = MazeInitState.maze_vectorCreated;
	}

	public void init()
	{
		m_maze = new Vector<Vector<NodeModel>>();
		m_nodes =  new Vector<NodeModel>();
		m_relationships =  new Vector<NodeLocalTree>();
		m_structuredGraph =  new Vector<Vector<NodeLocalTree>>();

		m_state = MazeInitState.maze_initialised;
	}

	//METHOD buildGraphFromManhattanSystem
	//for any seach we will need to know the local neighbours of any node - at each step of a search
	//we will construct a graph as soon as possible and select a search algorithm after this step which will use the graph

    public boolean buildGraphForManhattanSystem(boolean isToroidalMap, boolean isDiagonalValid, int wallIdentity, int pathIdentity) {

		m_structuredGraph.setSize(0);
		m_structuredGraph.setSize(m_ySize);

		NodeModel currentNode = null;
		Vector<NodeModel> currentRow = null;

		Vector<NodeLocalTree> currentStructuredRow = null;
		NodeModel neighbour = null;
		NodeLocalTree nodeTree = null;

		Vector<NodeLocalTree> newStructuredRow=null;
		OperationResult result = new OperationResult(false);

		try {

			for (int y =0; y < m_ySize; y++)
			{
				currentRow = m_maze.get(y);

				newStructuredRow = new 	Vector<NodeLocalTree>();
				m_structuredGraph.set(y,newStructuredRow);

				currentStructuredRow = m_structuredGraph.get(y);
				currentStructuredRow.setSize(m_xSize);

				for (int x =0; x < m_xSize; x++)
				{
					currentNode = currentRow.get(x);

					boolean isActive = false;//represents a wall,
					if (currentNode.identity==pathIdentity) isActive=true;

					nodeTree=null;

					nodeTree = new NodeLocalTree(currentNode, isActive);
					currentStructuredRow.set(x,nodeTree);

					if (currentNode.identity!=pathIdentity) continue;//we dont need to fill graphs from wall pieces;

					//String s="";

					for (int xRel = -1; xRel <=1; xRel++)
					{
						for (int yRel = -1; yRel <=1; yRel++)
						{
							if (xRel==0 && yRel==0) continue;//dont add self as a neighbour
							else if (isDiagonalValid==false) {
								if (xRel!=0 && yRel!=0) continue;//dont add anything that doesnt share an x or y coord
							}
							result.init(false);
							neighbour = getVectorLocalNeighbour(currentNode,isToroidalMap,xRel,yRel,m_xSize,m_ySize,result);

							if (result.success==true && neighbour.identity == pathIdentity) {
								 nodeTree.m_children.add(neighbour);//our method gave us a neighbour that is valid and not null if success is set;
								 //s = s.concat("Y");
							}

							//if (neighbour.identity != pathIdentity) s = s.concat("N");
						}
						//s = s.concat(" ");
					}
					//System.out.println(s);
				}
			}
		}
		catch (Exception ex)
		{
				if (currentNode==null)System.out.println("1\n");
				if (currentStructuredRow==null)System.out.println("2\n");
				if (neighbour==null)System.out.println("3\n");
				if (nodeTree.m_children==null)System.out.println("4\n");
				if (currentRow==null)System.out.println("5\n");
				if (m_structuredGraph==null)System.out.println("6\n");
				System.out.println("GRAPH ex: " + ex + "\n");
		}

	return true;
    }

    private NodeModel getVectorLocalNeighbour(NodeModel currentNode,boolean isToroidalMap,int xRel,int yRel,int m_xSize,int m_ySize, OperationResult result)
    {
    	//this gets the 1 of 8 neighbours in manhattan grid space setup

    	neighbourRelY = currentNode.y  + yRel;
    	neighbourRelX = currentNode.x  + xRel;

		if (isToroidalMap)
		{
	    	if (neighbourRelY <0) neighbourRelY += m_ySize;
	    	else if (neighbourRelY >= m_ySize) neighbourRelY -= m_ySize;

	    	if (neighbourRelX <0) neighbourRelX += m_xSize;
	    	else if (neighbourRelX >= m_xSize) neighbourRelX -= m_xSize;

			result.success=true;
		}
		else if (!isToroidalMap)
		{
			if (neighbourRelY <0) {result.success=false;return null;}
			else if (neighbourRelY >= m_ySize) {result.success=false;return null;}

			if (neighbourRelX <0)  {result.success=false;return null;}
			else if (neighbourRelX >= m_xSize)  {result.success=false;return null;}

			result.success=true;
		}

		NodeModel node = null;

		try
		{
    		node= m_maze.get(neighbourRelY).get(neighbourRelX);
		}
		catch (Exception ex) {
			System.out.println(ex); return null;
		}

		if (node==null) System.out.println("Node should not be null");

		result.success = true;

		return node;
	}

	public boolean setState(MazeInitState state)
	{
		switch(state)
		{
			case maze_not_initialised:
 			case maze_vectorCreated:
 			case maze_initialised:

 				 m_state = state;
 				 return true;
 		}
	return false;
	}

	public MazeInitState getState()
	{
		return m_state;
	}

	public Vector<Vector<NodeModel>> getMaze()
	{
		return m_maze;
	}

	public Vector<NodeModel> getNodeList()
	{
		return m_nodes;
	}

	public Vector<NodeLocalTree> getRelationships()
	{
		return m_relationships;
	}

	public Vector<Vector<NodeLocalTree>> getStructuredGraph()
	{
		return m_structuredGraph;
	}

}