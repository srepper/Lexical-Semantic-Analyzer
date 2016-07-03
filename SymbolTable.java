/*	COP4620 - Project 3
 *	Stephen Repper
 */

public class SymbolTable {
	final private int TSIZE = 53; 
	private Element[] elements;
	private SymbolTable next;
	private SymbolTable prev;
	
	public SymbolTable()
	{
		elements = new Element[TSIZE];
	}
	
	public boolean insert(Element e)
	{
		int hashVal = hash(e.getName());
		int start = hashVal;
		while(elements[hashVal] != null)
		{
			if(elements[hashVal].getName().contentEquals(e.getName()))
			{
				System.out.println("Element " + e.getName() + 
									" already exists in symbol table.");
				return false;
			}
			hashVal = (hashVal + 1) % TSIZE;
			if(hashVal == start)
			{
				System.out.println("Local symbol table full.");
				return false;
			}
		}
		elements[hashVal] = e;
		return true;
	}
	
	public Element lookup(SymbolTable table, String name)
	{
		Element ret = null;
		ret = get(table, name);
		if(ret == null)
			System.out.println("Attempt to use undeclared symbol.");
		
		return ret;
	}
	
	public Element get(SymbolTable table, String name)
	{
		int hashVal = hash(name);
		int start = hashVal;
		
		while(table.elements[hashVal] != null && !table.elements[hashVal].getName().contentEquals(name))
		{
			hashVal = (hashVal + 1) % TSIZE;
			if(hashVal == start)
				break;
		}
		
		if(table.elements[hashVal] != null && table.elements[hashVal].getName().contentEquals(name))
			return table.elements[hashVal];
		else if(table.prev != null)
			return get(table.prev, name);
		else
			return null;
	}
	
	public int hash(String s)
	{
		int retVal = 0;
		for(int i = 0; i < s.length(); i++)
		{
			retVal = (retVal + ((int)Math.pow(8, i+1) * s.charAt(i))) % TSIZE;
		}
		
		return retVal;
	}
	
	public void addTable()
	{
		next = new SymbolTable();
		next.setPrevious(this);
	}
	
	public SymbolTable getNext()
	{
		return next;
	}
	
	public SymbolTable getPrev()
	{
		return prev;
	}
	
	public void setPrevious(SymbolTable st)
	{
		prev = st;
	}
}
