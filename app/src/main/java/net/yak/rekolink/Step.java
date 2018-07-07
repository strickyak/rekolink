package net.yak.rekolink;

/**
 * Created by strick on 10/25/17.
 */
class Step {
    int step;
    String description;
    Complainer.Starter doer;
    Complainer.Stopper undoer;

    Step(int step, String description, Complainer.Starter doer, Complainer.Stopper undoer) {
        this.step = step;
        this.description = description;
        this.doer = doer;
        this.undoer = undoer;
    }
}
