package mirrg.helium.compile.oxygen.apatite2.nodes;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.ApatiteLoader;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.node.ApatiteCodeBase;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;

public class LiteralString extends ApatiteCodeBase
{

	public String expression;

	public LiteralString(int begin, int end, String expression)
	{
		super(begin, end);
		this.expression = expression;
	}

	@Override
	public Optional<IApatiteScript> validateImpl(ApatiteVM vm)
	{
		return success(ApatiteLoader.STRING, () -> expression);
	}

}
