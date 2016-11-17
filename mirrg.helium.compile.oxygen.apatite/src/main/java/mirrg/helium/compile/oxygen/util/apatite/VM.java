package mirrg.helium.compile.oxygen.util.apatite;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class VM
{

	public final WrapperType<Object> OBJECT;
	public final WrapperType<BaseType> BASETYPE;

	public VM()
	{
		OBJECT = new WrapperType<>(new BaseType("Object").apply());
		BASETYPE = new WrapperType<>(new BaseType("BaseType").apply());

		registerConstant(BASETYPE, "Object", OBJECT.type.baseType);
		registerConstant(BASETYPE, "BaseType", BASETYPE.type.baseType);

		BASETYPE.type.baseType.setParentFunction(t -> OBJECT.type);
	}

	//

	public final Frame frameRoot = new Frame();

	public BaseType getBaseType(String name)
	{
		return (BaseType) frameRoot.getVariable(name).get();
	}

	public BaseType registerBaseType(String name)
	{
		BaseType baseType = new BaseType(name);
		frameRoot.define(name, BASETYPE.type).set(baseType);
		return baseType;
	}

	//

	public Frame frame = frameRoot;

	public <T> void registerConstant(WrapperType<T> type, String identifier, T value)
	{
		frame.define(identifier, type.type).set(value);
	}

	public void pushFrame()
	{
		frame = new Frame(frame);
	}

	public void popFrame()
	{
		frame = frame.parent;
	}

	//

	private final RegistryOperator registryOperator = new RegistryOperator();

	public <A, B, R> void registerOperator(String token, BiFunction<A, B, R> function, WrapperType<A> left, WrapperType<B> right, WrapperType<R> result)
	{
		registryOperator.register(new Operator(token, (a, b) -> function.apply((A) a, (B) b), left.type, right.type, result.type));
	}

	public ArrayList<Operator> getOperator(String token, Type left, Type right)
	{
		return registryOperator.get(token, left, right);
	}

	//

	public Runtime createRuntime()
	{
		return new Runtime(this);
	}

}
