package checker6;

public class Position {
    
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        this(-1, -1);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Position that = (Position) obj;
        return that.getX() == this.getX() && that.getY() == this.getY();
    }

    @Override public int hashCode() {
        int result = 31 * getX();
        result = 31 * result + getY();
        return result;
    }

    @Override public String toString() {
        return String.format("(%d,%d)", getX(), getY());
    }
}
