package mirrg.helium.compile.oxygen.parser.test2;

import java.util.Hashtable;
import java.util.function.BiFunction;

public class VM
{

	public RegistryOperator registryOperator = new RegistryOperator();
	public RegistryVariable registryVariable = new RegistryVariable();

	public Hashtable<String, Object> variables = new Hashtable<>();

	public VM()
	{
		registerOperator("+", (a, b) -> a + b, EnumType.STRING, EnumType.OBJECT, EnumType.STRING);
		registerOperator("+", (a, b) -> a + b, EnumType.OBJECT, EnumType.STRING, EnumType.STRING);
		registerOperator("+", (a, b) -> a + b, EnumType.STRING, EnumType.STRING, EnumType.STRING);

		registerOperator("+", (a, b) -> a + b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.DOUBLE);
		registerOperator("+", (a, b) -> a + b, EnumType.INTEGER, EnumType.INTEGER, EnumType.INTEGER);
		registerOperator("-", (a, b) -> a - b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.DOUBLE);
		registerOperator("-", (a, b) -> a - b, EnumType.INTEGER, EnumType.INTEGER, EnumType.INTEGER);
		registerOperator("*", (a, b) -> a * b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.DOUBLE);
		registerOperator("*", (a, b) -> a * b, EnumType.INTEGER, EnumType.INTEGER, EnumType.INTEGER);
		registerOperator("/", (a, b) -> a / b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.DOUBLE);
		registerOperator("/", (a, b) -> a / b, EnumType.INTEGER, EnumType.INTEGER, EnumType.INTEGER);
		registerOperator("%", (a, b) -> a % b, EnumType.INTEGER, EnumType.INTEGER, EnumType.INTEGER);

		registerOperator("<=", (a, b) -> a <= b, EnumType.INTEGER, EnumType.INTEGER, EnumType.BOOLEAN);
		registerOperator("<=", (a, b) -> a <= b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.BOOLEAN);
		registerOperator(">=", (a, b) -> a >= b, EnumType.INTEGER, EnumType.INTEGER, EnumType.BOOLEAN);
		registerOperator(">=", (a, b) -> a >= b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.BOOLEAN);
		registerOperator("<", (a, b) -> a < b, EnumType.INTEGER, EnumType.INTEGER, EnumType.BOOLEAN);
		registerOperator("<", (a, b) -> a < b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.BOOLEAN);
		registerOperator(">", (a, b) -> a > b, EnumType.INTEGER, EnumType.INTEGER, EnumType.BOOLEAN);
		registerOperator(">", (a, b) -> a > b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.BOOLEAN);

		registerOperator("==", (a, b) -> a == b, EnumType.INTEGER, EnumType.INTEGER, EnumType.BOOLEAN);
		registerOperator("==", (a, b) -> a == b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.BOOLEAN);
		registerOperator("==", (a, b) -> a == b, EnumType.BOOLEAN, EnumType.BOOLEAN, EnumType.BOOLEAN);
		registerOperator("==", (a, b) -> a.equals(b), EnumType.OBJECT, EnumType.OBJECT, EnumType.BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, EnumType.INTEGER, EnumType.INTEGER, EnumType.BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, EnumType.DOUBLE, EnumType.DOUBLE, EnumType.BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, EnumType.BOOLEAN, EnumType.BOOLEAN, EnumType.BOOLEAN);
		registerOperator("!=", (a, b) -> !a.equals(b), EnumType.OBJECT, EnumType.OBJECT, EnumType.BOOLEAN);

		registerOperator("&&", (a, b) -> a && b, EnumType.BOOLEAN, EnumType.BOOLEAN, EnumType.BOOLEAN);
		registerOperator("||", (a, b) -> a || b, EnumType.BOOLEAN, EnumType.BOOLEAN, EnumType.BOOLEAN);

		registerOperator("+", (a, b) -> a.add(b), EnumType.COMPLEX, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("-", (a, b) -> a.subtract(b), EnumType.COMPLEX, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("*", (a, b) -> a.multiply(b), EnumType.COMPLEX, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("/", (a, b) -> a.divide(b), EnumType.COMPLEX, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("+", (a, b) -> a.add(b), EnumType.COMPLEX, EnumType.DOUBLE, EnumType.COMPLEX);
		registerOperator("-", (a, b) -> a.subtract(b), EnumType.COMPLEX, EnumType.DOUBLE, EnumType.COMPLEX);
		registerOperator("*", (a, b) -> a.multiply(b), EnumType.COMPLEX, EnumType.DOUBLE, EnumType.COMPLEX);
		registerOperator("/", (a, b) -> a.divide(b), EnumType.COMPLEX, EnumType.DOUBLE, EnumType.COMPLEX);
		registerOperator("+", (a, b) -> b.add(a), EnumType.DOUBLE, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("-", (a, b) -> b.subtract(a), EnumType.DOUBLE, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("*", (a, b) -> b.multiply(a), EnumType.DOUBLE, EnumType.COMPLEX, EnumType.COMPLEX);
		registerOperator("/", (a, b) -> b.divide(a), EnumType.DOUBLE, EnumType.COMPLEX, EnumType.COMPLEX);

		registerConstant(EnumType.DOUBLE, "pi", Math.PI);
		registerConstant(EnumType.DOUBLE, "e", Math.E);
		registerConstant(EnumType.BOOLEAN, "true", true);
		registerConstant(EnumType.BOOLEAN, "false", false);
		registerConstant(EnumType.STRING, "lineSeparator", System.lineSeparator());
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
