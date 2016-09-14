package mirrg.helium.compile.oxygen.util.apatite;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class RegistryVariable
{

	private ArrayList<Variable> variables = new ArrayList<>();

	public void register(Variable variable)
	{
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).isSameSignature(variable)) {
				variables.set(i, variable);
				return;
			}
		}

		variables.add(variable);
	}

	public Optional<Variable> get(String identifier)
	{
		return variables.stream()
			.filter(v -> v.identifier.equals(identifier))
			.findFirst();
	}

	public Stream<Variable> getVariables()
	{
		return variables.stream();
	}

}
