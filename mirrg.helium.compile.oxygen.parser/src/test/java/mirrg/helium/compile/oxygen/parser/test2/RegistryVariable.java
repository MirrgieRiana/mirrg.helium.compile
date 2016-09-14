package mirrg.helium.compile.oxygen.parser.test2;

import java.util.ArrayList;
import java.util.Optional;

public class RegistryVariable
{

	public ArrayList<Variable> variables = new ArrayList<>();

	public void register(Variable variable)
	{
		variables.add(variable);
	}

	public Optional<Variable> get(String identifier)
	{
		return variables.stream()
			.filter(v -> v.identifier.equals(identifier))
			.findFirst();
	}

}
