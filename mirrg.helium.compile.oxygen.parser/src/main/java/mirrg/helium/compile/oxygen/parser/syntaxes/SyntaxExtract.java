package mirrg.helium.compile.oxygen.parser.syntaxes;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import mirrg.helium.compile.oxygen.parser.core.Memo;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.struct.Struct1;

public class SyntaxExtract<T> extends Syntax<T>
{

	public final SyntaxSerial<Struct1<T>> syntaxSerial = serial(Struct1<T>::new);
	public final SyntaxPack<Struct1<T>, T> syntaxPack = pack(syntaxSerial, Struct1::getX);

	@Override
	protected Node<T> parseImpl(Memo memo, String text, int index)
	{
		return syntaxPack.parse(memo, text, index);
	}

	public SyntaxExtract<T> and(Syntax<?> syntax)
	{
		syntaxSerial.and(syntax);
		return this;
	}

	public SyntaxExtract<T> extract(Syntax<T> syntax)
	{
		syntaxSerial.and(syntax, Struct1::setX);
		return this;
	}

}
