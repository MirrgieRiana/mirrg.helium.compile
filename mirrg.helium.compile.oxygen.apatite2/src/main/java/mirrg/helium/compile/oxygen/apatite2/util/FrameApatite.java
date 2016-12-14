package mirrg.helium.compile.oxygen.apatite2.util;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import mirrg.helium.compile.oxygen.apatite2.Loader;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.event.EventManager;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class FrameApatite extends JFrame
{

	public TextPaneApatite textPaneIn;
	public JTextPane textPaneOut;

	private ApatiteVM vm;
	private Optional<IApatiteScript> script;

	public FrameApatite(Syntax<IApatiteCode> syntax, String source)
	{
		add(process(createSplitPaneVertical(
			createScrollPane(get(() -> {
				textPaneIn = new TextPaneApatite(syntax);
				textPaneIn.setText(source);
				textPaneIn.setPreferredSize(new Dimension(500, 100));
				textPaneIn.addProposalString("a()");
				return textPaneIn;
			})),
			createScrollPane(get(() -> {
				textPaneOut = new JTextPane();
				textPaneOut.setPreferredSize(new Dimension(500, 80));
				textPaneOut.setEditable(false);
				textPaneOut.setOpaque(false);
				textPaneOut.setFont(new Font(Font.MONOSPACED, textPaneOut.getFont().getStyle(), textPaneOut.getFont().getSize()));
				return textPaneOut;
			}))),
			c -> ((JSplitPane) c).setResizeWeight(1) /* TODO mirrg */));

		{
			textPaneIn.event().register(EventTextPaneOxygen.ChangeSource.class, e -> {
				event().post(new EventFrameApatite.ChangeSource(textPaneIn, textPaneOut, textPaneIn.getText()));
			});
			textPaneIn.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
				ApatiteVM vm = createVM();
				Optional<IApatiteScript> script = textPaneIn.getValue().validate(vm);

				if (e.timing == EventTextPaneOxygen.Syntax.Success.TIMING_MAIN) {
					this.vm = vm;
					this.script = script;

					if (script.isPresent()) {
						a:
						{
							Object res;
							try {
								res = script.get().invoke();
							} catch (Exception e1) {
								event().post(new EventFrameApatite.RuntimeError(textPaneIn, textPaneOut, vm, script.get(), e1));
								break a;
							}
							event().post(new EventFrameApatite.Success(textPaneIn, textPaneOut, vm, script.get(), res));
						}
					} else {
						event().post(new EventFrameApatite.CompileError(textPaneIn, textPaneOut, vm));
					}

				}

			});
			textPaneIn.event().register(EventTextPaneOxygen.Syntax.Failure.class, e -> {
				if (e.timing == EventTextPaneOxygen.Syntax.Success.TIMING_MAIN) {
					event().post(new EventFrameApatite.SyntaxError(textPaneIn, textPaneOut, e.result));
				}
			});
			textPaneIn.event().register(EventTextPaneOxygen.Syntax.Error.class, e -> {
				event().post(new EventFrameApatite.SyntaxException(textPaneIn, textPaneOut, e.exception));
			});
			textPaneIn.event().register(EventTextPaneOxygen.Highlight.Post.class, e -> {
				if (!script.isPresent()) {
					// コンパイルエラー時の下線表示
					vm.getErrors()
						.forEach(t -> {
							textPaneIn.setUnderline(
								t.getX(),
								t.getY() - t.getX());
						});
				}
			});
		}

		{

			// パースエラー
			event().register(EventFrameApatite.SyntaxException.class, e -> {
				textPaneOut.setText("" + e.exception);
			});

			// シンタックスエラー
			event().register(EventFrameApatite.SyntaxError.class, e -> {
				LineProvider lineProvider = HString.getLineProvider(textPaneIn.getText());
				int index = textPaneIn.getResult().getTokenProposalIndex();
				textPaneOut.setText(String.format("[SyntaxError %s] expected: %s\n%s",
					toPosition(lineProvider, index),
					textPaneIn.getResult().getTokenProposal().stream()
						.map(p -> p.getName())
						.distinct()
						.collect(Collectors.joining(" ")),
					String.join("\n", getPositionString(lineProvider, index))));
			});

			// コンパイルエラー
			event().register(EventFrameApatite.CompileError.class, e -> {
				LineProvider lineProvider = HString.getLineProvider(textPaneIn.getText());
				textPaneOut.setText(vm.getErrors().stream()
					.map(t -> toPosition(lineProvider, t.getX()) + ": " + t.getZ())
					.collect(Collectors.joining("\n")));
			});

			// ランタイムエラー
			event().register(EventFrameApatite.RuntimeError.class, e -> {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					e.exception.printStackTrace(new PrintStream(out, true, "Unicode"));
					textPaneOut.setText("" + out.toString("Unicode"));
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
				}
			});

			// 成功
			event().register(EventFrameApatite.Success.class, e -> {
				textPaneOut.setText("" + e.value);
			});

		}

		textPaneIn.update();
	}

	protected ApatiteVM createVM()
	{
		return Loader.createVM();
	}

	//

	protected String toPosition(LineProvider lineProvider, int characterIndex)
	{
		int row = lineProvider.getLineNumber(characterIndex);
		int column = characterIndex - lineProvider.getStartIndex(row);

		return String.format("R:%d, C:%d", row, column);
	}

	protected String[] getPositionString(LineProvider lineProvider, int characterIndex)
	{
		int row = lineProvider.getLineNumber(characterIndex);
		int column = characterIndex - lineProvider.getStartIndex(row);
		String line = lineProvider.getContent(row);

		return new String[] {
			line,
			HString.rept(" ", column) + "^",
		};
	}

	//

	private EventManager<EventFrameApatite> eventManager = new EventManager<>();

	public EventManager<EventFrameApatite> event()
	{
		return eventManager;
	}

}
