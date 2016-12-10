package mirrg.helium.compile.oxygen.apatite2.nodes;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.loader.Loader;
import mirrg.helium.compile.oxygen.apatite2.node.ApatiteCodeBase;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;

public class LiteralDouble extends ApatiteCodeBase
{

	public String expression;

	public LiteralDouble(int begin, int end, String expression)
	{
		super(begin, end);
		this.expression = expression;
	}

	@Override
	public Optional<IApatiteScript> validateImpl(ApatiteVM vm)
	{

		double value;
		try {
			value = Double.parseDouble(expression);
		} catch (NumberFormatException e) {
			vm.reportError(begin, end, "Illegal double range: " + expression);
			return failure();
		}

		return success(Loader.DOUBLE, () -> value);
	}

}
