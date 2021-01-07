package bexysuttx.gomoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bexysuttx.gomoku.impl.DefaultComputerTurn;
import bexysuttx.gomoku.impl.DefaultGameTable;
import bexysuttx.gomoku.impl.DefaultHumanTurn;
import bexysuttx.gomoku.impl.DefaultWinnerChecker;

public class GUIGomoku extends JFrame {
	private static final Logger LOGGER = LoggerFactory.getLogger(GUIGomoku.class);
	private static final long serialVersionUID = 1714372457079337160L;
	private final JLabel cells[][];
	private final transient GameTable gameTable;
	private final transient HumanTurn humanTurn;
	private final transient ComputerTurn computerTurn;
	private final transient WinnerChecker winnerChecker;
	private boolean isHumanFirstTurn;

	public GUIGomoku() throws HeadlessException {
		super("Gomoku");
		// config section
		gameTable = new DefaultGameTable();
		humanTurn = new DefaultHumanTurn();
		computerTurn = new DefaultComputerTurn();
		winnerChecker = new DefaultWinnerChecker();
		// end config section
		initGameComponents();
		cells = new JLabel[gameTable.getSize()][gameTable.getSize()];
		isHumanFirstTurn = true;
		createGameUITable();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Game stopped with game table {}x{}", gameTable.getSize(), gameTable.getSize());
				System.exit(0);
			}
		});
	}

	protected void initGameComponents() {
		humanTurn.setGameTable(gameTable);
		computerTurn.setGameTable(gameTable);
		winnerChecker.setGameTable(gameTable);
	}

	protected void drawCellValue(Cell cell) {
		CellValue cellValue = gameTable.getValue(cell.getRowIndex(), cell.getColIndex());
		cells[cell.getRowIndex()][cell.getColIndex()].setText(cellValue.getValue());
		if (cellValue == CellValue.COMPUTER) {
			cells[cell.getRowIndex()][cell.getColIndex()].setForeground(Color.RED);
		} else {
			// human
			cells[cell.getRowIndex()][cell.getColIndex()].setForeground(Color.BLUE);
		}

	}

	protected void markWinnerCells(List<Cell> winnerCells) {
		for (int i = 0; i < winnerCells.size(); i++) {
			Cell cell = winnerCells.get(i);
			cells[cell.getRowIndex()][cell.getColIndex()].setForeground(Color.CYAN);
			cells[cell.getRowIndex()][cell.getColIndex()].setFont(new Font(Font.SERIF, Font.BOLD, 35));
		}
	}

	protected void createGameUITable() {
		setLayout(new GridLayout(gameTable.getSize(), gameTable.getSize()));
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				final int row = i;
				final int col = j;
				cells[i][j] = new JLabel();
				cells[i][j].setPreferredSize(new Dimension(45, 45));
				cells[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				cells[i][j].setVerticalAlignment(SwingConstants.CENTER);
				cells[i][j].setFont(new Font(Font.SERIF, Font.PLAIN, 35));
				cells[i][j].setForeground(Color.BLACK);
				cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				add(cells[i][j]);
				cells[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						handleHumanTurn(row, col);
					}
				});
			}
		}
	}

	protected void startNewGame() {
		isHumanFirstTurn = !isHumanFirstTurn;
		gameTable.reInit();
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				cells[i][j].setText(gameTable.getValue(i, j).getValue());
				cells[i][j].setFont(new Font(Font.SERIF, Font.PLAIN, 35));
				cells[i][j].setForeground(Color.BLACK);
			}
		}
		if (!isHumanFirstTurn) {
			Cell compCell = computerTurn.makeFirstTurn();
			drawCellValue(compCell);
		}
		LOGGER.info("------------------------------------------------------");
		LOGGER.info("New game started with game table {}x{} {}", gameTable.getSize(), gameTable.getSize(),
				isHumanFirstTurn ? "" : CellValue.COMPUTER + " made the first turn");
	}

	protected void stopGame() {
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				cells[i][j].removeMouseListener(cells[i][j].getMouseListeners()[0]);
			}
		}
		LOGGER.info("Game disabled with game table {}x{}", gameTable.getSize(), gameTable.getSize());
	}

	protected void handleGameOver(String message) {
		if (JOptionPane.showConfirmDialog(this, message) == JOptionPane.YES_OPTION) {
			startNewGame();
		} else {
			stopGame();
		}
	}

	protected void handleHumanTurn(int row, int col) {
		try {
			if (gameTable.isCellFree(row, col)) {
				Cell humanCell = humanTurn.makeTurn(row, col);
				drawCellValue(humanCell);
				WinnerResult winnerResult = winnerChecker.isWinnerFound(CellValue.HUMAN);
				if (winnerResult.winnerExists()) {
					markWinnerCells(winnerResult.getWinnerCells());
					LOGGER.info("Human wins: {}", winnerResult.getWinnerCells());
					handleGameOver("Game over: You win!\nNew game?");
					return;
				}
				if (!gameTable.emptyCellExists()) {
					LOGGER.info("Nobody wins - draw");
					handleGameOver("Game over: Draw!\nNew game?");
					return;
				}
				Cell compCell = computerTurn.makeTurn();
				drawCellValue(compCell);
				winnerResult = winnerChecker.isWinnerFound(CellValue.COMPUTER);
				if (winnerResult.winnerExists()) {
					markWinnerCells(winnerResult.getWinnerCells());
					LOGGER.info("Computer wins: {}", winnerResult.getWinnerCells());
					handleGameOver("Game over: Computer wins!\nNew game?");
					return;
				}
				if (!gameTable.emptyCellExists()) {
					LOGGER.info("Nobody wins - draw");
					handleGameOver("Game over: Draw!\nNew game?");
					return;
				}
			} else {
				LOGGER.warn("Cell {}:{} is not empty", row, col);
				JOptionPane.showMessageDialog(this, "Cell is not empty! Click on empty cell!");
			}
		} catch (RuntimeException e) {
			LOGGER.error("Error in the game: " + e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		GUIGomoku w = new GUIGomoku();
		w.setResizable(false);
		w.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		w.setLocation(dim.width / 2 - w.getSize().width / 2, dim.height / 2 - w.getSize().height / 2);
		w.setVisible(true);
		LOGGER.info("------------------------------------------------------");
		LOGGER.info("New game started with game table {}x{}", w.gameTable.getSize(), w.gameTable.getSize());
	}
}
