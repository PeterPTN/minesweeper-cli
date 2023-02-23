import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class App {

  // ---- BIG PICTURE GOALS ----  
  // TODO: Refactor to OOP standard
  // TODO: Use less procedural code somehow

  public static void main(String[] args) throws Exception {

    // DESC: State which determines if game should run
    boolean isAlive = true;

    // -------------------- GRID FOR MINES --------------------
    boolean omitCells = true;
    int arrayXValue = 10;
    int arrayYValue = 10;

    // DESC: Mines always 10% of grid
    // TODO: Use user input for difficulty
    int totalMines = arrayXValue * arrayYValue / 10;
    int placedMines = 0;

    // DESC: Set grid using user info
    Cell[][] grid = new Cell[arrayXValue][arrayYValue];
    int[][] display = new int[arrayXValue][arrayYValue];

    // DESC: Create the grid of safeCells
    for (int i = 0; i < arrayXValue; i++) {
      for (int j = 0; j < arrayYValue; j++) {
        grid[i][j] = new Cell();
        grid[i][j].setToSafeCell();
      }
    }

    // DESC: Override some cells with mines
    ArrayList<int[]> coordsForMines = new ArrayList<>();

    while (placedMines != totalMines) {
      java.util.Random numGenerator = new java.util.Random();
      int xIndex = numGenerator.nextInt(arrayXValue);
      int yIndex = numGenerator.nextInt(arrayYValue);
      int[] mineCoord = new int[2];

      if (grid[xIndex][yIndex].getCellType() == "safe") {
        grid[xIndex][yIndex].setToMine();
        placedMines++;
        mineCoord[0] = xIndex;
        mineCoord[1] = yIndex;
        coordsForMines.add(mineCoord);
      } else if (grid[xIndex][yIndex].getCellType() == "mine") {
        continue;
      }
    }

    // -------------------- GRID FOR DISPLAY --------------------

    // DESC: Grid to store display state
    // -2 unknown, -1 safe, 0 zero mines, 1 mine etc.
    for (int i = 0; i < arrayXValue; i++) {
      for (int j = 0; j < arrayYValue; j++) {
        display[i][j] = -2;
      }
    }

    // -------------------- MOCK INPUTS --------------------

    // TODO: Implement user logic
    while (isAlive) {

    }

    // DESC: Debugging
    // TODO: Display total amount of mines to player at the start
    System.out.print("Mine coords");
    coordsForMines
      .stream()
      .forEach(coord -> System.out.print(Arrays.toString(coord) + ", "));

    // displayGrid(arrayXValue, arrayYValue, display);
    int userY = 0;
    int userX = 0;

    boolean isAMine = determineIfMine(grid, userX, userY);

    if (isAMine) System.out.println("YOU LOSE");

    // Reveals 8 cells surrounding the args
    int[][] surroundingCells = getSurroundingsFromSelectedCell(
      omitCells,
      userX,
      userY,
      arrayXValue,
      arrayYValue
    );
    int[] minesPerCell = determineAmountOfMines(
      grid,
      surroundingCells,
      arrayXValue,
      arrayYValue
    );
    updateGrid(display, surroundingCells, minesPerCell, userX, userY);
    displayGrid(arrayXValue, arrayYValue, display);
    /* 
    userX = 4;
    userY = 5;
    isAMine = determineIfMine(grid, userX, userY);
    if (isAMine) System.out.println("YOU LOSE");
    surroundingCells =
      getSurroundingsFromSelectedCell(
        omitCells,
        userX,
        userY,
        arrayXValue,
        arrayYValue
      );
    minesPerCell =
      determineAmountOfMines(grid, surroundingCells, arrayXValue, arrayYValue);
    updateGrid(display, surroundingCells, minesPerCell, userX, userY);
    displayGrid(arrayXValue, arrayYValue, display);

    userX = 1;
    userY = 0;
    isAMine = determineIfMine(grid, userX, userY);
    if (isAMine) System.out.println("YOU LOSE");
    surroundingCells =
      getSurroundingsFromSelectedCell(
        omitCells,
        userX,
        userY,
        arrayXValue,
        arrayYValue
      );
    minesPerCell =
      determineAmountOfMines(grid, surroundingCells, arrayXValue, arrayYValue);
    updateGrid(display, surroundingCells, minesPerCell, userX, userY);
    displayGrid(arrayXValue, arrayYValue, display);
*/
  }

  // -------------------- DETERMINE IF IT'S A MINE OR NOT --------------------

  public static boolean determineIfMine(Cell[][] grid, int userX, int userY) {
    if (grid[userX][userY].getCellType() == "mine") {
      return true;
    }
    return false;
  }

  // -------------------- GET SURROUNDING CELLS --------------------

  public static int[][] getSurroundingsFromSelectedCell(
    boolean omit,
    int x,
    int y,
    int arrX,
    int arrY
  ) {
    int arraySize = 9;
    int firstIndexStarter = x - 1;
    int secondIndexStarter = y - 1;
    int counter = 0;
    int counterModifier = 3;

    boolean omitLeftColumn = false;
    boolean omitRightColumn = false;
    boolean omitTopRow = false;
    boolean omitBottomRow = false;

    if (x - 1 < 0) {
      omitLeftColumn = true;
    } else if (x + 1 == arrX) {
      omitRightColumn = true;
    }

    if (y - 1 < 0) {
      omitTopRow = true;
    } else if (y + 1 == arrY) {
      omitBottomRow = true;
    }

    // DESC: Takes values at this snapshot
    boolean[] omittedArray = {
      omitLeftColumn,
      omitRightColumn,
      omitTopRow,
      omitBottomRow,
    };

    // DESC: determineAmountOfMines() passes omit = false
    // Therefore runs default: 
    // firstIndexStarter, secondIndexStarter, arraySize and counterModifier
    if (omit) {
      int omittedValue = (int) IntStream
        .range(0, omittedArray.length)
        .filter(i -> omittedArray[i] == true)
        .count();

      // DESC: If users selects an edge cell
      if (omittedValue == 1) {
        arraySize = 6;
        if (omitTopRow == true) {
          firstIndexStarter = x - 1;
          secondIndexStarter = y;
        } else if (omitLeftColumn == true) {
          firstIndexStarter = x;
          secondIndexStarter = y - 1;
          counterModifier = 2;
        } else if (omitRightColumn == true) {
          firstIndexStarter = x - 1;
          secondIndexStarter = y - 1;
          counterModifier = 2;
        } else if (omitBottomRow == true) {
          firstIndexStarter = x - 1;
          secondIndexStarter = y - 1;
        }
      }
      // DESC: If users selects corner cells
      else if (omittedValue > 1) {
        arraySize = 4;
        counterModifier = 2;
        if (omitTopRow == true && omitLeftColumn == true) {
          firstIndexStarter = x;
          secondIndexStarter = y;
        } else if (omitTopRow == true && omitRightColumn == true) {
          firstIndexStarter = x - 1;
        } else if (omitBottomRow == true && omitLeftColumn == true) {
          secondIndexStarter = y - 1;
        } else if (omitBottomRow == true && omitRightColumn == true) {
          firstIndexStarter = x - 1;
          secondIndexStarter = y - 1;
        }
      }
    }

    int[][] surroundingCells = new int[arraySize][2];

    // DESC: Find surrounding cells and store it in the array
    for (int j = 0; j < surroundingCells.length; j++) {
      if (counter == counterModifier) {
        counter = 0;
        secondIndexStarter++;
      }
      surroundingCells[j][0] = firstIndexStarter + counter;
      surroundingCells[j][1] = secondIndexStarter;
      counter++;
    }

    return surroundingCells;
  }

  // -------------------- DETERMINE AMOUNT OF MINES --------------------

  public static int[] determineAmountOfMines(
    Cell[][] grid,
    int[][] surroundingCells,
    int arrayXValue,
    int arrayYValue
  ) {
    int[] minesPerCell = new int[9];
    boolean doNotOmit = false;

    // DESC: For each surroundingCell, for its surrounding cells, check for mines, increment if mine found
    // Later surroundingCell displays total mines surrounding it
    for (int i = 0; i < surroundingCells.length; i++) {
      int[][] wrapperOfSurroundingCells = getSurroundingsFromSelectedCell(
        doNotOmit,
        surroundingCells[i][0],
        surroundingCells[i][1],
        arrayXValue,
        arrayYValue
      );

      // DESC: Only keep cells within grid
      int[][] validWrapperCells = Arrays
        .stream(wrapperOfSurroundingCells)
        .filter(arr -> arr[0] >= 0 && arr[1] >= 0)
        .toArray(int[][]::new);

      for (int j = 0; j < validWrapperCells.length; j++) {
        // DESC: If check to exclude itself from mine count
        if (
          surroundingCells[i][0] == validWrapperCells[j][0] &&
          surroundingCells[i][1] == validWrapperCells[j][1]
        ) {
          continue;
        }

        // DESC: If mine increment minesPerCell array
        if (
          grid[validWrapperCells[j][0]][validWrapperCells[j][1]].getCellType() ==
          "mine"
        ) {
          minesPerCell[i]++;
        }
      }
    }

    return minesPerCell;
  }

  // -------------------- UPDATE GRID --------------------

  public static void updateGrid(
    int[][] display,
    int[][] coordsArr,
    int[] minesArr,
    int userX,
    int userY
  ) {
    // DESC: Match mineCount to surroundingCell coords 
    // (surroundingCells are cells around USER chosen co-ords/input)
    for (int i = 0; i < coordsArr.length; i++) {
      if (display[coordsArr[i][0]][coordsArr[i][1]] != -1) {
        display[coordsArr[i][0]][coordsArr[i][1]] = minesArr[i];
      }
    }

    // DESC: User selected input is set to -1 
    // (-1 represents revealed + safe)
    display[userX][userY] = -1;
  }

  // -------------------- DISPLAY GRID --------------------

  // DESC: Displays display[][], each cell holds a number
  // -2 unknown, -1 safe+revealed, every subsequent number represents total mines around it
  public static void displayGrid(
    int arrayXValue,
    int arrayYValue,
    int[][] display
  ) {
    int displayX = 0;
    int displayY = 0;
    int totalCells = arrayXValue * arrayYValue;
    int counter = 0;

    System.out.println("\n\n\n");

    while (counter != totalCells) {
      if (displayX == 9) {
        if (display[displayX][displayY] != -2) {
          if (display[displayX][displayY] == -1) {
            System.out.printf("|   |  %d \n", displayY);
          } else {
            System.out.printf(
              "| %d |  %d\n",
              display[displayX][displayY],
              displayY
            );
          }
        } else {
          System.out.printf("| _ |  %d\n", displayY);
        }
        displayY++;
        displayX = 0;
        if (displayY == arrayYValue) {
          System.out.println();
          break;
        }
        continue;
      } else {
        if (display[displayX][displayY] != -2) {
          if (display[displayX][displayY] == -1) {
            System.out.printf("|   ");
          } else {
            System.out.printf("| %d ", display[displayX][displayY]);
          }
        } else {
          System.out.print("| _ ");
        }
      }

      displayX++;
      counter++;
    }
    System.out.println("  |   |   |   |   |   |   |   |   |   |");
    System.out.print("  0   1   2   3   4   5   6   7   8   9  ");
    System.out.println();
  }
}
