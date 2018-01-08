/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.core.simulation;

public class SysLock {

    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }
}