package mirrg.helium.compile.oxygen.parser.test2;

import java.awt.Color;
import java.util.Hashtable;
import java.util.function.BiFunction;

import org.apache.commons.math3.complex.Complex;

public class VM
{

	public static final EnumType<Object> OBJECT = new EnumType<>("OBJECT", Color.decode("#545454"));
	public static final EnumType<String> STRING = new EnumType<>("STRING", Color.decode("#a64eaa"));
	public static final EnumType<Integer> INTEGER = new EnumType<>("INTEGER", Color.decode("#424aaf"));
	public static final EnumType<Double> DOUBLE = new EnumType<>("DOUBLE", Color.decode("#53a540"));
	public static final EnumType<Boolean> BOOLEAN = new EnumType<>("BOOLEAN", Color.decode("#878334"));
	public static final EnumType<Complex> COMPLEX = new EnumType<>("COMPLEX", Color.decode("#960002"));

	public RegistryOperator registryOperator = new RegistryOperator();
	public RegistryVariable registryVariable = new RegistryVariable();

	public Hashtable<String, Object> variables = new Hashtable<>();

	public VM()
	{
		registerOperator("+", (a, b) -> a + b, STRING, OBJECT, STRING);
		registerOperator("+", (a, b) -> a + b, OBJECT, STRING, STRING);
		registerOperator("+", (a, b) -> a + b, STRING, STRING, STRING);

		registerOperator("+", (a, b) -> a + b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("+", (a, b) -> a + b, INTEGER, INTEGER, INTEGER);
		registerOperator("-", (a, b) -> a - b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("-", (a, b) -> a - b, INTEGER, INTEGER, INTEGER);
		registerOperator("*", (a, b) -> a * b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("*", (a, b) -> a * b, INTEGER, INTEGER, INTEGER);
		registerOperator("/", (a, b) -> a / b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("/", (a, b) -> a / b, INTEGER, INTEGER, INTEGER);
		registerOperator("%", (a, b) -> a % b, INTEGER, INTEGER, INTEGER);

		registerOperator("<=", (a, b) -> a <= b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("<=", (a, b) -> a <= b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator(">=", (a, b) -> a >= b, INTEGER, INTEGER, BOOLEAN);
		registerOperator(">=", (a, b) -> a >= b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator("<", (a, b) -> a < b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("<", (a, b) -> a < b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator(">", (a, b) -> a > b, INTEGER, INTEGER, BOOLEAN);
		registerOperator(">", (a, b) -> a > b, DOUBLE, DOUBLE, BOOLEAN);

		registerOperator("==", (a, b) -> a == b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("==", (a, b) -> a == b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator("==", (a, b) -> a == b, BOOLEAN, BOOLEAN, BOOLEAN);
		registerOperator("==", (a, b) -> a.equals(b), OBJECT, OBJECT, BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, BOOLEAN, BOOLEAN, BOOLEAN);
		registerOperator("!=", (a, b) -> !a.equals(b), OBJECT, OBJECT, BOOLEAN);

		registerOperator("&&", (a, b) -> a && b, BOOLEAN, BOOLEAN, BOOLEAN);
		registerOperator("||", (a, b) -> a || b, BOOLEAN, BOOLEAN, BOOLEAN);

		registerOperator("+", (a, b) -> a.add(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("-", (a, b) -> a.subtract(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("*", (a, b) -> a.multiply(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("/", (a, b) -> a.divide(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("+", (a, b) -> a.add(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("-", (a, b) -> a.subtract(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("*", (a, b) -> a.multiply(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("/", (a, b) -> a.divide(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("+", (a, b) -> b.add(a), DOUBLE, COMPLEX, COMPLEX);
		registerOperator("-", (a, b) -> b.subtract(a), DOUBLE, COMPLEX, COMPLEX);
		registerOperator("*", (a, b) -> b.multiply(a), DOUBLE, COMPLEX, COMPLEX);
		registerOperator("/", (a, b) -> b.divide(a), DOUBLE, COMPLEX, COMPLEX);

		registerConstant(DOUBLE, "pi", Math.PI);
		registerConstant(DOUBLE, "e", Math.E);
		registerConstant(BOOLEAN, "true", true);
		registerConstant(BOOLEAN, "false", false);
		registerConstant(STRING, "lineSeparator", System.lineSeparator());
	}

	public <A, B, R> void registerOperator(String token, BiFunction<A, B, R> function, EnumType<A> left, EnumType<B> right, EnumType<R> result)
	{
		registryOperator.register(new Operator(token, (a, b) -> function.apply((A) a, (B) b), left, right, result));
	}

	public <T> void registerConstant(EnumType<T> type, String identifier, T value)
	{
		registryVariable.register(new Variable(type, identifier));
		variables.put(identifier, value);
	}

	public Runtime createRuntime()
	{
		return new Runtime(this);
	}

}
