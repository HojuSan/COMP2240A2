import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//main class
public class c3244203A2P2
{
    public static void main(String[] args) 
    {
        System.out.println("shit is running as it should");
    }
}

//thread
class customer
{
    private String id;
    private int arrives;
    private int duration;
    private int leaves;

    public customer(String id, int arrives, int duration)
    {
        this.id = id;
        this.arrives = arrives;
        this.duration = duration;
    }

    public void run()
    {
        System.out.println("customer is running as it should");
    }
}

//uses semaphore
class seat
{
    public seat()
    {

    }
}