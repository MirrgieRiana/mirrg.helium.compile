package mirrg.helium.compile.oxygen.util.apatite;

import java.util.Hashtable;

public class Frame
{

	public final Frame parent;
	private Hashtable<String, Variable> variables = new Hashtable<>();

	public Frame()
	{
		this(null);
	}

	public Frame(Frame parent)
	{
		this.parent = parent;
	}

	public Variable getVariable(String name)
	{
		Variable variable = variables.get(name);
		if (variable != null) return variable;
		if (parent == null) return null;
		return parent.getVariable(name);
	}

	public Variable define(String name, Type type)
	{
		Variable variable = new Variable(name, type);
		variables.put(name, variable);
		return variable;
	}

	public Hashtable<String, Variable> getVariables()
	{
		return getVariables(new Hashtable<>());
	}

	private Hashtable<String, Variable> getVariables(Hashtable<String, Variable> hash)
	{
		if (parent != null) parent.getVariables(hash);
		hash.putAll(variables);
		return hash;
	}

}
