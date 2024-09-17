// Starter code for Project 1

// VSW230001
package com.company;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Stack;
import java.util.*;

/** Class to store a node of expression tree
 For each internal node, element contains a binary operator
 List of operators: +|*|-|/|%|^
 Other tokens: (|)
 Each leaf node contains an operand (long integer)
 */

public class Expression {
	public enum TokenType {  // NIL is a special token that can be used to mark bottom of stack
		PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER
	}

	public static class Token {
		TokenType token;
		int priority; // for precedence of operator
		Long number;  // used to store number of token = NUMBER
		String string;

		Token(TokenType op, int pri, String tok) {
			token = op;
			priority = pri;
			number = null;
			string = tok;
		}

		// Constructor for number.  To be called when other options have been exhausted.
		Token(String tok) {
			token = TokenType.NUMBER;
			number = Long.parseLong(tok);
			string = tok;
		}

		boolean isOperand() {
			return token == TokenType.NUMBER;
		}

		public long getValue() {
			return isOperand() ? number : 0;
		}

		public String toString() {
			return string;
		}
	}

	Token element;
	Expression left, right;

	// Create token corresponding to a string
	// tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
	// NUMBER is either "0" or "[-]?[1-9][0-9]*
	static Token getToken(String tok) {  // To do
		Token result;
		switch(tok) {
			case "+":
				result = new Token(TokenType.PLUS, 1, tok);  // modify if priority of "+" is not 1
				break;
			// Complete rest of this method
			case "-":
				result = new Token(TokenType.MINUS, 1, tok);
				break;
			case "*":
				result = new Token(TokenType.TIMES, 2, tok);
				break;
			case "/":
				result = new Token(TokenType.DIV, 2, tok);
				break;
			case "%":
				result = new Token(TokenType.MOD, 2, tok);
				break;
			case "^":
				result = new Token(TokenType.POWER, 3, tok);
				break;
			case "(":
				result = new Token(TokenType.OPEN, 4, tok); // OPEN PRIORITY 4
				break;
			case ")":
				result = new Token(TokenType.CLOSE, 5, tok); // CLOSE PRIORITY 5
				break;
			default:
				result = new Token(tok);
				break;
		}
		return result;
	}

	private Expression() {
		element = null;
	}

	private Expression(Token oper, Expression left, Expression right) {
		this.element = oper;
		this.left = left;
		this.right = right;
	}

	private Expression(Token num) {
		this.element = num;
		this.left = null;
		this.right = null;
	}

	// Given a list of tokens corresponding to an infix expression,
	// return the expression tree corresponding to it.
	public static Expression infixToExpression(List<Token> exp) {  // To do
		Stack<Expression> expStk = new Stack<>();
		Stack<Token> tokStk = new Stack<>();

		for (Token t : exp) {
			if (t.isOperand()) {
				expStk.push(new Expression(t));
			} else if (t.token == TokenType.OPEN) {
				tokStk.push(t);
			} else if (t.token == TokenType.CLOSE) {
				while (!tokStk.isEmpty() && tokStk.peek().token != TokenType.OPEN) {
					Token operator = tokStk.pop();
					Expression right = expStk.pop();
					Expression left = expStk.pop();
					expStk.push(new Expression(operator, left, right));
				}
				tokStk.pop();
			} else {
				while (!tokStk.isEmpty() && tokStk.peek().priority >= t.priority) {
					Token operator = tokStk.pop();
					Expression right = expStk.pop();
					Expression left = expStk.pop();
					expStk.push(new Expression(operator, left, right));
				}
				tokStk.push(t);
			}
		}
		while (!tokStk.isEmpty()) {
			Token operator = tokStk.pop();
			Expression right = expStk.pop();
			Expression left = expStk.pop();
			expStk.push(new Expression(operator, left, right));
		}
		return expStk.pop();

	}

	// Given a list of tokens corresponding to an infix expression,
	// return its equivalent postfix expression as a list of tokens.
	public static List<Token> infixToPostfix(List<Token> exp) {  // To do
		List<Token> output = new ArrayList<Token>();
		Stack<Token> stack = new Stack<Token>();
		stack.push(new Token(TokenType.NIL, 0, null));
		for(Token t : exp) {
			if(t.isOperand()) {
				output.add(t);
			}
			else if(t.priority < 4) {
				while((stack.peek().priority != 4) && (stack.peek().priority != 0) && (stack.peek().priority >= t.priority)) {
					output.add(stack.pop());
				}
				stack.push(t);
			}
			else if(t.priority == 4) {
				stack.push(t);
			}
			else if(t.priority == 5) {
				while((stack.peek().priority != 4)) {
					output.add(stack.pop());
				}
				stack.pop();
			}
		}
		while(stack.peek().priority != 0) {
			output.add(stack.pop());
		}

		return output;
	}

	// Given a postfix expression, evaluate it and return its value.
	public static long evaluatePostfix(List<Token> exp) {  // To do
		Stack<Token> stk = new Stack<Token>();
		for (Token t : exp) {
			if (t.isOperand()) {
				stk.push(t);
			} else if (!t.isOperand()) {
				long temp1 = stk.pop().getValue();
				long temp2 = stk.pop().getValue();
				long result = operate(t.token, temp2, temp1);
				stk.push(new Token(result + ""));
			}
		}
		return stk.peek().getValue();
	}

	// Given an expression tree, evaluate it and return its value.
	public static long evaluateExpression(Expression tree) {  // To do
		if(tree.element.isOperand()) {
			return tree.element.getValue();
		}
		long leftVal = evaluateExpression(tree.left);
		long rightVal = evaluateExpression(tree.right);
		return operate(tree.element.token, leftVal, rightVal);
	}

	//helper method
	public static long operate(TokenType t, long a, long b) {
		long result = 0;
		switch(t) {
			case PLUS:
				result = a + b;
				break;
			case MINUS:
				result = a - b;
				break;
			case TIMES:
				result = a*b;
				break;
			case DIV:
				if (b == 0) {
					throw new ArithmeticException("div by 0");
				}
				result = a/b;
				break;
			case MOD:
				if (b == 0) {
					throw new ArithmeticException("div by 0");
				}
				result = a%b;
				break;
			case POWER:
				result = a^b;
				break;
		}
		return result;
	}

	// sample main program for testing
	public static void main(String[] args) throws FileNotFoundException {

		Scanner in;

		File inputFile = new File("/Users/veerwaje/Downloads/p1 testcases.txt");
		in = new Scanner(inputFile);

		int count = 0;
		while(in.hasNext()) {
			String s = in.nextLine();
			List<Token> infix = new LinkedList<>();
			Scanner sscan = new Scanner(s);
			int  len = 0;
			while(sscan.hasNext()) {
				infix.add(getToken(sscan.next()));
				len++;
			}
			if(len > 0) {
				count++;
				System.out.println("Expression number: " + count);
				System.out.println("Infix expression: " + infix);
				Expression exp = infixToExpression(infix);
				List<Token> post = infixToPostfix(infix);
				System.out.println("Postfix expression: " + post);
				long pval = evaluatePostfix(post);
				long eval = evaluateExpression(exp);
				System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
			}
		}
	}
}
