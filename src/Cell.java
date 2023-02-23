public class Cell {
    String cellType;

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
