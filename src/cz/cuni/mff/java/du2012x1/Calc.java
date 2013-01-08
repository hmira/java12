package cz.cuni.mff.java.du2012x1;


import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** DU 1. 
  * 
  * @author Peter Hmíra
  */
public class Calc {

	public static class EqRead
	{
		
	}
	

	public static Hashtable<String, Double> ht_variables = new Hashtable<String, Double>();
	public static ArrayList<String> variables = new ArrayList<String>();
	public static ArrayList<String> functions = new ArrayList<String>();
	
	public static Hashtable<String, String> ht_function_arguments = new Hashtable<String, String>();
	public static Hashtable<String, String> ht_function_equation = new Hashtable<String, String>();
	
	public static String ValidateVariable(String s)
	{
		String str = "";
		boolean variable = true;
		for (int i = 0; i < s.length(); i++) 
		{
			char c = s.charAt(i);
			
			if (Character.isWhitespace(c) && str.equals(""))
			{
				continue;
			}
			else if (Character.isWhitespace(c) && !str.equals(""))
			{
				variable = false;
			}
			else if (Character.isLetter(c) && variable)
			{
				str += Character.toString(c);
			}
			else
			{
				return "";
			}
		}
		return str;
	}
	
	public static boolean ValidateFunction(String s)
	{
		Pattern pt = Pattern.compile("^\\s*DEF\\s+([a-zA-Z]+)\\(((?:[a-zA-Z]\\s*,\\s*)*(?:[a-zA-Z]))\\)");
		Matcher m = pt.matcher(s);
		String[] str_arr = null;
		
		String equation = "";
		String params = "";
		String functionName = "";
		
		if (m.find())
		{
			System.out.println(m.group(1).replaceAll("\\s", ""));
			System.out.println(m.group(2).replaceAll("\\s", ""));
			functionName = m.group(1).replaceAll("\\s", "");
			params = m.group(2).replaceAll("\\s", "");
			str_arr = (m.group(2).replaceAll("\\s", "")).split(",");
			for (int i = 0; i < str_arr.length; i++) {
				String s1 = str_arr[i];
				for (int j = 0; j < str_arr.length; j++) {
					String s2 = str_arr[j];
					if (i != j && s1.equals(s2))
						return false;
				}
			}
		}
		
							//^\\s*DEF\\s+[a-zA-Z]+\\(  (  [a-zA-Z]\\s*,\\s*)*(  [a-zA-Z])\\)\\s+ (  (  (  \\(\\s*)*(  (  [a-zA-Z]|[-]?[0-9]*\\.?[0-9]+(  [eE][-+]?[0-9]+)?)\\s*(  \\)\\s*)*)\\s*[/*+-]\\s*)*\\s*(  \\(  \\s*)*([a-zA-Z]|[-]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?))\\s*(\\)\\s*)*?$
		pt = Pattern.compile("^(\\s*DEF\\s+[a-zA-Z]+\\((?:[a-zA-Z]\\s*,\\s*)*([a-zA-Z])\\)\\s+).*$");
		m = pt.matcher(s);
		String equation2 = "";
		if (m.find())
		{
			String t = m.group(1);
			equation2 = s.substring(t.length());
		}
		if (true)
		{
			System.out.println(equation2.replaceAll("\\s", ""));
			equation = equation2.replaceAll("\\s", "");
			
			boolean expectedVar = true;
			
			boolean variable = false;
			boolean number = false;
			
			boolean expectedOperator = false;
			int depth = 0;
			String str = "";
			
			for (int i = 0; i < equation.length(); i++) 
			{
				char c = equation.charAt(i);
				
				if (expectedVar && (c == '(') )
				{
					depth++;
				}
				else if ((c == ')') )
				{
					depth--;
					if (depth < 0)
						return false;
					if (!str.equals(""))
					{
						str = "";
					}
					number = false;
					variable = false;
					expectedOperator = true;
					expectedVar = false;
				}
				else if (expectedVar && 
						(Character.isDigit(c) || (c == '.') || (c == 'e') || (c == '-' && !number)))
				{
					if (variable)
						return false;
					if(number && (c == '-' ))
						return false;
					if (c != '-')
						expectedOperator = true;
					number = true;
					
					str += Character.toString(c);
				}
				else if (expectedVar && (Character.isLetter(c)))
				{
					if (number)
						return false;
					
					expectedOperator = true;
					variable = true;
					str += Character.toString(c);
					boolean is_contained = false;
					for (String string : str_arr) {
						if (string.equals(Character.toString(c)))
							is_contained = true;
					}
					if (!is_contained)
						return false;
				}
				else if ((expectedOperator) &&
					((c == '+') || (c == '-') 
					|| (c == '*') || (c == '/')))
				{
					if (!str.equals(""))
					{
						str = "";
					}
					expectedVar = true;
					expectedOperator = false;
					number = false;
					variable = false;
				}
				else
				{
					return false;
				}
				
			}
			if (depth != 0)
				return false;
		}
		
		ht_function_arguments.put(functionName, params);
		ht_function_equation.put(functionName, equation);
		
		return true;
	}
	
	public static void Evaluate(String s) throws IndexOutOfBoundsException
	{
		Pattern pt = Pattern.compile("^\\s*DEF\\s+[a-zA-Z]+\\(([a-zA-Z]\\s*,\\s*)*([a-zA-Z])\\)\\s+(((\\(\\s*)*(([a-zA-Z]|[-]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*(\\)\\s*)*)\\s*[/*+-]\\s*)*\\s*(\\(\\s*)*([a-zA-Z]|[-]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?))\\s*(\\)\\s*)*?$");
		Matcher m = pt.matcher(s);

		if (m.find())
		{
			if (ValidateFunction(s))
				System.out.print("match");
		}
		else
		{
			String[] str_array = s.split("=");
			String sss = "";
			Double ddd = 0.0d;
			
			if (str_array.length == 1)
			{
				ddd = EvaluatePr(str_array[0]);
			}
			else if (str_array.length == 2)
			{
				sss = ValidateVariable(str_array[0]);
				ddd = EvaluatePr(str_array[1]);
				System.out.print(sss);
				System.out.print(" = ");
				
				ht_variables.put(sss, ddd);
			}
			System.out.printf("%.5f",ddd);
			System.out.println();
		}
	}
	
	public static Double EvaluatePr(String s) throws IndexOutOfBoundsException
	{
		

		ArrayList<String> expression = new ArrayList<String>();
		
		boolean expectedVar = true;
		
		boolean variable = false;
		boolean number = false;
		
		boolean expectedOperator = false;
		String str = "";

		Pattern gdouble_pattern = Pattern.compile("^\\s*([-]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*.*");
		Pattern gvar_pattern = Pattern.compile("^\\s*([a-zA-Z]+)\\s*");
		Pattern proper_operators = Pattern.compile("^\\s*([=*/+-])\\s*.*");
		Pattern l_brackets_pattern = Pattern.compile("^\\s*(\\()\\s*.*");
		Pattern r_brackets_pattern = Pattern.compile("^\\s*(\\))\\s*.*");
		Pattern funct_pattern = Pattern.compile("^([a-zA-Z]+\\((?:(?:[a-zA-Z]+|[-]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?)\\s*,\\s*)*(?:(?:[a-zA-Z]+|[-]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?))\\)).*");
		while (!s.equals("") && (!s.equals("\n"))) 
		{
			Matcher m_double = gdouble_pattern.matcher(s);
			Matcher m_operator = proper_operators.matcher(s);
			Matcher left_m_brackets = l_brackets_pattern.matcher(s);
			Matcher right_m_brackets = r_brackets_pattern.matcher(s);
			Matcher m_var = gvar_pattern.matcher(s);
			Matcher m_funct = funct_pattern.matcher(s);
			if (m_double.find() && !expectedOperator)
			{
				String expr = m_double.group(1);
				s = s.replaceFirst("^\\s*([-]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*", "");
				expression.add(expr);
				expectedOperator = true;
			}
			else if (m_funct.find())
			{
				String expr = m_funct.group(1);
				s = s.replaceFirst("^\\s*([a-zA-Z]+\\((?:(?:[a-zA-Z]+|[-]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?)\\s*,\\s*)*(?:(?:[a-zA-Z]+|[-]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?))\\))", "");
				String func_name = expr.replaceAll("\\(.*$", "");
				String func_params = expr.replaceAll("^.*\\(", "").replace("\\)", "");
				
				if (ht_function_equation.containsKey(func_name))
				{
					String ex_n = ht_function_arguments.get(func_name);
					String[] ex_o_a = ex_n.split(",");
					String[] ex_n_a = func_params.split(",");
					
					String eq_new = ht_function_equation.get(func_name);
					for (int i = 0; i < ex_n_a.length; i++) {
						eq_new = eq_new.replace(ex_o_a[i], ex_n_a[i] );
					}
					
					s = "(" + eq_new + ")" + s;
				}
				
			}
			else if (m_operator.find() && expectedOperator)
			{
				String expr = m_operator.group(1);
				s = s.replaceFirst("^\\s*([=*/+-])\\s*", "");
				expression.add(expr);
				expectedOperator = false;
			}
			else if (left_m_brackets.find() && !expectedOperator)
			{
				String expr = left_m_brackets.group(1);
				s = s.replaceFirst("^\\s*(\\()\\s*", "");
				expression.add(expr);
			}
			else if (right_m_brackets.find() && expectedOperator)
			{
				String expr = right_m_brackets.group(1);
				s = s.replaceFirst("^\\s*(\\))\\s*", "");
				expression.add(expr);
			}
			else if (m_var.find() && !expectedOperator)
			{
				String expr = m_var.group(1);
				s = s.replaceFirst("^\\s*([a-zA-Z]+)\\s*", "");
				expression.add(expr);
				expectedOperator = true;
			}
			else
			{
				throw new IndexOutOfBoundsException();
			}
			System.out.print("");
		}

		EvaluateBraces(expression);
		
		EvaluateMult(expression);
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i).equals(""))
			{
				expression.remove(i--);
				continue;
			}
		}
		
		EvaluatePlus(expression);
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i).equals(""))
			{
				expression.remove(i);
				continue;
			}
		}
		
		return Double.parseDouble(expression.get(0));
	}
	
	public static int EvaluateBraces(ArrayList<String> A) {
		return EvaluateBraces(A, 0, A.size()); 
	}
	public static int EvaluateBraces(ArrayList<String> A, int left, int right) 
	{
		int deletedItems = 0;
		
		int depth = 0;
		
		int left_n = 0;
		int right_n = A.size();// - 1;
		
		for (int i = left; i < right; i++) 
		{
			if (A.get(i).matches("[a-zA-Z]+"))
			{
				if (ht_variables.containsKey(A.get(i)))
				{
					Double d = ht_variables.get(A.get(i));
					A.set(i, d.toString());
				}
				else
				{
					A.set(i, "0");
				}
			}
			if (A.get(i).equals("(") && (depth == 0))
			{
				left_n = i;
			}
			if (A.get(i).equals(")") && (depth == 1))
			{
				right_n = i;
				int di = EvaluateBraces(A, left_n+1, right_n);
				
				deletedItems += di;
				
				right_n -= di;
				right -= di;
				
				A.remove(right_n);
				A.remove(left_n);
				right-=2;
				deletedItems+=2;
				
				for (int ii = 0; ii < A.size(); ii++) {
					if (A.get(ii).equals(""))
					{
						deletedItems++;
						right--;
						A.remove(ii--);
						continue;
					}
				}
				
				depth--;
				i = left;
				continue;
			}
			if (A.get(i).equals("(")) depth++;
			if (A.get(i).equals(")")) depth--;
		}
		
		EvaluateMult(A, left+1, right-1);
		for (int i = 0; i < A.size(); i++) {
			if (A.get(i).equals(""))
			{
				right--;
				A.remove(i--);
				deletedItems++;
				continue;
			}
		}
		
		EvaluatePlus(A, left+1, right-1);
		return deletedItems;
	}
	
	public static void EvaluateMult(ArrayList<String> A) 
	{
		EvaluateMult(A, 0, A.size());
	}
	public static void EvaluateMult(ArrayList<String> A, int left, int right) 
	{
		for (int i = left; i < right; i++) {
			String s = A.get(i);
			if (s.equals("*"))
			{
				Double d1 = Double.parseDouble(A.get(i - 1));
				Double d2 = Double.parseDouble(A.get(i + 1));
				
				Double d = d1 * d2;
				A.set(i + 1, d.toString());
				
				A.set(i, "");
				A.set(i - 1, "");
			}
			else if (s.equals("/"))
			{
				Double d1 = Double.parseDouble(A.get(i - 1));
				Double d2 = Double.parseDouble(A.get(i + 1));
				
				if (d2 == 0.0)
					return;
				
				Double d = d1 / d2;
				A.set(i + 1, d.toString());
				
				A.set(i, "");
				A.set(i - 1, "");				
			}
		}
		
	}
	
	public static void EvaluatePlus(ArrayList<String> A)
	{
		EvaluatePlus(A, 0, A.size());
	}
	
	public static void EvaluatePlus(ArrayList<String> A, int left, int right)
	{
		for (int i = left; i < right; i++) {
			String s = A.get(i);
			if (s.equals("+"))
			{
				Double d1 = Double.parseDouble(A.get(i - 1));
				Double d2 = Double.parseDouble(A.get(i + 1));
				
				Double d = d1 + d2;
				A.set(i + 1, d.toString());
				
				A.set(i, "");
				A.set(i - 1, "");
			}
			else if (s.equals("-"))
			{
				Double d1 = Double.parseDouble(A.get(i - 1));
				Double d2 = Double.parseDouble(A.get(i + 1));
				
				
				Double d = d1 - d2;
				A.set(i + 1, d.toString());
				
				A.set(i, "");
				A.set(i - 1, "");				
			}
		}
	}
	
  public static void main(String[] argv) {
	  
	  
    /* VZOR: kod, ktery opisuje data ze standardniho vstupu na standardni vystup
     * a konci pokud narazi na konec vstupu.
     *
     *
     */  try {
         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
         int c;
         String equation = "";
         while ((c = input.read()) != -1) {
        	 
        	 equation += Character.toString((char)c);
        	 
        	 if ((char)c == '\n')
        	 {
        		 try {
            		 Evaluate(equation);	
				} catch (Exception e) {
					System.out.println("CHYBA");
				}
        		 equation = "";
        	 }
        	 
        	 /*if (Character.isWhitespace((char)c))
        	 {
        		 continue;
        	 }*/
        	 
           //System.out.print((char) c);
         }
       } catch (IOException ex) {
         System.err.println("Nastala IOException");
       }
     
     //System.out.printf("%.5f", d);

  }
}

