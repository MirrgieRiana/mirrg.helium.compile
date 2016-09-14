package mirrg.helium.compile.oxygen.parser.test2;

import java.awt.Color;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JLabel;

import mirrg.helium.compile.oxygen.util.Proposal;

public class FormulaVariable extends FormulaNode
{

	public final String identifier;

	protected Variable variable;
	protected VM vm;

	public FormulaVariable(String identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public Type<?> getType(VM vm)
	{
		return variable.type;
	}

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		this.vm = vm;

		Optional<Variable> variable = vm.registryVariable.get(identifier);

		if (!variable.isPresent()) {
			errorReporter.report(this, "そのような変数はありません: " + identifier);
			return false;
		}

		this.variable = variable.get();
		return true;
	}

	@Override
	public Object calculate(Runtime runtime)
	{
		return variable.get(runtime);
	}

	public Color getColor()
	{
		if (variable == null) return Color.black;
		return variable.type.color;
	}

	public Stream<Proposal> getProposals()
	{
		if (vm == null) return null;
		return vm.registryVariable.variables.stream()
			.map(v -> new Proposal(v.identifier) {

				@Override
				public void decorateListCellRendererComponent(JLabel label)
				{
					label.setForeground(v.type.color);
				}

			});
	}

}
