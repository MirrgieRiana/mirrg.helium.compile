package mirrg.helium.compile.oxygen.apatite2.nodes;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.ApatiteLoader;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.node.ApatiteCodeBase;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;

public class CodeLiteralInteger extends ApatiteCodeBase
{

	public String expression;

	public CodeLiteralInteger(int begin, int end, String expression)
	{
		super(begin, end);
		this.expression = expression;
	}

	@Override
	public Optional<IApatiteScript> validateImpl(ApatiteVM vm)
	{

		int value;
		try {
			value = Integer.parseInt(expression, 10);
		} catch (NumberFormatException e) {
			vm.reportError(begin, end, "Illegal integer range: " + expression);
			return failure();
		}

		return success(ApatiteLoader.INTEGER, () -> value);
	}

}
