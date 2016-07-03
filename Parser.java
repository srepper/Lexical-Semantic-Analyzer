/*	COP4620 - Project 3
 *	Stephen Repper
 *
 *	main() has been moved to Semantic.java for Project 3
 */


import java.io.File;

public class Parser
{
	LexicalAnalyzer lex;
	Token nextToken;
	SymbolTable st;
	FunElement func;
	Element e;
	boolean main = false;
	
	/*	Constructor calls LexicalAnalyzer for the first token from the file.
	 */
	public Parser(File file, SymbolTable symtab) throws Exception
	{
		lex = new LexicalAnalyzer(file);
		nextToken = lex.getNextToken();
		st = symtab;
	}
	
	/*	Run calls the first grammar rule.  If no errors occur during the parse,
	 * 	program() will complete, and success will be printed to screen.
	 */
	public SymbolTable run()
	{
		program();
		if(!main)
		{
			System.out.println("No main() in program.");
			semFail();
		}
		System.out.println("Complete.");
		return st;
	}
	
	/*	Accept current token by calling getNextToken() from LexicalAnalyzer
	 */
	private void accept()
	{
		nextToken = lex.getNextToken();
	}
	
	/*	All grammar functions are merely implementations of
	 * 	their respective grammar and semantic rules. 
	 */
	private void program()
	{
		if(nextToken.getToken().contentEquals("int") ||
				nextToken.getToken().contentEquals("float") ||
				nextToken.getToken().contentEquals("void"))
			decList();
		else
			parseFail();
	}
	
	private void decList()
	{
		if(nextToken.getToken().contentEquals("int") ||
			nextToken.getToken().contentEquals("float") ||
			nextToken.getToken().contentEquals("void"))
		{
			dec();
			decListTwo();
		}
		else
			parseFail();
	}
	
	private void decListTwo()
	{
		if(nextToken.getToken().contentEquals("int") ||
				nextToken.getToken().contentEquals("float") ||
				nextToken.getToken().contentEquals("void"))
		{
			dec();
			decListTwo();
		}
		else if(!nextToken.getToken().contentEquals("$"))
			parseFail();
	}
	
	private void dec()
	{
		if(nextToken.getToken().contentEquals("int") ||
				nextToken.getToken().contentEquals("float") ||
				nextToken.getToken().contentEquals("void"))
		{
			typeSpec();
			if(nextToken.getType().contentEquals("ID"))
			{
				e.setName(nextToken.getToken());
				accept();
			}
			else
				parseFail();
			decTwo();
		}
		else 
			parseFail();
	}
	
	private void decTwo()
	{
		if(nextToken.getToken().contentEquals("("))
		{
			funDec();
		}
		else if(nextToken.getToken().contentEquals("[") ||
				nextToken.getToken().contentEquals(";"))
			varDec();
		else
			parseFail();
	}
	
	private void typeSpec()
	{
		if(nextToken.getToken().contentEquals("int") ||
				nextToken.getToken().contentEquals("float") ||
				nextToken.getToken().contentEquals("void"))
		{
			e = new Element(nextToken.getToken());
			accept();
		}
	}
	
	private void varDec()
	{
		if(e.getType().contentEquals("VOID"))
		{
			System.out.println("Variable cannot be type void.");
			semFail();
		}
		if(nextToken.getToken().contentEquals("["))
		{
			ArrayElement a = new ArrayElement(e);
			a.setValue("0");
			e = null;
			accept();
			if(nextToken.getType().contentEquals("NUM"))
			{
				accept();
				if(nextToken.getToken().contentEquals("]"))
				{
					accept();
					if(nextToken.getToken().contentEquals(";"))
					{
						if(!st.insert(a))
							semFail();
						accept();
					}
					else
						parseFail();
				}
				else
					parseFail();
			}
			else
				parseFail();
		}
		else if(nextToken.getToken().contentEquals(";"))
		{
			VarElement v = new VarElement(e);
			e = null;
			if(!st.insert(v))
				semFail();
			accept();
		}
		else
			parseFail();
	}
	
	private void funDec()
	{
		if(nextToken.getToken().contentEquals("("))
		{
			boolean ret = false;
			func = new FunElement(e);
			e = null;
			accept();
			if(main == true)
			{
				System.out.println("main() is not last function declared.");
				semFail();
			}
			else if(func.getName().contentEquals("main"))
				main = true;
				
			st.addTable();
			params();
			if(nextToken.getToken().contentEquals(")"))
			{
				if(!st.insert(func))
					semFail();
				accept();
				ret = cmpndStmt(ret);
				if(!(ret || st.lookup(st, func.getName()).getType().contentEquals("VOID")))
				{
					System.out.println("Function " + func.getName() + "() is not void type; " +
							" must have a return.");
					semFail();
				}
				func = null;
			}
			else
				parseFail();
		}
		else
			parseFail();
	}
	
	private void params()
	{
		if(nextToken.getToken().contentEquals("int") ||
			nextToken.getToken().contentEquals("float"))
		{
			e = new Element(nextToken.getToken());
			accept();
			paramList();
		}
		else if(nextToken.getToken().contentEquals("void"))
		{
			e = new Element(nextToken.getToken());
			func.addParam(e);
			accept();
		}
		else
			parseFail();
	}
	
	private void paramList()
	{
		if(nextToken.getType().contentEquals("ID"))
		{
			e.setName(nextToken.getToken());
			accept();
			paramPost();
			pListTwo();
		}
		else
			parseFail();
	}
	
	private void pListTwo()
	{
		if(nextToken.getToken().contentEquals(","))
		{
			accept();
			param();
			pListTwo();
		}
		else if(!nextToken.getToken().contentEquals(")"))
			parseFail();
	}
	
	private void param()
	{
		if(nextToken.getToken().contentEquals("int") ||
				nextToken.getToken().contentEquals("float"))
		{
			e = new Element(nextToken.getToken());
			accept();
			if(nextToken.getType().contentEquals("ID"))
			{
				e.setName(nextToken.getToken());
				accept();
			}
			else
				parseFail();
			paramPost();
		}
		else
			parseFail();
	}
	
	private void paramPost()
	{
		if(nextToken.getToken().contentEquals("["))
		{
			accept();
			if(nextToken.getToken().contentEquals("]"))
			{
				ArrayElement a = new ArrayElement(e);
				e = null;
				func.addParam(a);
				if(!st.getNext().insert(a))
					semFail();
				accept();
			}
			else
				parseFail();
		}
		else if(nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals(","))
		{
			VarElement v = new VarElement(e);
			e = null;
			func.addParam(v);
			if(!st.getNext().insert(v))
				semFail();
		}
		else
			parseFail();
	}
	
	private boolean cmpndStmt(boolean r)
	{
		st = st.getNext();
		if(nextToken.getToken().contentEquals("{"))
		{
			accept();
			localDecs();
			r = stmtList(r);
			if(nextToken.getToken().contentEquals("}"))
			{
				st = st.getPrev();
				accept();
			}
			else
				parseFail();	
		}
		else
			parseFail();
		
		return r;
	}
	
	private void localDecs()
	{
		if(nextToken.getToken().contentEquals("int") ||
				nextToken.getToken().contentEquals("float") ||
				nextToken.getToken().contentEquals("void"))
		{
			typeSpec();
			if(nextToken.getType().contentEquals("ID"))
			{
				e.setName(nextToken.getToken());
				accept();
			}
			else
				parseFail();
			varDec();
			localDecs();
		}
		else if(!(nextToken.getToken().contentEquals("if") ||
				nextToken.getToken().contentEquals("while") ||
				nextToken.getToken().contentEquals("return") ||
				nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("(") ||
				nextToken.getToken().contentEquals("{") ||
				nextToken.getToken().contentEquals("}") ||
				nextToken.getToken().contentEquals(";")))
			parseFail();
	}
	
	private boolean stmtList(boolean r)
	{
		if(nextToken.getToken().contentEquals("if") ||
				nextToken.getToken().contentEquals("while") ||
				nextToken.getToken().contentEquals("return") ||
				nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("(") ||
				nextToken.getToken().contentEquals("{") ||
				nextToken.getToken().contentEquals(";"))
		{
			r = statement(r);
			r = stmtList(r);
		}
		else if(!nextToken.getToken().contentEquals("}"))
			parseFail();
		
		return r;
	}
	
	private boolean statement(boolean r)
	{
		if(nextToken.getToken().contentEquals("if"))
			r = selStmt(r);
		else if(nextToken.getToken().contentEquals("while"))
			iterStmt();
		else if(nextToken.getToken().contentEquals("return"))
			r = returnStmt(r);
		else if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("(") ||
				nextToken.getToken().contentEquals(";"))
			expnStmt();
		else if(nextToken.getToken().contentEquals("{"))
		{
			st.addTable();
			cmpndStmt(false);
		}
		else
			parseFail();
		
		return r;
	}
	
	private void expnStmt()
	{
		if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
		{
			expression();
			if(nextToken.getToken().contentEquals(";"))
					accept();
			else
				parseFail();
		}
		else if(nextToken.getToken().contentEquals(";"))
			accept();
		else
			parseFail();
	}
	
	private boolean selStmt(boolean r)
	{
		if(nextToken.getToken().contentEquals("if"))
		{
			accept();
			if(nextToken.getToken().contentEquals("("))
				accept();
			else
				parseFail();
			
			expression();
			
			if(nextToken.getToken().contentEquals(")"))
				accept();
			else
				parseFail();
			r = statement(r);
			r = selStmtTwo(r);
		}
		else
			parseFail();
		
		return r;
	}
	
	private boolean selStmtTwo(boolean r)
	{
		if(nextToken.getToken().contentEquals("else"))
		{
			accept();
			boolean b = statement(r);
			if((b && !r) || (r && !b))
				r = false;
		}
		else if(nextToken.getToken().contentEquals("if") ||
				nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("while") ||
				nextToken.getToken().contentEquals("return") ||
				nextToken.getToken().contentEquals("(") ||
				nextToken.getToken().contentEquals("{") ||
				nextToken.getToken().contentEquals("}") ||
				nextToken.getToken().contentEquals(";"))
			;
		else
			parseFail();
		
		return r;
	}
	
	private void iterStmt()
	{
		if(nextToken.getToken().contentEquals("while"))
		{
			accept();
			if(nextToken.getToken().contentEquals("("))
				accept();
			else
				parseFail();
			expression();
			if(nextToken.getToken().contentEquals(")"))
				accept();
			else
				parseFail();
			statement(false);
		}
		else
			parseFail();
	}
	
	private boolean returnStmt(boolean r)
	{
		if(nextToken.getToken().contentEquals("return"))
		{
			r = true;
			accept();
			returnTwo();
		}
		else
			parseFail();
		
		return r;
	}
	
	private void returnTwo()
	{
		if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
		{
			Element rtn = expression();
			if(nextToken.getToken().contentEquals(";"))
			{
				if(!st.lookup(st, func.getName()).getType().contentEquals(rtn.getType()) ||
						rtn.getClass() == new ArrayElement(new Element()).getClass() ||
						rtn.getClass() == new FunElement(new Element()).getClass())
				{
					System.out.println("Incorrect return type.");
					semFail();
				}
				accept();
			}
			
			else
				parseFail();
		}
		else if(nextToken.getToken().contentEquals(";"))
		{
			if(!st.lookup(st, func.getName()).getType().contentEquals("VOID"))
			{
				System.out.println("Return type missing.");
				semFail();
			}
			accept();
		}
		else
			parseFail();
	}
	
	private Element expression()
	{
		Element exp = new Element();
		if(nextToken.getType().contentEquals("ID"))
			exp = expressID(exp);
		else if(nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
			exp = expressOther(exp);
		else
			parseFail();
		
		return exp;
	}
	
	private Element expressOther(Element exp)
	{
		if(nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
		{
			exp = factorAlt(exp);
			exp = termTwo(exp);
			exp = addExpnTwo(exp);
			exp = simpleExpn(exp);
		}
		
		return exp;
	}
	
	private Element expressID(Element exp)
	{
		if(nextToken.getType().contentEquals("ID"))
		{
			Element ele;
			if((ele = (st.lookup(st, nextToken.getToken()))) == null)
				semFail();
			else
				exp = ele.clone();
			
			accept();
		}
		else
			parseFail();
		exp = expressIDTwo(exp);
		
		return exp;
	}
	
	private Element expressIDTwo(Element exp)
	{
		if(nextToken.getToken().contentEquals("("))
		{
			if(exp.getClass() != new FunElement(new Element()).getClass())
			{
				System.out.println(exp.getName() + " is not a function.");
				semFail();
			}
				
			exp = call(new FunElement(exp.getName(), exp.getType()));
			exp = expressIDCall(exp);
		}
		else if(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-") ||
				nextToken.getToken().contentEquals("*") ||
				nextToken.getToken().contentEquals("/") ||
				nextToken.getToken().contentEquals("=") ||
				nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!=") ||
				nextToken.getToken().contentEquals("["))
		{
			exp = var(exp);
			exp = expressIDVar(exp);
		}
		else if(!(nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(",") ||
				nextToken.getToken().contentEquals(";")))
			parseFail();
		
		return exp;
	}
	
	private Element expressIDVar(Element exp)
	{
		if(nextToken.getToken().contentEquals("="))
		{
			if(exp.getClass() == new FunElement(new Element()).getClass())
			{
				System.out.println("Left side is a function.");
				semFail();
			}
			accept();
			Element ele = expression();
			if(exp.getClass() == new ArrayElement(new Element()).getClass())		
			{
				System.out.println("Left side is array variable.");
				semFail();
			}
			else if(ele.getClass() == new ArrayElement(new Element()).getClass() &&
					exp.getClass() != ele.getClass())
			{
				System.out.println("Attempt to assign unindexed array variable.");
				semFail();
			}
			else if(!exp.getType().contentEquals(ele.getType()))
			{
				System.out.println("Type error: " + exp.getType() + " = " + ele.getType());
				semFail();
			}
		}
		else if(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-") ||
				nextToken.getToken().contentEquals("*") ||
				nextToken.getToken().contentEquals("/") ||
				nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!="))
		{
			exp = termTwo(exp);
			exp = addExpnTwo(exp);
			exp = simpleExpn(exp);
		}
		else if(!(nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(";") ||
				nextToken.getToken().contentEquals(",")))
			parseFail();
		
		return exp;
	}
	
	private Element expressIDCall(Element exp)
	{
		if(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-") ||
				nextToken.getToken().contentEquals("*") ||
				nextToken.getToken().contentEquals("/") ||
				nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!="))
		{
			exp = termTwo(exp);
			exp = addExpnTwo(exp);
			exp = simpleExpn(exp);
		}
		else if(nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(",") ||
				nextToken.getToken().contentEquals(";"))
			;
		else
			parseFail();
		
		return exp;
	}
	
	private Element var(Element exp)
	{
		if(nextToken.getToken().contentEquals("["))
		{
			if(exp.getClass() != new ArrayElement(new Element()).getClass())
			{
				System.out.println("Attempt to index non-array variable.");
				semFail();
			}
			accept();
			Element i = expression();
			if(!i.getType().contentEquals("NUM"))
			{
				System.out.println("Invalid array index.");
				semFail();
			}
			
			if(nextToken.getToken().contentEquals("]"))
			{
				accept();
				Element ele = new Element(exp.getType());
				exp = ele;
			}
			else
				parseFail();
		}
		else if(!(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-") ||
				nextToken.getToken().contentEquals("*") ||
				nextToken.getToken().contentEquals("/") ||
				nextToken.getToken().contentEquals("=") ||
				nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!=")))
			parseFail();
		
		return exp;
	}
	
	private Element simpleExpn(Element exp)
	{
		if(nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!="))
		{
			relop();
			addExpn();
		}
		else if(!(nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(",") ||
				nextToken.getToken().contentEquals(";")))
			parseFail();
		
		return exp;
	}
	
	private String relop()
	{
		if(nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!="))
		{
			String t = nextToken.getToken();
			accept();
			return t;
		}
		return null;
	}
	
	private Element addExpn()
	{
		Element a = new Element();
		if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
		{
			a = term();
			a = addExpnTwo(a);
		}
		else
			parseFail();
		
		return a;
	}
	
	private Element addExpnTwo(Element exp)
	{
		if(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-"))
		{
			String op = addop();
			Element t = term();
			if(op.contentEquals("+") || op.contentEquals("-"))
			{
				if(exp.getClass() == new ArrayElement(new Element()).getClass() &&
					t.getClass() != exp.getClass())
				{
					System.out.println("Attempt to operate on non-indexed array variable.");
					semFail();
				}
				else if(!exp.getType().contentEquals(t.getType()))
				{
					System.out.println("Type error: "+exp.getType()+" "+op+" "+t.getType());
					semFail();
				}
			}
			exp = addExpnTwo(exp);
		}
		else if(nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!=") ||
				nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(",") ||
				nextToken.getToken().contentEquals(";"))
		;
		else
			parseFail();
		
		return exp;
	}
	
	private String addop()
	{
		if(nextToken.getToken().contentEquals("+") || nextToken.getToken().contentEquals("-"))
		{
			String o = nextToken.getToken();
			accept();
			return o;
		}
		else
			parseFail();
		
		return null;
	}
	
	private Element term()
	{
		Element t = new Element();
		if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
		{
			t = factor(t);
			t = termTwo(t);
		}
		else
			parseFail();
		
		return t;
	}
	
	private Element termTwo(Element exp)
	{
		if(nextToken.getToken().contentEquals("*") ||
				nextToken.getToken().contentEquals("/"))
		{
			String op = mulop();
			Element t = factor(new Element());
			if(op.contentEquals("*") || op.contentEquals("/"))
			{
				if(exp.getClass() == new ArrayElement(new Element()).getClass() &&
						t.getClass() != exp.getClass())
				{
						System.out.println("Attempt to operate on non-indexed array variable.");
						semFail();
				}
				else if(!exp.getType().contentEquals(t.getType()))
				{
					System.out.println("Type error: "+exp.getType()+" "+op+" "+t.getType());
					semFail();
				}
			}
				
			exp = termTwo(exp);
		}
		else if(!(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-") ||
				nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!=") ||
				nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(",") |
				nextToken.getToken().contentEquals(";")))
			parseFail();
		
		return exp;
	}
	
	private String mulop()
	{
		if(nextToken.getToken().contentEquals("*"))
		{
			accept();
			return "*";		
		}
		else if(nextToken.getToken().contentEquals("/"))
		{
			accept();
			return "/";
		}
		else
			parseFail();
		
		return null;
	}
	
	private Element factor(Element exp)
	{
		if(nextToken.getType().contentEquals("ID"))
		{
			exp = st.lookup(st, nextToken.getToken()).clone();
			accept();
			exp = iden(exp);
		}
		else if(nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT"))
		{
			exp.setType(nextToken.getType());
			exp.setValue(nextToken.getToken());
			accept();
		}
		else if(nextToken.getToken().contentEquals("("))
		{
			accept();
			exp = expression();
			if(nextToken.getToken().contentEquals(")"))
				accept();
			else
				parseFail();
		}
		
		return exp;
	}
	
	private Element factorAlt(Element exp)
	{
		if(nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT"))
		{
			exp.setType(nextToken.getType());
			exp.setValue(nextToken.getToken());
			accept();
		}
		else if(nextToken.getToken().contentEquals("("))
		{
			accept();
			exp = expression();
			if(nextToken.getToken().contentEquals(")"))
				accept();
			else
				parseFail();
		}
		
		return exp;
	}
	
	private Element iden(Element exp)
	{
		if(nextToken.getToken().contentEquals("("))
			exp = call(new FunElement(exp.getName(), exp.getType()));
		else if(nextToken.getToken().contentEquals("["))
			exp = var(exp);
		else if(!(nextToken.getToken().contentEquals("+") ||
				nextToken.getToken().contentEquals("-") ||
				nextToken.getToken().contentEquals("*") ||
				nextToken.getToken().contentEquals("/") ||
				nextToken.getToken().contentEquals("<=") ||
				nextToken.getToken().contentEquals("<") ||
				nextToken.getToken().contentEquals(">") ||
				nextToken.getToken().contentEquals(">=") ||
				nextToken.getToken().contentEquals("==") ||
				nextToken.getToken().contentEquals("!=") ||
				nextToken.getToken().contentEquals(")") ||
				nextToken.getToken().contentEquals("]") ||
				nextToken.getToken().contentEquals(",") ||
				nextToken.getToken().contentEquals(";")))
			parseFail();
		
		return exp;
	}
	
	private Element call(FunElement exp)
	{
		if(nextToken.getToken().contentEquals("("))
		{
			accept();
			exp = args(exp);
			if(nextToken.getToken().contentEquals(")"))
				accept();
			else
				parseFail();
			
			if(exp.getParams().size() == 0)
				exp.addParam(new Element("VOID"));
			
			FunElement f = (FunElement)st.lookup(st, exp.getName());
			if(exp.getParams().size() != f.getParams().size())
			{
				System.out.println("Mismatched parameters & arguments.");
				semFail();
			}
			
			for(int i = 0; i < exp.getParams().size(); i++)
			{	
				if((f.getParams().get(i).getClass() == new ArrayElement(new Element()).getClass() && 
						exp.getParams().get(i).getClass() != f.getParams().get(i).getClass()) ||
						!exp.getParams().get(i).getType().contentEquals(f.getParams().get(i).getType()))
				{
					System.out.println("Mismatched parameters & arguments.");
					semFail();
				}
			}
		}
		else
			parseFail();
		
		Element ele = new VarElement(exp);
		return ele;
	}
	
	private FunElement args(FunElement exp)
	{
		if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
			exp = argList(exp);
		else if(!(nextToken.getToken().contentEquals(")")))
			parseFail();
		
		return exp;
	}
	
	private FunElement argList(FunElement exp)
	{
		if(nextToken.getType().contentEquals("ID") ||
				nextToken.getType().contentEquals("NUM") ||
				nextToken.getType().contentEquals("FLT") ||
				nextToken.getToken().contentEquals("("))
		{
			exp.addParam(expression());
			exp = argListTwo(exp);
		}
		
		return exp;
	}
	
	private FunElement argListTwo(FunElement exp)
	{
		if(nextToken.getToken().contentEquals(","))
		{
			accept();
			exp.addParam(expression());
			exp = argListTwo(exp);	
		}
		else if(!nextToken.getToken().contentEquals(")"))
			parseFail();
		
		return exp;
	}
	
	/*	Method for ending syntactic analysis in error if an
	 * 	undefined state is encountered. 
	 */
	private void parseFail()
	{
		System.out.println("Parse failed.");
		System.exit(1);
	}
	
	/*	Method for ending semantic analysis in error if an
	 *  undefined state is encountered.
	 */
	private void semFail()
	{
		System.out.println("Semantic analysis failed.");
		System.exit(1);
	}
}