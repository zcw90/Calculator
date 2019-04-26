package com.zcw.calculator.operator;

import com.zcw.calculator.utils.Constant;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 算符优先算法
 * @author ASUS
 *
 */
public class Operator {
	/**
	 * 算符优先算法支持的运算（17种）。<br>
	 * abcehijklm代表的运算，参见{@link Operator#replaceExpression(String)}
	 */
	private static final String operator = "+-*/()abcdhijklm#";

	/** 算符优先算法优先级表 */
	private static final char[][] level = {
			{'>', '>', '<', '<', '<', '>', '<', '<', '<', '<', '<', '<', '<', '<', '<', '<', '>'},
			{'>', '>', '<', '<', '<', '>', '<', '<', '<', '<', '<', '<', '<', '<', '<', '<', '>'},
			{'>', '>', '>', '>', '<', '>', '<', '<', '<', '<', '<', '<', '<', '<', '<', '<', '>'},
			{'>', '>', '>', '>', '<', '>', '<', '<', '<', '<', '<', '<', '<', '<', '<', '<', '>'},
			{'<', '<', '<', '<', '<', '=', '<', '<', '<', '<', '<', '<', '<', '<', '<', '<', ' '},
			{'>', '>', '>', '>', ' ', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'>', '>', '>', '>', '<', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>', '>'},
			{'<', '<', '<', '<', '<', ' ', '<', '<', '<', '<', '<', '<', '<', '<', '<', '<', '='},
	};

	/** 存放数字的栈 */
	private Stack<Double> numbers;
	
	/** 存放符号的栈 */
	private Stack<String> operators;

	public Operator() {
		numbers = new Stack<Double>();
		operators = new Stack<String>();
	}
	
	/**
	 * 计算函数
	 * @param expression 需要计算的四则运算表达式
	 * @return 返回计算的结果
	 */
	public Double calculator(String expression) {
		Double result = null;
		
		parseExpression(repairExpression(expression));

		if(numbers.size() == 1 && operators.size() == 0) {
			result = numbers.pop();
		}
		return result;
	}

	/**
	 * 检测表达式是否正确
	 * @param expression
	 * @return 正确，返回true；否则，返回false
     */
	public boolean checkExpression(String expression) {
		expression = repairExpression(expression);
		return parseExpression(expression);
	}

	/**
	 * 计算表达式
	 * <br />用算符优先算法计算表达式
	 * @param expression 需要解析的表达式
	 * @return 如果表达式错误，则返回false；正确，则返回true
     */
	private boolean parseExpression(String expression) {
		// 解析表达式之前，先清空数字栈和符号栈
		numbers.clean();
		operators.clean();
		operators.push("#");	// 向符号栈底入#
		expression += "#";		// 向表达式末尾添加#
		expression = replaceExpression(expression);	// 替换表达式
		
		// 用于匹配字符和数字的正则变量
		String operatoeRegex = "[\\+\\-\\*\\/\\(\\)abcdhijklm\\#]";
		String numberRegex = "[0-9]+\\.*[0-9]*(E(?!\\-))?(E\\-)?[0-9]*";
		Pattern patternNumber = Pattern.compile(numberRegex);
		Matcher matcher;
		
		// 开始解析表达式
		String sub;
		int i = 0;
		while(i < expression.length()) {
			// 判断是否为符号, 如果为符号，则用算符优先算法计算
			sub = String.valueOf(expression.charAt(i));
			if(Pattern.matches(operatoeRegex, sub)) {

				/**
				 * 处理负数，
				 * 如果"-"位于表达式的最前面，或者“-”前为“（”，则向数字栈中入栈一个0
				 */
				if(sub.equals("-") && (i == 0 || expression.charAt(i - 1) == '(')) {
					numbers.push(0d);
				}

				switch(priority(operators.getTop().charAt(0), sub.charAt(0))) {
					// 当前符号优先级大，则入栈
					case '<':
						operators.push(sub);
						i++;
						break;
					
					// 如果优先级相等，则符号出栈
					case '=':
						operators.pop();
						i++;
						break;
						
					// 当前操作符优先级小，则出栈两个操作数和一个操作符计算，并将计算结果入栈
					case '>':
						String operator = operators.pop();
						Double value1 = numbers.pop();

						// 如果出栈的运算符为空，则表达式错误
						if(operator == null)
							return false;

						// 进行log、ln等一目运算
						if(Pattern.matches(Constant.OPERATOR_REGEX1, operator)) {
							if(value1 == null)
								return false;

							numbers.push(calculator(value1, operator));
						}

						// 进行+-*/运算
						if(Pattern.matches(Constant.OPERATOR_REGEX2, operator)) {
							Double value2 = numbers.pop();

							// 如果出栈的数据为null，则说明表达式错误
							if(value1 == null || value2 == null) {
								return false;
							}
							numbers.push(calculator(value1, value2, operator));
						}
						break;

					case ' ':
						return false;
				}
				continue;
			}
			
			// 判断是否为数字
			sub = expression.substring(i);
			matcher = patternNumber.matcher(sub);
			if(matcher.find()) {
				sub = matcher.group(0);
				push(sub, 2);
			}
			i += sub.length();
		}

		return true;
	}
	
	/**
	 * 把表达式中的数据入栈
	 * 如果是符号，则入符号栈；如果是数字，则入数字栈。
	 * @param data 要入栈的数据
	 * @param type 标示入栈的数据是字符还是数字。1标示字符，2标示数字
	 */
	private void push(String data, int type) {
		switch (type) {
		case 1:
			operators.push(data);
			break;
			
		case 2:
			numbers.push(Double.valueOf(data));
			break;
		}
	}
	
	/**
	 * 比较栈定符号和当前符号的优先级
	 * @param stackTop	栈顶符号
	 * @param current	当前符号
	 * @return 根据优先级表，返回优先级
	 */
	private char priority(char stackTop, char current) {
		int rows = operator.indexOf(stackTop);
		int cols = operator.indexOf(current);
		return level[rows][cols];
	}
	
	/**
	 * 计算函数（二目运算）
	 * @param value1
	 * @param value2
	 * @param operator 要计算的类型
	 * @return 返回计算结果
	 */
	private double calculator(Double value1, Double value2, String operator) {
		Double result = new Double(0);

		if(operator.equals("+")) {
			result = value2 + value1;
		}
		else if(operator.equals("-")) {
			result = value2 - value1;
		}
		else if(operator.equals("*")) {
			result = multiply(value1, value2);
		}
		else if(operator.equals("/")) {
			result = value2 / value1;
		}
		else if(operator.equals("d")) {
			result = StrictMath.pow(value2, value1);
		}
		
		return result;
	}

	/**
	 * 计算函数（一目运算）
	 * @param value1
	 * @param operator 要计算的类型
	 * @return 返回计算结果
	 */
	private double calculator(Double value1, String operator) {
		Double result = new Double(0);

		// log运算
		if(operator.equals("a")) {
			result = Math.log10(value1);
		}
		// ln运算
		else if(operator.equals("b")) {
			result = Math.log(value1);
		}
		// 开根号
		else if(operator.equals("c")) {
			result  = Math.sqrt(value1);
		}
		// sin
		else if(operator.equals("h")) {
			result = Math.sin(Math.PI / 180 * value1);
		}
		// cos
		else if(operator.equals("i")) {
			result = Math.cos(Math.PI / 180 * value1);
		}
		// tan
		else if(operator.equals("j")) {
			result = Math.tan(Math.PI / 180 * value1);
		}
		// sinh
		else if(operator.equals("k")) {
			result = Math.sinh(value1);
		}
		// cosh
		else if(operator.equals("l")) {
			result = Math.cosh(value1);
		}
		// tanh
		else if(operator.equals("m")) {
			result = Math.tanh(value1);
		}

		return result;
	}

	/**
	 * 乘法，用BigDecimal处理
	 * @param value1 乘数
	 * @param value2 乘数
     * @return
     */
	private Double multiply(Double value1, Double value2) {
		Double result = new Double(0);

		// 处理乘数中有无穷大和NaN的情况
		if(value1 == Double.NEGATIVE_INFINITY || value2 == Double.NEGATIVE_INFINITY) {
			result = Double.NEGATIVE_INFINITY;
		}
		else if(value1 == Double.POSITIVE_INFINITY || value2 == Double.POSITIVE_INFINITY) {
			result = Double.POSITIVE_INFINITY;
		}
		else if(value1.isNaN() || value2.isNaN()) {
			result = Double.NaN;
		}
		else {
			/**
			 * 利用BigDecimal处理乘法
			 * 如果用double处理乘法，计算0.84569 * 95、11540 * 0.35等时，会出现精度不准确
			 * 构造BigDecimal对象，必须用BigDecimal(String)构造函数，用BigDecimal(double)构造函数，一样会存在精度不准确
			 */
			BigDecimal val1 = new BigDecimal(String.valueOf(value1));
			BigDecimal val2 = new BigDecimal(String.valueOf(value2));
			result = val2.multiply(val1).doubleValue();
		}
		return result;
	}

	/**
	 * 修复表达式
	 * @param expression 需要修复的表达式
	 * @return 返回修复后的表达式
     */
	private String repairExpression(String expression) {
		if(expression.length() < 1)
			return "";

		// 加减乘除替换成正确的符号
		expression = expression.replaceAll("＋", "+");
		expression = expression.replaceAll("－", "-");
		expression = expression.replaceAll("×", "*");
		expression = expression.replaceAll("÷", "/");
		expression = expression.replaceAll(",", "");

		char ch = expression.charAt(expression.length() - 1);

		// 如果表达式以+/-结束，则在末尾添加0
		// 如果表达式以*//结束，则在末尾添加1
		switch (ch) {
			case '+':
			case '-':
				expression += "0";
				break;

			case '*':
			case '/':
				expression += "1";
				break;
		}

		// 统计左括号和右括号的个数
		int lBracket = 0, rBracket = 0;
		for(int i = 0; i < expression.length(); i++) {
			ch = expression.charAt(i);
			if(ch == '(') {
				lBracket++;
			}
			if(ch == ')') {
				rBracket++;
			}
		}

		// 如果右括号的个数少于左括号，则补全右括号
		if(rBracket < lBracket) {
			for(int i = 0; i < lBracket - rBracket; i++)
				expression += ")";
		}

		return expression;
	}

	/**
	 *	表达式替换
	 * <br />把表达式中的“log”，替换成“a”
	 * <br />把表达式中的“ln”，替换成“b”
	 * <br />把表达式中的“√”，替换成“c”
	 * <br />把表达式中的“^”，替换成“d”
	 * <br />把表达式中的“sin”，替换成“h”
	 * <br />把表达式中的“cos”，替换成“i”
	 * <br />把表达式中的“tan”，替换成“j”
	 * <br />把表达式中的“sinh”，替换成“k”
	 * <br />把表达式中的“cosh”，替换成“l”
	 * <br />把表达式中的“tanh”，替换成“m”
	 * <br />把表达式中的“π”，替换成Double中的π
	 * <br />把表达式中的“e”，替换成Double中的e
	 * @param expression
	 * @return
     */
	private String replaceExpression(String expression) {
		expression = expression.replaceAll("log", "a");
		expression = expression.replaceAll("ln", "b");
		expression = expression.replaceAll("√", "c");
		expression = expression.replaceAll("\\^", "d");
		expression = expression.replaceAll("sin(?!h)", "h");
		expression = expression.replaceAll("cos(?!h)", "i");
		expression = expression.replaceAll("tan(?!h)", "j");
		expression = expression.replaceAll("sinh", "k");
		expression = expression.replaceAll("cosh", "l");
		expression = expression.replaceAll("tanh", "m");
		expression = expression.replaceAll("π", String.valueOf(Math.PI));
		expression = expression.replaceAll("e", String.valueOf(Math.E));

		return expression;
	}
}
