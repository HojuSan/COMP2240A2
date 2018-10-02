import java.util.concurrent.Semaphore;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Random;
/*
class farmer extends Thread 
{
    public void run()
    {
        c3244203A2P1 v = new c3244203A2P1();


        for(int i=1; i<=v.getNorth(); i++)
        {
            System.out.println(Thread.currentThread().getId() + "Value " + i);
        }

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
}
*/

public class c3244203A2P1
{
    private static BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);

    int north = 2;
    int south = 2;

    public static void main(String[] args) throws InterruptedException
    {
        Thread t1 = new Thread (new Runnable()
        {
            public void run()
            {
                try 
                {
                    producer();
                } catch (InterruptedException e) 
                {
                    //TODO: handle exception
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread (new Runnable()
        {
            public void run()
            {
                try 
                {
                    consumer();
                } catch (InterruptedException e) 
                {
                    //TODO: handle exception
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        
    }

    private static void producer() throws InterruptedException
    {
        Random random = new Random();

        while (true)
        {
            queue.put(random.nextInt(100));
        }        
    }

    private static void consumer() throws InterruptedException
    {
        Random random = new Random();

        while (true)
        {
            Thread.sleep(100);

            if(random.nextInt(10) == 0)
            {
                Integer value = queue.take();

                System.out.println("Taken value: " + value + "; Queue size is: " + queue.size());
            }
        }        
    }







    public int getNorth()
    {
        return north;
    }

    public int getSouth()
    {
        return south;
    }
}
