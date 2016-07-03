/*	COP4620 - Project 3
 *	Stephen Repper
 */

public class ArrayElement extends Element
{
	public ArrayElement(Element e)
	{
		name = e.getName();
		type = e.getType();
	}
	
	public ArrayElement clone()
	{
		ArrayElement a = new ArrayElement(this);
		return a;
	}
}
