import java.util.concurrent.Semaphore;

public class c3244203A2P1
{
    int north = 2;
    int south = 2;

    public static void main(String[] args) 
    {
        farmer myClass = new farmer();
        myClass.start();

        farmer myClass1 = new farmer();
        myClass1.start();

        farmer myClass2 = new farmer();
        myClass2.start();

        farmer myClass3 = new farmer();
        myClass3.start();

        System.out.println("working");
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

class farmer extends Thread 
{
    public void run()
    {
        c3244203A2P1 v = new c3244203A2P1();


        for(int i=0; i<v.getNorth(); i++)
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