/**
 * @(#)OperationResult.java
 *
 *
 * @author Michael Goldborough
 * @version 1.00 2019/5/6
 */

public class OperationResult
{
	boolean success;

	public OperationResult(){}

	public OperationResult(boolean in)
	{
		init(in);
	}


	public void init(boolean in)
	{
		success = in;
	}

}