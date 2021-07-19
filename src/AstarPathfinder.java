/**
 * @(#)AstarPathfinder.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00
 */


//import java.util.Deque;
//import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Vector;

public class AstarPathfinder {

	private Vector<NodeModel> mr_nodesRef;
	private Vector<NodeLocalTree> mr_relationships;
	private Vector<Vector<NodeLocalTree>> mr_structuredGraph;
	
	private ArrayList<NodeLocalTree> m_openList;
	private ArrayList<NodeLocalTree> m_closedList;

	private UIOutputPanel out;
	
    public AstarPathfinder() {
		Init();
    }

	public void Init ()
	{
		out = null;

		mr_nodesRef = new Vector<NodeModel>();

		mr_relationships = new Vector<NodeLocalTree>();
		mr_structuredGraph = null;

		m_openList = null;
		m_closedList = null;
	}

	public void init (Vector<NodeModel> nodes,  Vector<NodeLocalTree> relationships,  Vector<Vector<NodeLocalTree>> structuredGraph, UIOutputPanel o)
	{
		out = o;

		mr_nodesRef = nodes;
		mr_relationships = relationships;
		mr_structuredGraph = structuredGraph;

	}

	public void CleanGraph ()
	{
		for (int i = 0; i < mr_structuredGraph.size(); i++)
		{
			for (int j = 0; j < mr_structuredGraph.get(i).size(); j++)
			{
				mr_structuredGraph.get(i).get(j).m_node.isOpenList = false;
				mr_structuredGraph.get(i).get(j).m_node.isClosedList = false;
				mr_structuredGraph.get(i).get(j).m_parent = null;
				mr_structuredGraph.get(i).get(j).m_child = null;
			}
		}
	}

    public void GeneratePath (NodeModel origin, NodeModel destination)
    {
    	CleanGraph ();

    	int timeoutInitial = 15000;
    	int timeout = timeoutInitial;
		boolean PRINTS = true;

		if (mr_structuredGraph.size() > 25) PRINTS=false;//seriously print its too slow for big maps

    	String s="";

    	boolean isComplete = false;
		//boolean found = false;

    	m_openList = new ArrayList<NodeLocalTree>();
		m_closedList = new ArrayList<NodeLocalTree>();

		/*// A* algorithm f= g + h main variables*/
	    double f = 0.0;//for a node, this is crude a final path length estimate. When comparing f, we want a lower f (g + h)
		double g = 0.0;//g for each node is the distance we calculated it took to get here from the origin
		double h = 0.0;//h is a estimate of apparent distance to the destination for each node

		double resx, resy, distance = 0.0;//difference magnitude calculations (not hidden in vector math here)
		//double SumOfG = 0;//or distance to start of graph if needed somehow (SumOfG = distance;) dist > 0

		NodeLocalTree testingNodeTree=null;
		NodeLocalTree lowestCostNodeTree=null;


		///////////////////////////////////////////////////////////////////////////////////////////////////////////
    	//1. Add starting node to openList ////////////////////////////////////////////////////////////////////////

    	NodeLocalTree currentNodeTree = mr_structuredGraph.get(origin.y).get(origin.x);
    	currentNodeTree.m_node.isOpenList=true;
    	m_openList.add(currentNodeTree);

					resx = Math.abs(currentNodeTree.m_node.x - destination.x);
					resy = Math.abs(currentNodeTree.m_node.y - destination.y);
			    	distance = Math.sqrt(resx*resx + resy*resy)-1;
    	currentNodeTree.m_node.h = distance;
    	currentNodeTree.m_node.g = 0;//we are here
    	currentNodeTree.m_node.f = g+h;


    	if (PRINTS) out.msg("Adding to openlist - initial start vector");
    	if (PRINTS) out.msg("start x "+currentNodeTree.m_node.x+", start y "+currentNodeTree.m_node.y);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
    	//2 Execute a series of steps until the target is found or break out if seach is exhuasted ////////////////

    	while (!isComplete && timeout >0)
    	{

			///////////////////////////////////////////////////////////////////////////////////////////////////////////
    		//2a Find lowest cost node on open list ///////////////////////////////////////////////////////////////////

    		int indexofLowestCostNode = -1;
			double lowestCost = 9999.9;

			for (int i = 0; i < m_openList.size(); i++)
			{
				testingNodeTree = m_openList.get(i);

				//currentNodeTree//Distance from element

				g = currentNodeTree.m_node.g;// SumOfG;
						//we want to recotd distance to the current square and then to this cell as this cell's G number
						resx = Math.abs(currentNodeTree.m_node.x - testingNodeTree.m_node.x);
						resy = Math.abs(currentNodeTree.m_node.y - testingNodeTree.m_node.y);
				    	distance =  Math.sqrt(resx*resx + resy*resy);
				g += distance;//on first iteration it will be 0 our position is the same as the only open list option.

						//vector mag calculation done here
						resx = Math.abs(testingNodeTree.m_node.x - destination.x);
						resy = Math.abs(testingNodeTree.m_node.y - destination.y);
					    distance = Math.sqrt(resx*resx + resy*resy)-1;
					    //-2.0f here for reason.
			    // The heuristic must guess lower then the shortest possible distance to not break A star
				h = distance;
				f = g + h;

				testingNodeTree.m_node.h = h;
    			testingNodeTree.m_node.g = g;
    			testingNodeTree.m_node.f = g+h;

				if (f < lowestCost)
				{
					lowestCost = f;
					indexofLowestCostNode = i;
				}
				if (PRINTS) out.msg(" "+f+", "+g+", "+h+"\n index of lowest cost: "+i);
			}

				/// done thaty now this.

			//select lowest g cost of neighbours
			if (indexofLowestCostNode != -1)//Another way of saying the open list had a node to use which we have indexFor.
			{
				//select best option from openlist with f=g+h
				lowestCostNodeTree = m_openList.get(indexofLowestCostNode);

				if (PRINTS && lowestCostNodeTree.m_node.isClosedList==true) out.msg("somehow an openlist option we selected has already been added to the closed list");
				if (PRINTS) out.msg(" lowestcost x: "+ lowestCostNodeTree.m_node.x + ", y: "+lowestCostNodeTree.m_node.y);


				


				///////////////////////////////////////////////////////////////////////////////////////////////////////////
				// 2d. Now we have our lowest cost node, set its G as OUR sum + latest distance

				// h
				resx = Math.abs(lowestCostNodeTree.m_node.x - destination.x);
				resy = Math.abs(lowestCostNodeTree.m_node.y - destination.y);
				distance = Math.sqrt(resx*resx + resy*resy)-0.5;
				lowestCostNodeTree.m_node.h = distance;

				//g
				if (lowestCostNodeTree.m_parent!=null && (lowestCostNodeTree.m_node.x != origin.x || lowestCostNodeTree.m_node.y != origin.y))//probably origin if its null
				{
					//parent and the implication of their being a parent - its not the orign as it has a  different x or y to the origin
					resx = Math.abs(lowestCostNodeTree.m_parent.x - lowestCostNodeTree.m_node.x);
					resy = Math.abs(lowestCostNodeTree.m_parent.y - lowestCostNodeTree.m_node.y);
					distance = Math.sqrt(resx*resx + resy*resy) + lowestCostNodeTree.m_parent.g;
				}
				else if (lowestCostNodeTree.m_parent==null  && (lowestCostNodeTree.m_node.x != origin.x || lowestCostNodeTree.m_node.y != origin.y))
				{
					//parent null && is not origin
					System.out.println("this should not happen");
				}
				else if (lowestCostNodeTree.m_parent!=null && (lowestCostNodeTree.m_node.x == origin.x && lowestCostNodeTree.m_node.y == origin.y))
				{
					//having a parent should not coinside with being the origin
					System.out.println("this should not happen");
				}

				else distance = 0;
				
				lowestCostNodeTree.m_node.g=  distance;
				lowestCostNodeTree.m_node.f = lowestCostNodeTree.m_node.g+lowestCostNodeTree.m_node.h;


				//2 d.1   SET NODE CHILD TO ME
				if (lowestCostNodeTree.m_parent != null && false)
				{
					mr_structuredGraph.get(lowestCostNodeTree.m_parent.y).get(lowestCostNodeTree.m_parent.x).m_child = lowestCostNodeTree.m_node;
				}

				lowestCostNodeTree.m_node.isOpenList=false;
				lowestCostNodeTree.m_node.isClosedList=true;
				m_closedList.add(lowestCostNodeTree);
				m_openList.remove(indexofLowestCostNode);
				

				///////////////////////////////////////////////////////////////////////////////////////////////////////////
				// 2E. STOP WHEN target added to close list.

				if (lowestCostNodeTree.m_node.x == destination.x && lowestCostNodeTree.m_node.y == destination.y)
				{
					isComplete = true;
					//target added to closed list = target found

					if (PRINTS) out.msg("Adding final");
					lowestCostNodeTree.m_node.isClosedList = true;
					//do somthing to store the path;////////////////////////////////////////////////////////////////////////////
					out.msg("\n Final iterations: "+ (timeoutInitial-timeout));
					return;
				}




				currentNodeTree = lowestCostNodeTree;

				lowestCostNodeTree=null;


				///////////////////////////////////////////////////////////////////////////////////////////////////////////
				// 2F. ADD CHILDREN TO OPENLIST

				if (PRINTS) out.msg("Adding to openlist - children of best node");
				if (PRINTS) s = s.concat("\nAdding children of best node to Openlist: [node"+currentNodeTree.m_node.x+","+currentNodeTree.m_node.x+"]");

				NodeLocalTree childTree = null;
				for (int j=0; j < currentNodeTree.m_children.size(); j++)
				{
					//do not consider items on closed list again;

					///////////////////////////////////////////////////////////////////////////////////////////////////////////
					// 2G. If the edge is in the closed list, dont add it to the open list;
					//we could iterated the closed list and search for the neighbours here;
					//or since we have a boolean in the object

					childTree = mr_structuredGraph.get(currentNodeTree.m_children.get(j).y).get(currentNodeTree.m_children.get(j).x);
					

					if (childTree.m_node.isClosedList==true)
					{
						if (PRINTS) s=s.concat("[denied"+childTree.m_node.x+","+childTree.m_node.y+"] ");
						continue;
					}
					else 

					///////////////////////////////////////////////////////////////////////////////////////////////////////////
					// 2H. If it is on the open list already, check to see if this path to that square is better,

								//Now we have our lowest cost node, set its G as OUR sum + latest distance
					if (childTree.m_node.isOpenList==true)
					{

						childTree.m_parent = currentNodeTree.m_node;

						if (PRINTS) s=s.concat("[new"+childTree .m_node.x+","+childTree .m_node.y+"] ");




						resx = Math.abs(childTree.m_node.x - destination.x);
						resy = Math.abs(childTree.m_node.y - destination.y);
					    h = Math.sqrt(resx*resx + resy*resy);

						resx = Math.abs(currentNodeTree.m_node.x - childTree.m_node.x);
						resy = Math.abs(currentNodeTree.m_node.y - childTree.m_node.y);
					    g =  Math.sqrt(resx*resx + resy*resy);

					    double thisG = currentNodeTree.m_node.g + g;//should actually use g stored in current node TODO ////////////////////////////#########

						if (thisG < childTree.m_node.g )
						{
							childTree.m_node.h= h -0.1;
						    childTree.m_node.g = distance + currentNodeTree.m_node.g;
						    childTree.m_node.f = childTree.m_node.g + childTree.m_node.h;

							childTree.m_node.g = thisG;
							childTree.m_parent = currentNodeTree.m_node;
							childTree.m_node.f = childTree.m_node.g + childTree.m_node.h;
						}
					}
					else
					{
						if (currentNodeTree.m_node!=null)
							{
							childTree.m_parent = currentNodeTree.m_node;
							childTree.m_node.isOpenList=true;
							m_openList.add(childTree);
						}
					}
					if (PRINTS) out.msg(s);
				}//for loop end

			}//finish all work on chosen node on openlist

			else
			{
				if (m_openList.size() == 0 )
				{ testingNodeTree=null; out.msg("Open list is empty. Skipping find best neighbout iteration.");}
				else { testingNodeTree=null; out.msg("Open list was not empty but we did not get an index. This should not happen."); }

				isComplete = true;
				//no path

				out.msg("No path exit.");
				return;
			}


			//AFTER THIS ROUND PRINT CONTENTS OF LISTS
			if (PRINTS) {
				s =" Openlist: "+m_openList.size()+" ";
				for (int i =0; i < m_openList.size();i++)
				{
					testingNodeTree = m_openList.get(i);
					s=s.concat("["+testingNodeTree.m_node.x+","+testingNodeTree.m_node.y+"] ");
				}
				out.msg(s);

				s =" ClosedList: "+m_closedList.size()+" ";
				for (int i =0; i < m_closedList.size();i++)
				{
					testingNodeTree = m_closedList.get(i);
					s=s.concat("["+testingNodeTree.m_node.x+","+testingNodeTree.m_node.y+"] ");
				}
				out.msg(s);
    		}

			timeout-=1;
			if ((timeoutInitial-timeout)%100 ==0) { out.msg("elapsed iterations "+(timeoutInitial-timeout));System.out.println("iterations "+(timeoutInitial-timeout));  }
		}
    }


    public ArrayList<NodeLocalTree> GetOpenList() {return m_openList;}
	public ArrayList<NodeLocalTree> GetClosedList() {return m_closedList;}
	public Vector<Vector<NodeLocalTree>> GetStructuredGraph() {return mr_structuredGraph;}


}