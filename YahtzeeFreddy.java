/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 * Author : Freddy Saenz
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class YahtzeeFreddy extends GraphicsProgram implements
		YahtzeeConstantsFreddy {

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {




		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);

		totalsXCategory = new int[nPlayers][N_CATEGORIES];
		playerFilledCategorys = new int[nPlayers][N_CATEGORIES];
		playerTotal = new int[nPlayers];

		playGame();
	}

	private void playGame() {
		int[] dice = new int[N_DICE];

		for (int i = 1; i <= N_SCORING_CATEGORIES; i++) {
			for (int j = 1; j <= nPlayers; j++) {
				display.printMessage(playerNames[j - 1]
						+ "'s turn!, Click the \"Rolll Dice\" button to roll the dice");
				display.waitForPlayerToClickRoll(j);

				rollDice(dice);
				reRoll(dice);

				int categoria = 0;
				categoria = selectCategory(j);
				int score = 0;
				score = scoreCategory(dice, categoria);

				display.updateScorecard(categoria, j, score);
				playerTotal[j - 1] += score;

				totalsXCategory[j - 1][categoria - 1] = score;
				playerFilledCategorys[j - 1][categoria - 1] = 1;
				display.updateScorecard(TOTAL, j, playerTotal[j - 1]);

			}
		}
		endGame();
	}

	/**
	 * Calculates the score based upon the caterory
	 * 
	 * @param dice
	 * @param categoria
	 * @return the score obtained
	 */
	private int scoreCategory(int[] dice, int categoria) {
		int score = 0;

		// if (YahtzeeMagicStub.checkCategory(dice, categoria)) {

		switch (categoria) {
		case ONES:
		case TWOS:
		case THREES:
		case FOURS:
		case FIVES:
		case SIXES:
			score = quantityEquals(dice, categoria);//sum of category
			break;
		case THREE_OF_A_KIND:
			score = sumEquals(dice, 3);
			break;
		case FOUR_OF_A_KIND:
			score = sumEquals(dice, 4);
			break;
		case FULL_HOUSE:
			if (fullHouse(dice) == 1)
				score = POINTS_FULL_HOUSE;

			break;

		case SMALL_STRAIGHT:
			if (sumStraight(dice, 4) != 0)
				score = POINTS_SMALL_STRAIGHT;
			break;
		case LARGE_STRAIGHT:
			if (sumStraight(dice, 5) != 0)
				score = POINTS_LARGE_STRAIGHT;
			break;

		case YAHTZEE:
			if (sumEquals(dice, 5) != 0)
				score = POINTS_YAHTZEE;
			break;
		case CHANCE:
			score = sumElements(dice);
			break;
		}
		// }
		return score;
	}

	/**
	 * two chances to change the dice
	 * 
	 * @param dice
	 */
	private void reRoll(int[] dice) {
		for (int i = 1; i <= 2; i++) {
			display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\"");
			display.waitForPlayerToSelectDice();
			//
			for (int j = 0; j <= N_DICE - 1; j++) {
				if (display.isDieSelected(j)) {
					dice[j] = rgen.nextInt(1, 6);
				}
			}
			display.displayDice(dice);

		}

	}

	/**
	 * Roll dice
	 * 
	 * @param dice
	 */
	private void rollDice(int[] dice) {
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		display.displayDice(dice);

	}

	/**
	 * Show the winner of the game
	 */
	private void endGame() {
		int posWin = -1;

		/*
		 * Calculates the score of each player
		 */
		for (int i = 0; i < nPlayers; i++) {
			int sumUpper = 0;
			int sumLower = 0;
			playerTotal[i] = 0;
			for (int j = 0; j < N_CATEGORIES; j++) {

				if (j < UPPER_SCORE - 1) {
					sumUpper += totalsXCategory[i][j];
				} else if (j == UPPER_SCORE - 1) {
					if (sumUpper >= POINTS_X_UPPER_BONUS) {

						totalsXCategory[i][j] += POINTS_UPPER_BONUS;
						display.updateScorecard(UPPER_BONUS, i + 1,
								POINTS_UPPER_BONUS);

					} else {
						display.updateScorecard(UPPER_BONUS, i + 1, 0);

					}
				} else if (j > UPPER_BONUS - 1) {
					sumLower += totalsXCategory[i][j];

				}
				playerTotal[i] += totalsXCategory[i][j];
			}
			display.updateScorecard(UPPER_SCORE, i + 1, sumUpper);
			display.updateScorecard(LOWER_SCORE, i + 1, sumLower);

		}

		/* search the winner */
		for (int i = 0; i < nPlayers; i++) {
			if (posWin == -1)
				posWin = i;
			else if (playerTotal[posWin] < playerTotal[i]) {
				posWin = i;
			}
			display.updateScorecard(TOTAL, i + 1, playerTotal[i]);

		}

		/* show the winner */

		if (posWin != -1) {
			display.printMessage("Congratulations," + playerNames[posWin]
					+ ",you're the winner with a total score of "
					+ playerTotal[posWin] + "!");

		}

	}

	/**
	 * User select a valid category
	 * 
	 * @param numUser
	 * @return a category
	 */
	private int selectCategory(int numUser) {
		int categoria = 0;

		display.printMessage("Select a category for this roll");
		int categoryOK = 0;
		while (categoryOK == 0) {
			categoria = display.waitForPlayerToSelectCategory();
			switch (categoria) {
			case UPPER_SCORE:
			case UPPER_BONUS:
			case LOWER_SCORE:
			case TOTAL:
				/* the user can not choose this categories */
				break;
			default:
				if (playerFilledCategorys[numUser - 1][categoria - 1] == 0) {
					categoryOK = 1;
				} else {
					/* this category has already been used by this player */
					display.printMessage("Invalid Category ,Select a category for this roll");

				}
				break;

			}
		}
		return categoria;
	}

	/**
	 * For Ones,Twos,...Sixes, calculates the points .
	 * 
	 * @param dice
	 * @param numSearch
	 *            (1,2,..6)
	 * @return points
	 */
	private int quantityEquals(int[] dice, int numSearch) {
		int res = 0;
		for (int i = 0; i < dice.length; i++) {
			if (dice[i] == numSearch)
				res += numSearch;
		}
		return res;

	}

	/**
	 * Indicate if a dice contains a Straight of numElements Elements
	 * 
	 * @param dice
	 * @param numElements
	 * @return 1 if the Straight contains the number of Elements , 0 in other
	 *         case
	 */
	private int sumStraight(int[] dice, int numElements) {
		int res = 0;
		int[] arrThereAre = new int[FACES_DICE];
		/*
		 * Converts an array from i.e [ 5,4,2,4,6] to[0,1,0,1,1,1] in other
		 * words show if a number is present in the array if 4 or 5 numbers are
		 * sequential , the sum of this numbers is 4 or 5 .
		 * if A[i] == k ==> B[k-1] = 1,2,...,5
		 */
		for (int i = 0; i < dice.length; i++) {
			arrThereAre[dice[i] - 1] = 1;
		}
		for (int i = 0; i < arrThereAre.length; i++) {
			/*
			 * Number present , one plus
			 */
			if (arrThereAre[i] == 1) {
				res++;
				/*
				 * complete, there are a Straight
				 */
				if (res == numElements)
					break;

			} else {
				res = 0;
			}
		}

		if (res < numElements)
			res = 0;

		return res;
	}

	/**
	 * Indicates if dice contains a Element quantity Times
	 * 
	 * @param dice
	 * @param quantity
	 * @return sum of equal elements
	 */
	private int sumEquals(int[] dice, int quantity) {
		int res = 0;
		int[] arrThereAre = new int[FACES_DICE];
		int total = 0;
		int found = 0;

		/*
		 * Converts an array from i.e [ 5,4,2,4,6] to[0,1,0,2,1,1] for 4 in
		 * other words counts how many times a number is in the array and
		 * indicates if a number is in dice grather o equal than quantity times
		 */

		for (int i = 0; i < dice.length; i++) {
			total += dice[i];
			arrThereAre[dice[i] - 1]++;
			if (arrThereAre[dice[i] - 1] == quantity) {
				found = 1;
			}
		}
		if (found == 1) {
			res = total;
		}
		return res;
	}

	/**
	 * Sum of the value of dice
	 * 
	 * @param dice
	 * @return
	 */
	private int sumElements(int[] dice) {
		int res = 0;
		for (int i = 0; i < dice.length; i++) {
			res += dice[i];
		}
		return res;
	}

	/**
	 * Indicates if dice is a full House
	 * 
	 * @param dice
	 * @return
	 */
	private int fullHouse(int[] dice) {
		int res = 0;
		int[] arrThereAre = new int[FACES_DICE];
		for (int i = 0; i < arrThereAre.length; i++) {
			arrThereAre[i] = 0;
		}

		/*
		 * Converts an array from i.e [ 5,4,2,4,5] to[0,1,0,2,2,0] in other
		 * words counts how many times a number is in the array and sum the
		 * quantities grather than one , in the example the sum is 4 example 2 [
		 * 5,4,5,4,5] to [0,0,0,2,3,0] sum equal to 5, and there are full house
		 */

		for (int i = 0; i < dice.length; i++) {
			arrThereAre[dice[i] - 1]++;
		}
		int total = 0;
		for (int i = 0; i < arrThereAre.length; i++) {
			if (arrThereAre[i] > 1) {
				total += arrThereAre[i];
			}

		}
		if (total == 5) {
			res = 1;
		}
		return res;
	}

	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

	private int[][] totalsXCategory;
	private int[][] playerFilledCategorys;
	private int[] playerTotal;

}