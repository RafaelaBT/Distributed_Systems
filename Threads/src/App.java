import ThreadDemo.ThreadDemo;

public class App {
    public static void main(String[] args) throws Exception {
        // Creates a thread (instance/object from ThreadDemo)
        ThreadDemo td1 = new ThreadDemo("ThreadDemo 1");
        ThreadDemo td2 = new ThreadDemo("ThreadDemo 2");

        // Execute the thread, calling the run() method from ThreadDemo.
        td1.start();
        td2.start();
    }
}
