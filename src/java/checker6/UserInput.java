package checker6;


public class UserInput {

    private int serialNum;
    private Position position;

    public UserInput(int serialNum, Position position) {
        this.serialNum = serialNum;
        this.position = position;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public Position getPosition() {
        return position;
    }
}
