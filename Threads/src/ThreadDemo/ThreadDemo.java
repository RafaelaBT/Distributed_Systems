package ThreadDemo;

/** Thread class - inherit from Thread (extends the Thread interface).
 * Threads execute multiples process/activities at the same time (concurrent) utilizing multiples CPUs (cores).
*/
public class ThreadDemo extends Thread{
    // ThreadDemo atributes.
    private String threadName;

    /**
     * ThreadDemo constructor.
     * @param name - the thread name.
     */
    public ThreadDemo(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Thread execution method.
     * Prints the thread name 4 times in a 50ms interval.
    */
    @Override
    public void run() {
        try {
            for (int i = 4; i > 0; i--) {
                System.out.println("T:"+threadName+" "+i);
                Thread.sleep(50);
            }
        } catch (Exception e) {
            System.out.println("Another thread is not supported.\n Exception "+e);
        }
    }
}