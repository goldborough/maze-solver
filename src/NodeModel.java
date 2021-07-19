/**
 * @(#)NodeModel.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/5/6
 */

public class NodeModel {
	public int identity;

	public int x;
	public int y;

	public double f;
	public double g;
	public double h;

	public boolean isOpenList;
	public boolean isClosedList;


    public NodeModel() {
    	init();
    }

    public NodeModel(int identIn, int xIn, int yIn) {
		identity = identIn;//0,1
    	x = xIn;
    	y = yIn;
		f = 0;
		g = 0;
		h = 0;
		isOpenList = false;
		isClosedList=false;
    }


    public void init()
    {
    	identity = -1;//0,1
    	x = 0;
    	y = 0;
		f = 0;
		g = 0;
		h = 0;

    	isOpenList = false;
    	isClosedList=false;

    }
}