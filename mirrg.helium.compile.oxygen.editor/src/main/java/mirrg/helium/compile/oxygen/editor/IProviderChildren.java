package mirrg.helium.compile.oxygen.editor;

import java.util.ArrayList;
import java.util.List;

import mirrg.helium.compile.oxygen.parser.core.Node;

public interface IProviderChildren
{

	public List<Node<?>> getChildren();

	public static List<Node<?>> getChildren(Node<?> node)
	{
		if (node.value instanceof IProviderChildren) {
			return ((IProviderChildren) node).getChildren();
		} else if (node.children != null) {
			return node.children;
		} else {
			return new ArrayList<>();
		}
	}

}
