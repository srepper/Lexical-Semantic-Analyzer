/*	COP4620 - Project 3
 *	Stephen Repper
 */

public class Element {
	protected String name;
	protected String type;
	protected String value;
	
	public Element()
	{
		name = null;
		type = null;
		value = null;
	}
	
	public Element(String t)
	{
		if(t.contentEquals("int"))
			type = "NUM";
		else if(t.contentEquals("float"))
			type = "FLT";
		else if(t.contentEquals("void"))
			type = "VOID";
		else
			type = t;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void setType(String t)
	{
		type = t;
	}
	
	public void setValue(String v)
	{
		value = v;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Element clone()
	{
		return new Element();
	}
}
