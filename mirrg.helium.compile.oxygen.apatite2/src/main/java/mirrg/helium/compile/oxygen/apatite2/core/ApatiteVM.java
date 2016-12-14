package mirrg.helium.compile.oxygen.apatite2.core;

import java.util.ArrayList;
import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.type.IType;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class ApatiteVM
{

	private ArrayList<Tuple3<Integer, Integer, String>> errors = new ArrayList<>();

	public void reportError(int begin, int end, String string)
	{
		errors.add(new Tuple3<>(begin, end, string));
	}

	public ArrayList<Tuple3<Integer, Integer, String>> getErrors()
	{
		return errors;
	}

	//

	private ArrayList<Tuple<String, IApatiteMetaFunctionProvider>> metaFunctions = new ArrayList<>();

	public void registerMetaFunction(IApatiteMetaFunctionProvider metaFunction, String... names)
	{
		for (String name : names) {
			metaFunctions.add(new Tuple<>(name, metaFunction));
		}
	}

	public ArrayList<Tuple<String, IApatiteMetaFunctionProvider>> getMetaFunctions()
	{
		return metaFunctions;
	}

	public Optional<IApatiteMetaFunctionEntity> getMetaFunction(String name, IApatiteCode[] codes)
	{
		return getMetaFunctions().stream()
			.filter(t -> t.getX().equals(name))
			.map(t -> t.getY().matches(codes))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();
	}

	//

	private ArrayList<Tuple<String, IApatiteFunctionProvider>> functions = new ArrayList<>();

	public void registerFunction(IApatiteFunctionProvider function, String... names)
	{
		for (String name : names) {
			functions.add(new Tuple<>(name, function));
		}
	}

	public ArrayList<Tuple<String, IApatiteFunctionProvider>> getFunctions()
	{
		return functions;
	}

	public Optional<IApatiteFunctionEntity> getFunction(String name, IType<?>[] types)
	{
		return getFunctions().stream()
			.filter(t -> t.getX().equals(name))
			.map(t -> t.getY().matches(types))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted((a, b) -> a.getX() != b.getX() ? a.getX() - b.getX() : a.getY() - b.getY())
			.findFirst()
			.map(Tuple3::getZ);
	}

	//

	private ArrayList<ApatiteVariable<?>> variables = new ArrayList<>();

	public void registerVariable(ApatiteVariable<?> variable)
	{
		variables.add(variable);
	}

	public ArrayList<ApatiteVariable<?>> getVariables()
	{
		return variables;
	}

	//

	private ArrayList<ApatiteConstant<?>> constants = new ArrayList<>();

	public void registerConstant(ApatiteConstant<?> constant)
	{
		constants.add(constant);
	}

	public ArrayList<ApatiteConstant<?>> getConstants()
	{
		return constants;
	}

}
