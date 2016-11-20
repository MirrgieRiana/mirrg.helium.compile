package mirrg.helium.compile.oxygen.editor;

import java.awt.Event;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.event.EventManager;
import mirrg.helium.standard.hydrogen.util.HLambda;

public class TextPaneOxygen<T> extends JTextPane
{

	public TextPaneOxygen()
	{
		this(null);
	}

	public TextPaneOxygen(Syntax<T> syntax)
	{
		this.syntax = syntax;

		setFont(new Font(Font.MONOSPACED, getFont().getStyle(), getFont().getSize()));

		applyUndo(this);

		// 変更フック
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				event().post(new EventTextPaneOxygen.ChangeSource());
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				event().post(new EventTextPaneOxygen.ChangeSource());
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{

			}

		});

		// 構文解析
		event().register(EventTextPaneOxygen.Update.class, e -> {
			if (syntax == null) return;

			try {
				result = syntax.matches(getText());
			} catch (RuntimeException e2) {
				event().post(new EventTextPaneOxygen.Syntax.Error(EventTextPaneOxygen.Syntax.TIMING_MAIN, e2));
				return;
			}

			if (!result.isValid) {
				event().post(new EventTextPaneOxygen.Syntax.Failure(EventTextPaneOxygen.Syntax.TIMING_MAIN, result));
				return;
			}

			event().post(new EventTextPaneOxygen.Syntax.Success(EventTextPaneOxygen.Syntax.TIMING_MAIN, result));
		});

		// ハイライト
		Timer timer = new Timer(300, e -> {
			if (result.isValid) updateHighlight();
		});
		timer.setRepeats(false);
		event().register(EventTextPaneOxygen.Syntax.class, e -> {
			timer.start();
		});

		// 候補
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.isControlDown()) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						e.consume();

						openProposal();
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{

			}

		});

	}

	public void update()
	{
		event().post(new EventTextPaneOxygen.Update());
	}

	public void updateHighlight()
	{
		eventManager.post(new EventTextPaneOxygen.Highlight.Pre());
		clearHighlight();
		eventManager.post(new EventTextPaneOxygen.Highlight.Cleared());
		if (result.isValid) {
			updateHighlightImpl(result.node);
			eventManager.post(new EventTextPaneOxygen.Highlight.Post());
		}
	}

	protected void updateHighlightImpl(Node<?> node)
	{
		if (node.value instanceof IProviderColor) {
			setAttribute(node.begin, node.end - node.begin, a -> {
				a.addAttribute(StyleConstants.Foreground, ((IProviderColor) node.value).getColor());
			});
		}

		for (Node<?> child : IProviderChildren.getChildren(node)) {
			updateHighlightImpl(child);
		}
	}

	protected void clearHighlight()
	{
		((DefaultStyledDocument) getDocument()).setCharacterAttributes(
			0, getDocument().getLength() + 1, new SimpleAttributeSet(), true);
	}

	protected void setAttribute(int offset, int length, Consumer<SimpleAttributeSet> consumer)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();
		consumer.accept(attr);
		((DefaultStyledDocument) getDocument()).setCharacterAttributes(offset, length, attr, false);
	}

	protected ArrayList<String> proposalStrings = new ArrayList<>();
	{
		addProposalString("a");
	}

	public void addProposalString(String proposalString)
	{
		proposalStrings.add(proposalString);
	}

	public ArrayList<String> getProposalStrings()
	{
		return proposalStrings;
	}

	public void openProposal()
	{
		if (syntax == null) return;

		String text = getText();
		int caretPosition = getCaretPosition();
		String left = text.substring(0, caretPosition);
		String right = text.substring(caretPosition);

		Hashtable<Proposal, Node<?>> tableProposalNode = new Hashtable<>();
		ArrayList<Proposal> proposals = new ArrayList<>();

		for (String proposalString : getProposalStrings()) {
			ResultOxygen<T> result;
			try {
				result = syntax.matches(left + proposalString + right); // カーソル位置に文字を入れた文字列
			} catch (RuntimeException e) {
				continue;
			}
			if (!result.isValid) continue;

			// 候補表示可能

			event().post(new EventTextPaneOxygen.Syntax.Success(EventTextPaneOxygen.Syntax.TIMING_PROPOSAL, result));

			// 最も内側の候補プロバイダ取得
			ArrayList<Node<?>> hierarchy = getHierarchy(caretPosition, result.node);
			Optional<Node<?>> node2 = HLambda.reverse(hierarchy.stream())
				.filter(n -> n.value instanceof IProviderProposal)
				.findFirst();
			if (!node2.isPresent()) continue;

			// プロバイダがあった

			IProviderProposal providerProposal = (IProviderProposal) node2.get().value;
			Stream<Proposal> stream = providerProposal.getProposals();
			if (stream == null) continue;

			// 候補が取得できた

			stream.forEach(t -> {
				tableProposalNode.put(t, node2.get());
				proposals.add(t);
			});

		}

		if (proposals.size() == 0) return;

		{
			DialogProposal dialog = new DialogProposal(proposals.stream());

			dialog.eventManager.register(EventDialogProposal.Update.class, e2 -> {
				try {
					Node<?> node2 = tableProposalNode.get(e2.proposal);
					((DefaultStyledDocument) getDocument()).replace(
						node2.begin,
						node2.end - node2.begin - 1,
						e2.proposal.text, null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			});

			{
				Point a = getCaret().getMagicCaretPosition();
				Point b = getLocationOnScreen();
				if (a != null && b != null) {
					dialog.setLocation(a.x + b.x, a.y + b.y + 20);
				}
			}
			dialog.pack();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}

	}

	protected ArrayList<Node<?>> getHierarchy(int caretPosition, Node<?> node)
	{
		ArrayList<Node<?>> hierarchy = new ArrayList<>();
		while (true) {
			hierarchy.add(node);
			Optional<Node<?>> node2 = IProviderChildren.getChildren(node).stream()
				.filter(n -> n.begin <= caretPosition)
				.filter(n -> n.end > caretPosition)
				.findFirst();
			if (!node2.isPresent()) break;
			node = node2.get();
		}
		return hierarchy;
	}

	//

	private EventManager<EventTextPaneOxygen> eventManager = new EventManager<>();

	public EventManager<EventTextPaneOxygen> event()
	{
		return eventManager;
	}

	//

	private Syntax<T> syntax;

	public Syntax<T> getSyntax()
	{
		return syntax;
	}

	public void setSyntax(Syntax<T> syntax)
	{
		this.syntax = syntax;

		update();
	}

	//

	private ResultOxygen<T> result;

	public ResultOxygen<T> getResult()
	{
		return result;
	}

	public T getValue()
	{
		return result.node.value;
	}

	//

	// TODO mirrg
	public static UndoManager applyUndo(JTextComponent textComponent)
	{
		String ACTION_KEY_UNDO = "undo";
		String ACTION_KEY_REDO = "redo";
		UndoManager undoManager = new UndoManager();

		ActionMap actionMap = textComponent.getActionMap();
		InputMap inputMap = textComponent.getInputMap();
		if (actionMap.get(ACTION_KEY_UNDO) == null) {
			AbstractAction action = new AbstractAction("元に戻す(U)") {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (undoManager.canUndo()) undoManager.undo();
				}

			};
			action.putValue(Action.MNEMONIC_KEY, new Integer('U'));
			action.putValue(Action.SHORT_DESCRIPTION, "元に戻す");
			action.putValue(Action.LONG_DESCRIPTION, "元に戻す");
			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('Z', Event.CTRL_MASK));

			actionMap.put(ACTION_KEY_UNDO, action);
			inputMap.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), ACTION_KEY_UNDO);
		}
		if (actionMap.get(ACTION_KEY_REDO) == null) {
			AbstractAction action = new AbstractAction("やり直し(R)") {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (undoManager.canRedo()) undoManager.redo();
				}

			};
			action.putValue(Action.MNEMONIC_KEY, new Integer('R'));
			action.putValue(Action.SHORT_DESCRIPTION, "やり直し");
			action.putValue(Action.LONG_DESCRIPTION, "やり直し");
			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('Y', Event.CTRL_MASK));

			actionMap.put(ACTION_KEY_REDO, action);
			inputMap.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), ACTION_KEY_REDO);
		}

		textComponent.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				if (e instanceof DefaultDocumentEvent) undoManager.addEdit((DefaultDocumentEvent) e);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				if (e instanceof DefaultDocumentEvent) undoManager.addEdit((DefaultDocumentEvent) e);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{

			}

		});

		return undoManager;
	}

}
