package mirrg.helium.compile.oxygen.util.apatite;

import java.awt.Color;
import java.util.function.Function;

public class BaseType
{

	protected String name;
	protected Function<Type, Color> functionColor;
	protected Function<Type, Type> functionParent;
	protected Function<Object, Object> caster;

	public BaseType(String name)
	{
		this.name = name;
	}

	public BaseType setColorFunction(Function<Type, Color> functionColor)
	{
		this.functionColor = functionColor;
		return this;
	}

	public BaseType setParentFunction(Function<Type, Type> functionParent)
	{
		this.functionParent = functionParent;
		return this;
	}

	public BaseType setCaster(Function<Object, Object> caster)
	{
		this.caster = caster;
		return this;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public Type apply(Type... arguments)
	{
		return new Type(this, arguments);
	}

}
