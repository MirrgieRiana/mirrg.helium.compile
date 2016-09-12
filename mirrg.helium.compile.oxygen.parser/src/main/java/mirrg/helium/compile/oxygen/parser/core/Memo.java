package mirrg.helium.compile.oxygen.parser.core;

import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Supplier;

public class Memo
{

	private Hashtable<Syntax<?>, Hashtable<Integer, Optional<Node<?>>>> memo = new Hashtable<>();

	@SuppressWarnings("unchecked")
	public <T> Node<T> get(Syntax<T> syntax, int index, Supplier<Node<T>> or)
	{

		Hashtable<Integer, Optional<Node<?>>> memo2 = memo.get(syntax);
		if (memo2 == null) {
			memo2 = new Hashtable<>();
			memo.put(syntax, memo2);
		}

		Optional<Node<?>> memo3 = memo2.get(index);
		if (memo3 == null) {
			memo3 = Optional.ofNullable(or.get());
			memo2.put(index, memo3);
		}

		return (Node<T>) memo3.orElse(null);
	}

}
