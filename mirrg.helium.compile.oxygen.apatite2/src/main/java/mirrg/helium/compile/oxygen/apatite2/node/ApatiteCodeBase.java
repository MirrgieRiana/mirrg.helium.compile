package mirrg.helium.compile.oxygen.apatite2.node;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Supplier;

import mirrg.helium.compile.oxygen.apatite2.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.compile.oxygen.editor.IProviderColor;

public abstract class ApatiteCodeBase implements IApatiteCode, IProviderColor
{

	public int begin;
	public int end;

	public ApatiteCodeBase(int begin, int end)
	{
		this.begin = begin;
		this.end = end;
	}

	@Override
	public int getBegin()
	{
		return begin;
	}

	@Override
	public int getEnd()
	{
		return end;
	}

	protected Optional<IApatiteScript> failure()
	{
		return Optional.empty();
	}

	protected <T> Optional<IApatiteScript> success(Type<T> type, Supplier<T> supplier)
	{
		return Optional.of(new ApatiteScriptBase(type, supplier::get));
	}

	private IApatiteScript script;

	@Override
	public final Optional<IApatiteScript> validate(ApatiteVM vm)
	{
		Optional<IApatiteScript> oScript = validateImpl(vm);
		script = oScript.orElse(null);
		return oScript;
	}

	public abstract Optional<IApatiteScript> validateImpl(ApatiteVM vm);

	@Override
	public Color getColor()
	{
		return script == null ? null : script.getType().getColor();
	}

}
