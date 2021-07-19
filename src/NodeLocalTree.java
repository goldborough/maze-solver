/**
 * @(#)NodeLocalTree.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/5/6
 */

import java.util.Vector;

public class NodeLocalTree {

	public boolean m_isActiveNode;

	public NodeModel m_parent=null;
	public NodeModel m_child=null;;

    public NodeModel m_node;
    public Vector<NodeModel> m_children;

    public NodeLocalTree(NodeModel node, boolean isActive)
    {
    	init(node, isActive);
    }

     public void init(NodeModel node, boolean isActive)
     {
		m_parent=null;
		m_child=null;;
     	m_isActiveNode = isActive;
    	m_node=node;
    	m_children = new Vector<NodeModel>();
    }
}