package bexysuttx.gomoku.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import bexysuttx.gomoku.Cell;
import bexysuttx.gomoku.CellValue;
import bexysuttx.gomoku.ComputerTurn;
import bexysuttx.gomoku.GameTable;

public class RandomComputerTurn implements ComputerTurn {
	private GameTable gameTable;

	@Override
	public void setGameTable(GameTable gameTable) {
		Objects.requireNonNull(gameTable, "Game table can't be null");
		this.gameTable = gameTable;
	}

	@Override
	public Cell makeTurn() {
		List<Cell> emptyCells = getAllEmptyCells();
		if (emptyCells.size() > 0) {
			Cell randomCell = emptyCells.get(new Random().nextInt(emptyCells.size()));
			gameTable.setValue(randomCell.getRowIndex(), randomCell.getColIndex(), CellValue.COMPUTER);
			return randomCell;
		} else {
			throw new ComputerCantMakeTurnException(
					"All cells are filled! Have you checked draw state before call of computer turn?");
		}
	}

	@Override
	public Cell makeFirstTurn() {
		return makeTurn();
	}

	protected List<Cell> getAllEmptyCells() {
		List<Cell> emptyCells = new ArrayList<>();
		for (int i = 0; i < gameTable.getSize(); i++) {
			for (int j = 0; j < gameTable.getSize(); j++) {
				if (gameTable.isCellFree(i, j)) {
					emptyCells.add(new Cell(i, j));
				}
			}
		}
		return emptyCells;
	}
}
