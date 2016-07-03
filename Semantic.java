/*	COP4620 - Project 3
 *	Stephen Repper
 */

import java.io.File;

public class Semantic {
	File file;
	LexicalAnalyzer lex;
	Parser parser;
	Token nextToken;
	SymbolTable st;

	public static void main(String[] args)
	{
		try
		{
			new Semantic(args[0]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("No data file present.");
		}
		catch(Exception e)
		{
			System.out.println("> " + e.getMessage());
		}
	}
	
	/*	Constructor opens file, sends it to LexicalAnalyzer, closes the
	 * 	local file, and creates & runs the Parser.
	 */
	public Semantic(String arg) throws Exception
	{
		st = new SymbolTable();
		file = new File(arg);
		parser = new Parser(file, st);
		file = null;
		st = parser.run();
	}
}
