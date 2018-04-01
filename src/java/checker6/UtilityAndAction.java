package checker6;

public class UtilityAndAction {
    private final Action action;
    private final double utility;

    public UtilityAndAction(Action action, double utility) {
        this.action = action;
        this.utility = utility;
    }

    public UtilityAndAction() {
        this(null, 0);
    }

    public Action getAction() {
        return action;
    }

    public double getUtility() {
        return utility;
    }
}
