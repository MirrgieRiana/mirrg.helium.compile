package mirrg.helium.compile.oxygen.util.apatite;

import java.util.Hashtable;
import java.util.function.BiFunction;

public class VM
{

	public RegistryOperator registryOperator = new RegistryOperator();
	public RegistryVariable registryVariable = new RegistryVariable();

	public Hashtable<String, Object> variables = new Hashtable<>();

	public <A, B, R> void registerOperator(String token, BiFunction<A, B, R> function, Type<A> left, Type<B> right, Type<R> result)
	{
		registryOperator.register(new Operator(token, (a, b) -> function.apply((A) a, (B) b), left, right, result));
	}

	public <T> void registerConstant(Type<T> type, String identifier, T value)
	{
		registryVariable.register(new Variable(type, identifier));
		variables.put(identifier, value);
	}

	public Runtime createRuntime()
	{
		return new Runtime(this);
	}

}
