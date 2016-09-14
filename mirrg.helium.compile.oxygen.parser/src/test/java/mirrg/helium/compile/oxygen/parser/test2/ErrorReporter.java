package mirrg.helium.compile.oxygen.parser.test2;

import java.util.ArrayList;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public class ErrorReporter
{

	public ArrayList<Tuple<Formula, String>> messages = new ArrayList<>();

	public void report(Formula formula, String message)
	{
		messages.add(new Tuple<>(formula, message));
	}

	public boolean isValid()
	{
		return messages.isEmpty();
	}

}
