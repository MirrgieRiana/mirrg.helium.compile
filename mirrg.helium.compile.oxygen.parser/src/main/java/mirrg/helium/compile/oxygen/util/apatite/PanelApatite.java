package mirrg.helium.compile.oxygen.util.apatite;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.util.EventPanelSyntax;
import mirrg.helium.compile.oxygen.util.PanelSyntax;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class PanelApatite extends JPanel
{

	private PanelSyntax panelSyntax;
	private JLabel labelOut;

	public PanelApatite(Syntax<Formula> syntax, VM vm)
	{
		setLayout(new CardLayout());
		add(createSplitPaneVertical(
			get(() -> {
				panelSyntax = new PanelSyntax(syntax);
				panelSyntax.eventManager.register(EventPanelSyntax.Edit.class, e -> {
					Node<Formula> node = syntax.parse(e.source);
					if (node != null) {
						ErrorReporter errorReporter = node.value.validate(vm);

						if (errorReporter.isValid()) {
							labelOut.setText("<html>" + node.value.calculate(vm.createRuntime()) + "</html>");
						} else {
							LineProvider lineProvider = HString.getLineProvider(e.source);

							labelOut.setText("<html>" + errorReporter.messages.stream()
								.map(t -> {
									int lineNumber = lineProvider.getLineNumber(t.getX().getBegin());
									return String.format("[L: %d, C: %d] %s",
										lineNumber,
										t.getX().getBegin() - lineProvider.getStartIndex(lineNumber),
										t.getY());
								})
								.collect(Collectors.joining("<br>")) + "</html>");
						}
					} else {
						labelOut.setText("<html>" + "構文エラーです" + "</html>");
					}
				});
				panelSyntax.eventManager.register(EventPanelSyntax.Parsed.class, e -> {
					((Node<Formula>) e.node).value.validate(vm);
				});
				panelSyntax.eventManager.register(EventPanelSyntax.AfterHighlight.class, e -> {
					ErrorReporter errorReporter = ((Node<Formula>) e.node).value.validate(vm);
					errorReporter.messages.forEach(m -> {
						panelSyntax.setAttribute(m.getX().getBegin(), m.getX().getEnd() - m.getX().getBegin(), a -> {
							a.addAttribute(StyleConstants.Underline, true);
						});
					});
				});
				return panelSyntax;
			}),
			createScrollPane(get(() -> {
				labelOut = new JLabel("<html></html>");
				labelOut.setVerticalAlignment(JLabel.TOP);
				return labelOut;
			}), 100, 80)));
	}

	public PanelSyntax getPanelSyntax()
	{
		return panelSyntax;
	}

	public JLabel getLabelOut()
	{
		return labelOut;
	}

}
