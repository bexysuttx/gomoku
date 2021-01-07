package bexysuttx.gomoku;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CellValueTest {

	@Test
	public void testGetValue() {
		assertEquals("O", CellValue.COMPUTER.getValue());
		assertEquals("X", CellValue.HUMAN.getValue());
		assertEquals(" ", CellValue.EMPTY.getValue());
	}
}
