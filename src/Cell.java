public class Cell {
    String cellType;

    // Bool instead of string
    // Bomb, revealed, neighbours, coords
    public void setToMine() {
        cellType = "mine";
    }

    public void setToSafeCell() {
        cellType = "safe";
    }

    public String getCellType() {
        return cellType;
    }

    
}
