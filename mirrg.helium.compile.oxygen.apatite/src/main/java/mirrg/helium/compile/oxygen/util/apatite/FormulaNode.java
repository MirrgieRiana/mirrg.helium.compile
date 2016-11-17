package mirrg.helium.compile.oxygen.util.apatite;

import mirrg.helium.compile.oxygen.parser.core.IListenerNode;
import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class FormulaNode extends Formula implements IListenerNode
{

	protected Node<?> node;

	@Override
	public void setNode(Node<?> node)
	{
		this.node = node;
	}

	@Override
	public int getBegin()
	{
		return node.begin;
	}

	@Override
	public int getEnd()
	{
		return node.end;
	}

}
