package checker6;


public class Utility {
    
    private final static int NUMBER_OF_CHAR_PER_LINE = 27;
    
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i),radix) < 0) {
                return false;
            }
        }
        return true;
    }

    public static int positionToIndex(Position position) {
        return NUMBER_OF_CHAR_PER_LINE * (position.getX() * 2 + 2) + (4 + position.getY() * 4);
    }
}
