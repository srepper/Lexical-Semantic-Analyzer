/*	COP4620 - Project 3
 *	Stephen Repper
 */

import java.util.Vector;

public class FunElement extends Element
{
	private Vector<Element> params;
	
	public FunElement(Element e)
	{
		name = e.getName();
		type = e.getType();
		params = new Vector<Element>();
	}
	
	public FunElement(String n, String t)
	{
		name = n;
		type = t;
		params = new Vector<Element>();
	}
	
	public void addParam(Element p)
	{
		params.add(p);
	}
	
	public Vector<Element> getParams()
	{
		return params;
	}
	
	public FunElement clone()
	{
		FunElement f = new FunElement(this);
		return f;
	}
}
