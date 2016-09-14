package mirrg.helium.compile.oxygen.parser.test2;

public class Variable<T>
{

	public EnumType<T> type;
	public String identifier;

	public Variable(EnumType<T> type, String identifier)
	{
		this.type = type;
		this.identifier = identifier;
	}

	public T get(Runtime runtime)
	{
		T value = (T) runtime.variables.get(identifier);
		if (value != null) return value;

		value = (T) runtime.vm.variables.get(identifier);
		return value;
	}

}
