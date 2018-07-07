package net.yak.rekolink;

/**
 * Created by strick on 10/22/17.
 */
interface Complainer {

    String run();  // Return "" on success, else return error string.

    interface Starter extends Complainer {
    }

    interface Stopper extends Complainer {
    }
}
