import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class test
{
    public static void main(String[] args)
    {
        final Bridge bridge = new Bridge();

        Thread thNorthbound = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    Farmer farmer = new Farmer(bridge);
                    Thread th = new Thread(farmer);
                    farmer.setName("North Farmer : " + th.getId());
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
        });

        Thread thSouthbound = new Thread( new Runnable() {
             
            @Override
            public void run() {
                 
                while(true)
                {
                    Farmer farmer = new Farmer(bridge);
                    Thread th = new Thread(farmer);
                    farmer.setName("South Farmer : "+th.getId());
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
        });

        thNorthbound.start();
        thSouthbound.start();
    }
}