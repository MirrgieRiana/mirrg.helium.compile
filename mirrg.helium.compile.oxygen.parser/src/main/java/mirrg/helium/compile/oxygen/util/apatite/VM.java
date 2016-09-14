package mirrg.helium.compile.oxygen.util.apatite;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class VM
{

	private RegistryOperator registryOperator = new RegistryOperator();
	private RegistryVariable registryVariable = new RegistryVariable();

	private Hashtable<String, Object> variables = new Hashtable<>();

	public <A, B, R> void registerOperator(String token, BiFunction<A, B, R> function, Type<A> left, Type<B> right, Type<R> result)
	{
		registryOperator.register(new Operator(token, (a, b) -> function.apply((A) a, (B) b), left, right, result));
	}

	public <T> void registerConstant(Type<T> type, String identifier, T value)
	{
		registryVariable.register(new Variable(type, identifier));
		variables.put(identifier, value);
	}

	public ArrayList<Operator> getOperator(String token, Type<?> left, Type<?> right)
	{
		return registryOperator.get(token, left, right);
	}

	public Optional<Variable> getVariable(String identifier)
	{
		return registryVariable.get(identifier);
	}

	public Object getVariableContent(String identifier)
	{
		return variables.get(identifier);
	}

	public Stream<Variable> getVariables()
	{
		return registryVariable.getVariables();
	}

	public Runtime createRuntime()
	{
		return new Runtime(this);
	}

}
