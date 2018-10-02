import java.util.concurrent.Semaphore;

class MyClass extends Thread
{
    public void run()
    {
        for(int i=0; i<10; i++)
        {
            System.out.println("Value " + i);
        }
    }

}

public class c3244203A2P1
{

    public static void main(String[] args) 
    {
        MyClass myClass = new MyClass();
        myClass.run();

        System.out.println("working");
    }
}