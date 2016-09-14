package mirrg.helium.compile.oxygen.parser.test2;

public class Variable
{

	public EnumType<?> type;
	public String identifier;

	public Variable(EnumType<?> type, String identifier)
	{
		this.type = type;
		this.identifier = identifier;
	}

	public Object get(Runtime runtime)
	{
		Object value;

		value = runtime.variables.get(identifier);
		if (value != null) return value;

		value = runtime.vm.variables.get(identifier);
		if (value != null) return value;

		throw new NullPointerException();
	}

}
