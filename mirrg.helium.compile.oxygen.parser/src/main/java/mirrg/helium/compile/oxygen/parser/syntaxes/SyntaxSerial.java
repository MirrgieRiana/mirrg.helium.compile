package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import mirrg.helium.compile.oxygen.parser.HSyntaxOxygen;
import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxSerial<T> implements ISyntax<T>
{

	public final Supplier<T> supplier;
	public final ArrayList<Function<T, ISyntax<?>>> syntaxes = new ArrayList<>();

	public SyntaxSerial(Supplier<T> supplier)
	{
		this.supplier = supplier;
	}

	public <T2> SyntaxSerial<T> and(ISyntax<T2> syntax)
	{
		return and(syntax, (t, t2) -> {});
	}

	public <T2> SyntaxSerial<T> and(ISyntax<T2> syntax, BiConsumer<T, T2> function)
	{
		syntaxes.add(t -> HSyntaxOxygen.map(syntax, t2 -> {
			function.accept(t, t2);
			return null;
		}));
		return this;
	}

	@Override
	public Node<T> parse(String text, int index)
	{
		T t = supplier.get();
		ArrayList<Node<?>> children = new ArrayList<>();
		int begin = index;
		int end = begin;

		for (Function<T, ISyntax<?>> syntax : syntaxes) {
			Node<?> node = syntax.apply(t).parse(text, index);
			if (node == null) return null;
			children.add(node);
			index += node.end - node.begin;
			end = node.end;
		}

		return new Node<>(this, children, begin, end, t);
	}

}
