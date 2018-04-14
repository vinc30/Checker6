package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

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
