package mirrg.helium.compile.oxygen.util.apatite;

public class Variable
{

	public final String name;
	public final Type type;
	private Object value;

	public Variable(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}

	public Object get()
	{
		if (value == null) throw new RuntimeException("Uninitialized Variable: " + name);
		return value;
	}

	public void set(Object value)
	{
		if (this.value != null) throw new RuntimeException("Illegal Override: " + name);
		this.value = value;
	}

}
