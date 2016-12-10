package mirrg.helium.compile.oxygen.apatite2;

import java.util.ArrayList;
import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.standard.hydrogen.struct.Struct3;
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

	private ArrayList<IApatiteMetaFunctionProvider> metaFunctions = new ArrayList<>();

	public void registerMetaFunction(IApatiteMetaFunctionProvider metaFunction)
	{
		metaFunctions.add(metaFunction);
	}

	public ArrayList<IApatiteMetaFunctionProvider> getMetaFunctions()
	{
		return metaFunctions;
	}

	//

	private ArrayList<IApatiteFunctionProvider> functions = new ArrayList<>();

	public void registerFunction(IApatiteFunctionProvider function)
	{
		functions.add(function);
	}

	public ArrayList<IApatiteFunctionProvider> getFunctions()
	{
		return functions;
	}

	public Optional<IApatiteFunctionEntity> getFunction(String name, Type<?>[] types)
	{
		return getFunctions().stream()
			.filter(f -> f.getName().equals(name))
			.map(f -> f.matches(types))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted((a, b) -> a.x != b.x ? a.x - b.x : a.y - b.y)
			.findFirst()
			.map(Struct3::getZ);
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
