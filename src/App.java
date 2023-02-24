import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class App {

  // ---- BIG PICTURE GOALS ----
  // TODO: Refactor to OOP standard
  // TODO: Use less procedural code somehow

  public static void main(String[] args) throws Exception {
    // DESC: State which determines if game should run
    Timer timer = new Timer();
    Scanner scanner = new Scanner(System.in);
    int arrayXValue = -1;
    int arrayYValue = -1;
    int percentage = 0;

    System.out.printf("\nMinesweeper Elite - No Flags Edition V0.1.0\n\n");
    System.out.println("Grid setup");

    // DESC: Get X value
    while (arrayXValue < 5 || arrayXValue > 10) {
      System.out.println(
        "Please provide a numerical digit(s) for amount of ROWS:\n(MIN: 0, MAX: 10)"
      );
      arrayXValue = scanner.nextInt();

      if (arrayXValue >= 5 && arrayXValue <= 10) break;

      ClearTerminal();
      System.out.println("\nINVALID INPUT");
    }

    // DESC: Get Y value
    while (arrayYValue < 5 || arrayYValue > 20) {
      System.out.println(
        "Please provide a numerical digit(s) for amount of COLUMNS:\n(MIN: 0, MAX: 20)"
      );
      arrayYValue = scanner.nextInt();

      if (arrayYValue >= 5 && arrayYValue <= 20) break;

      ClearTerminal();
      System.out.println("\nINVALID INPUT");
    }

    // DESC: Set mines based on percentage

    while (percentage < 10 || percentage > 90) {
      System.out.println(
        "Please provide a numerical digit(s) for percentage of grid to be mines:\n(MIN: 10, MAX: 90)"
      );
      percentage = scanner.nextInt();

      if (percentage >= 10 && percentage <= 90) break;

      ClearTerminal();
      System.out.println("\nINVALID INPUT");
    }

    double doubleTotalMines =
      arrayXValue * arrayYValue * ((double) percentage / 100);
    int totalMines = (int) Math.round(doubleTotalMines);
    boolean isAlive = true;
    boolean runGame = true;
    boolean omitCells = true;

    int userX = 0;
    int userY = 0;
    int placedMines = 0;
    int totalSafeCells = arrayXValue * arrayYValue - totalMines;
    int totalSafeCellsCounter = 0;

    timer.schedule(
      new TimerTask() {
        public void run() {
          System.out.println("Rendering rows...");
        }
      },
      250
    );
    Thread.sleep(250);

    timer.schedule(
      new TimerTask() {
        public void run() {
          System.out.println("Creating columns...");
        }
      },
      500
    );
    Thread.sleep(500);

    timer.schedule(
      new TimerTask() {
        public void run() {
          System.out.println("Manufacturing mines...");
        }
      },
      650
    );
    Thread.sleep(750);

    timer.schedule(
      new TimerTask() {
        public void run() {
          System.out.println("Generating grid...");
        }
      },
      1000
    );
    Thread.sleep(1350);
    ClearTerminal();

    // -------------------- GRID FOR MINES --------------------

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

    // DESC: Debugging mines
    // System.out.print("Mine coords");
    // coordsForMines
    //  .stream()
    //  .forEach(coord -> System.out.print(Arrays.toString(coord) + ", "));

    System.out.printf(
      "Your grid will be %dx%d - a total of " +
      (arrayXValue * arrayYValue) +
      " cells\n",
      arrayXValue,
      arrayYValue
    );
    System.out.printf("There will be a total of %d mines\n", totalMines);
    System.out.println("Sweep fast and take chances!\n");
    displayGrid(arrayXValue, arrayYValue, display);

    while (runGame == true) {
      System.out.println(
        "Please enter a numerical digit(s) for the 'x' co-ordinate"
      );
      userX = scanner.nextInt();

      System.out.println(
        "Please enter a numerical digit(s) for the 'y' co-ordinate"
      );
      userY = scanner.nextInt();

      isAlive = isUserStillAlive(grid, userX, userY);

      if (isAlive == true) {
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
          arrayYValue,
          userX,
          userY
        );

        updateGrid(display, surroundingCells, minesPerCell, userX, userY);
        ClearTerminal();
        displayGrid(arrayXValue, arrayYValue, display);
        totalSafeCellsCounter++;

        if (totalSafeCellsCounter == totalSafeCells) {
          String[] winMessages = new String[7];
          winMessages[0] =
            "The field has been cleared of mines, well done! Play again? Y/N";
          winMessages[1] =
            "You've managed to find all the mines! Great job! Play again? Y/N";
          winMessages[2] = "Not bad... Play again? Y/N";
          winMessages[3] = "You're pretty good at this. Play again? Y/N";
          winMessages[4] =
            "Diligence and a bit of effort goes a long way, good job. Play again? Y/N";
          winMessages[5] = "You can do better! Play again? Y/N";
          winMessages[6] =
            "Visit https://landminefree.org/ for more information on how to make the world a mine-free place <3\nPlay again? Y/N";

          java.util.Random numGenerator = new java.util.Random();
          int randNum = numGenerator.nextInt(winMessages.length);

          System.out.printf("\n%s\n", winMessages[randNum]);
          if (totalSafeCellsCounter > 100) {
            System.out.println(
              "P.S. You've matched Magawa the bomb-sniffing's rat record!"
            );
          }
          String yesOrNo = scanner.next().toLowerCase();

          if (yesOrNo.equals("y")) {
            ClearTerminal();
            rerunMain();
          } else {
            System.out.println("Thanks for playing!");
            System.out.println("Exiting Application...");
          }
        }
      } else if (isAlive == false) {
        System.out.println("\n___.                          ");
        System.out.println("\\_ |__   ____   ____   _____  ");
        System.out.println(" | __ \\ /  _ \\ /  _ \\ /     \\ ");
        System.out.println(" | \\_\\ (  <_> |  <_> )  Y Y  \\");
        System.out.println(" |___  /\\____/ \\____/|__|_|  /");
        System.out.println("     \\/                    \\/ \n");

        String[] failMessages = new String[7];
        failMessages[0] = "You lose, try again? Y/N";
        failMessages[1] =
          "Magawa the rat helped disable over 100 mines by smell, is a rat better than you?\nTry Again? Y/N";
        failMessages[2] = "Better luck next time! Try again? Y/N";
        failMessages[3] = "Maybe this isn't your thing... try again? Y/N";
        failMessages[4] = "You're better than this! Try again! Y/N";
        failMessages[5] = "Never be game over, try again? Y/N";
        failMessages[6] =
          "Visit https://landminefree.org/ for more information on how to make the world a mine-free place <3\nTry again? Y/N";

        java.util.Random numGenerator = new java.util.Random();
        int randNum = numGenerator.nextInt(failMessages.length);

        System.out.printf("\n%s\n", failMessages[randNum]);
        String yesOrNo = scanner.next().toLowerCase();

        if (yesOrNo.equals("y")) {
          ClearTerminal();
          rerunMain();
        } else {
          System.out.println("Thanks for playing!");
          System.out.println("Exiting Application...");
        }
      }
    }
  }

  // -------------------- DETERMINE IF IT'S A MINE OR NOT --------------------

  public static void ClearTerminal() throws IOException, InterruptedException {
    // DESC: Clear the terminal on Windows/Linus/macOS
    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
  }

  public static void rerunMain() throws Exception {
    main(new String[] {});
  }

  public static boolean isUserStillAlive(Cell[][] grid, int userX, int userY) {
    // DESC: If user input is mine they're dead
    if (grid[userX][userY].getCellType() == "mine") {
      return false;
    }
    return true;
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
          secondIndexStarter = y;
        } else if (omitBottomRow == true && omitLeftColumn == true) {
          firstIndexStarter = x;
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
    int arrayYValue,
    int userX,
    int userY
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
        .filter(arr -> {
          if (
            arr[0] >= 0 &&
            arr[0] <= arrayXValue - 1 &&
            arr[1] > 0 &&
            arr[1] <= arrayYValue - 1
          ) {
            return true;
          }

          return false;
        })
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
    int[] mineArr,
    int userX,
    int userY
  ) {
    // DESC: Match mineCount to surroundingCell coords
    // (surroundingCells are cells around USER chosen co-ords/input)
    for (int i = 0; i < coordsArr.length; i++) {
      if (display[coordsArr[i][0]][coordsArr[i][1]] != -1) {
        display[coordsArr[i][0]][coordsArr[i][1]] = mineArr[i];
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
      if (displayX == arrayXValue - 1) {
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

    for (int i = 0; i < arrayXValue; i++) {
      System.out.print("  | ");
    }
    System.out.println();

    for (int i = 0; i < arrayXValue; i++) {
      System.out.printf("  %d ", i);
    }
    System.out.println("\n");
  }
}
