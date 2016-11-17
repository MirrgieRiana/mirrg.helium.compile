package mirrg.helium.compile.oxygen.util.apatite;

import java.util.ArrayList;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public class ErrorReporter
{

	public boolean isInvalid = false;
	public ArrayList<Tuple<Formula, String>> messages = new ArrayList<>();

	public void report(Formula formula, String message)
	{
		messages.add(new Tuple<>(formula, message));
	}

	public boolean isValid()
	{
		return !isInvalid && messages.isEmpty();
	}

	public void setInvalid()
	{
		isInvalid = true;
	}

}
