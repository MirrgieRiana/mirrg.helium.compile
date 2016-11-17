package mirrg.helium.compile.oxygen.util.apatite;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class PanelApatite extends JPanel
{

	private TextPaneOxygen<Formula> textPaneOxygen;
	private JLabel labelOut;

	public PanelApatite(Syntax<Formula> syntax, VM vm)
	{
		setLayout(new CardLayout());
		add(createSplitPaneVertical(
			get(() -> {
				textPaneOxygen = new TextPaneOxygenExtension(syntax, vm);
				return textPaneOxygen;
			}),
			createScrollPane(get(() -> {
				labelOut = new JLabel("<html></html>");
				labelOut.setVerticalAlignment(SwingConstants.TOP);
				return labelOut;
			}), 100, 80)));
	}

	public TextPaneOxygen<Formula> getPanelSyntax()
	{
		return textPaneOxygen;
	}

	public JLabel getLabelOut()
	{
		return labelOut;
	}

	public class TextPaneOxygenExtension extends TextPaneOxygen<Formula>
	{

		public TextPaneOxygenExtension(Syntax<Formula> syntax, VM vm)
		{
			super(syntax);

			event().register(EventTextPaneOxygen.ChangeSource.class, e -> {
				Node<Formula> node = syntax.parse(getText());
				if (node != null) {
					ErrorReporter errorReporter = node.value.validate(vm);

					if (errorReporter.isValid()) {
						labelOut.setText("<html>" + node.value.calculate() + "</html>");
					} else {
						LineProvider lineProvider = HString.getLineProvider(getText());

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
			event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
				getValue().validate(vm);
			});
			event().register(EventTextPaneOxygen.Highlight.Post.class, e -> {
				ErrorReporter errorReporter = getValue().validate(vm);
				errorReporter.messages.forEach(m -> {
					setAttribute(m.getX().getBegin(), m.getX().getEnd() - m.getX().getBegin(), a -> {
						a.addAttribute(StyleConstants.Underline, true);
					});
				});
			});

		}

	}

}
