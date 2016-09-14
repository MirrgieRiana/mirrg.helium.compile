package mirrg.helium.compile.oxygen.util.apatite;

public class Variable
{

	public Type<?> type;
	public String identifier;

	public Variable(Type<?> type, String identifier)
	{
		this.type = type;
		this.identifier = identifier;
	}

	public Object get(Runtime runtime)
	{
		Object value;

		value = runtime.variables.get(identifier);
		if (value != null) return value;

		value = runtime.vm.getVariableContent(identifier);
		if (value != null) return value;

		throw new NullPointerException();
	}

	public boolean isSameSignature(Variable other)
	{
		if (!identifier.equals(other.identifier)) return false;
		return true;
	}

}
