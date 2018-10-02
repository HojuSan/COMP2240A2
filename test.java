import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class test
{
    public static void main(String[] args)
    {
        final Bridge bridge = new Bridge();
        int nNum =4;
        int sNum =4;

        //Creates north farmer threads
        Thread thNorthbound = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    //generates the farmers based off input
                    for(int i = 1; i <= nNum;i++)
                    {
                        Farmer farmer = new Farmer(bridge);
                        Thread th = new Thread(farmer);
                        farmer.setName("N_Farmer" + i);
                        th.start();
                        
                        try 
                        {
                            TimeUnit.SECONDS.sleep((long)(Math.random()*10));
                        } 
                        catch (InterruptedException iex) 
                        {
                            //TODO: handle exception
                            iex.printStackTrace();
                        }
                    }
                }
            }
        });

        //Creates south farmer threads
        Thread thSouthbound = new Thread( new Runnable() 
        {
            @Override
            public void run() {
                 
                while(true)
                {
                    //generates the farmers based off input
                    for(int i = 1; i <= sNum;i++)
                    {
                        Farmer farmer = new Farmer(bridge);
                        Thread th = new Thread(farmer);
                        farmer.setName("S_Farmer"+i);
                        th.start();
                        try
                        {
                            TimeUnit.SECONDS.sleep((long)(Math.random()*10));
                        }
                        catch(InterruptedException iex)
                        {
                            iex.printStackTrace();
                        }
                    }
                }
            }
        });

        thNorthbound.start();
        thSouthbound.start();
    }
}

class Bridge
{
    private final Semaphore semaphore;

    public Bridge()
    {
        semaphore = new Semaphore(1);
    }

    public void crossBridge(Farmer farmer)
    {
        try 
        {
            System.out.printf("%s : Wating for bridge. Going towards ??.\n",farmer.getName());
            semaphore.acquire();
            System.out.printf("%s :is crossing the bridge.\n",farmer.getName());
            long duration = (long)(Math.random() * 10);
            TimeUnit.SECONDS.sleep(duration);
        } 
        catch (InterruptedException iex) 
        {
            //TODO: handle exception
            iex.printStackTrace();
        }
        finally
        {
            System.out.printf("Farmer %s has crossed the bridge.\n",farmer.getName());
            semaphore.release();
        }
    }
}

class Farmer implements Runnable
{
    private String name;
    private Bridge bridge;
     
    public Farmer(Bridge bridge)
    {
        this.bridge = bridge;
    }
     
    public void run()
    {
        bridge.crossBridge(this);
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
}