import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	
	AITicTacToe a = new AITicTacToe();;
	
	@Test
	void checkFalse() {
		ArrayList<Character> gameBoard = new ArrayList<>(Arrays.asList('-','-','-','-','-','-','-','-','-'));
		assertEquals(false, a.checkWin(gameBoard));
	}
	
	@Test
	void checkAcross() {
		ArrayList<Character> gameBoard = new ArrayList<>(Arrays.asList('-','-','-','-','-','-','-','-','-'));
		gameBoard.set(0, 'X');
		gameBoard.set(1, 'X');
		gameBoard.set(2, 'X');
		assertEquals(true, a.checkWin(gameBoard));
	}
	
	@Test
	void checkDown() {
		ArrayList<Character> gameBoard = new ArrayList<>(Arrays.asList('-','-','-','-','-','-','-','-','-'));
		gameBoard.set(0, 'X');
		gameBoard.set(3, 'X');
		gameBoard.set(6, 'X');
		assertEquals(true, a.checkWin(gameBoard));
	}
	
	@Test
	void checkDiagnol() {
		ArrayList<Character> gameBoard = new ArrayList<>(Arrays.asList('-','-','-','-','-','-','-','-','-'));
		gameBoard.set(0, 'X');
		gameBoard.set(4, 'X');
		gameBoard.set(8, 'X');
		assertEquals(true, a.checkWin(gameBoard));
	}

}
