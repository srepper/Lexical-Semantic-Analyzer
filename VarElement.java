/*	COP4620 - Project 3
 *	Stephen Repper
 */

public class VarElement extends Element
{
	public VarElement(Element e)
	{
		name = e.getName();
		type = e.getType();
	}
	
	public VarElement clone()
	{
		VarElement v = new VarElement(this);
		return v;
	}
}
