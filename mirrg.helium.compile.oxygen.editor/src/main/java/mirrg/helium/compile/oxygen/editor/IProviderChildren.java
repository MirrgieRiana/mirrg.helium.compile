package mirrg.helium.compile.oxygen.editor;

import java.util.ArrayList;
import java.util.List;

import mirrg.helium.compile.oxygen.parser.core.Node;

public interface IProviderChildren
{

	public List<Node<?>> getChildren();

	public static List<Node<?>> getChildren(Node<?> node)
	{
		ArrayList<Node<?>> list = new ArrayList<>();
		if (node.value instanceof IProviderChildren) list.addAll(((IProviderChildren) node.value).getChildren());
		if (node.children != null) list.addAll(node.children);
		return list;
	}

}
