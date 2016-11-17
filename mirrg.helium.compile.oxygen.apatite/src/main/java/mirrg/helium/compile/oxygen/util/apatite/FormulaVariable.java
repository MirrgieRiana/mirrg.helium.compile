package mirrg.helium.compile.oxygen.util.apatite;

import java.awt.Color;
import java.util.stream.Stream;

import javax.swing.JLabel;

import mirrg.helium.compile.oxygen.editor.Proposal;

public class FormulaVariable extends FormulaNode
{

	public final String identifier;

	protected Frame frame;
	protected Variable variable;

	public FormulaVariable(String identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public Type getType()
	{
		return variable.type;
	}

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		frame = vm.frame;
		variable = vm.frame.getVariable(identifier);

		if (variable == null) {
			errorReporter.report(this, "Undefined Variable: " + identifier);
			return false;
		}
		return true;
	}

	@Override
	public Object calculate()
	{
		return variable.get();
	}

	public Color getColor()
	{
		if (variable == null) return Color.black;
		return variable.type.getColor();
	}

	public Stream<Proposal> getProposals()
	{
		if (frame == null) return null;
		return frame.getVariables().entrySet().stream()
			.map(v -> new Proposal(v.getKey()) {

				@Override
				public void decorateListCellRendererComponent(JLabel label)
				{
					label.setForeground(v.getValue().type.getColor());
				}

			});
	}

}
