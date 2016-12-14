package mirrg.helium.compile.oxygen.apatite2.nodes;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.ApatiteLoader;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteConstant;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVariable;
import mirrg.helium.compile.oxygen.apatite2.node.ApatiteCodeBase;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.apatite2.type.Type;

public class NodeIdentifier extends ApatiteCodeBase
{

	public String name;

	public NodeIdentifier(String name, int begin, int end)
	{
		super(begin, end);
		this.name = name;
	}

	@Override
	public Optional<IApatiteScript> validateImpl(ApatiteVM vm)
	{
		Optional<ApatiteConstant<?>> oConstant = vm.getConstants().stream()
			.filter(f -> {
				if (!name.equals(f.name)) return false;
				return true;
			})
			.findFirst();

		if (oConstant.isPresent()) {
			return success(oConstant.get());
		} else {
			return success(ApatiteLoader.KEYWORD, () -> name);
		}
	}

	@SuppressWarnings("unchecked")
	private Optional<IApatiteScript> success(ApatiteConstant<?> constant)
	{
		return success((Type<Object>) constant.type, () -> {
			return constant.value;
		});
	}

	// TODO
	public Optional<IApatiteScript> validate2(ApatiteVM vm)
	{
		Optional<ApatiteVariable<?>> oVariable = vm.getVariables().stream()
			.filter(f -> {
				if (!name.equals(f.name)) return false;
				return true;
			})
			.findFirst();

		if (!oVariable.isPresent()) {
			vm.reportError(begin, end, "No such variable: " + name);
			return failure();
		}

		return success2(oVariable.get());
	}

	// TODO
	@SuppressWarnings("unchecked")
	private Optional<IApatiteScript> success2(ApatiteVariable<?> variable)
	{
		return success((Type<Object>) variable.type, () -> {
			return variable.value;
		});
	}

}
