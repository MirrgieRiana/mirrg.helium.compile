package mirrg.helium.compile.oxygen.apatite2.node;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.ApatiteVM;

public interface IApatiteCode
{

	public Optional<IApatiteScript> validate(ApatiteVM vm);

	public int getBegin();

	public int getEnd();

}
