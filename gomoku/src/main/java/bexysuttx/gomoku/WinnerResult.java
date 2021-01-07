package bexysuttx.gomoku;

import java.util.List;


public interface WinnerResult {

	boolean winnerExists();

	List<Cell> getWinnerCells();
}
