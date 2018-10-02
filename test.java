import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class test
{
    public static void main(String[] args)
    {
        //creates the bridge 
        final Bridge bridge = new Bridge();

        int nNum =2;
        int sNum =2;

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
                        //farmer is set to cross the bridge
                        Farmer farmer = new Farmer(bridge);
                        //farmer is set as a thread
                        Thread th = new Thread(farmer);
                        //farmers name is set
                        farmer.setName("N_Farmer" + i);
                        //he now begins he's pointless journey
                        th.start();
                        
                        //try catch
                        try 
                        {
                            TimeUnit.SECONDS.sleep((long)(Math.random() * 10));
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
                            TimeUnit.SECONDS.sleep((long)(Math.random() * 10));
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
    private int neonSign = 0;

    public Bridge()
    {
        semaphore = new Semaphore(1);
    }

    public void crossBridge(Farmer farmer)
    {
        try 
        {
            long du = (long)(Math.random() * 10);

            System.out.printf("%s : Wating for bridge. Going towards ??.\n",farmer.getName());
            semaphore.acquire();
            System.out.printf("%s : Crossing bridge Step 5.\n",farmer.getName());
            TimeUnit.SECONDS.sleep(du);
            System.out.printf("%s : Crossing bridge Step 10.\n",farmer.getName());
            TimeUnit.SECONDS.sleep(du);
            System.out.printf("%s : Crossing bridge Step 15.\n",farmer.getName());
            TimeUnit.SECONDS.sleep(du);
        } 
        catch (InterruptedException iex) 
        {
            //TODO: handle exception
            iex.printStackTrace();
        }
        finally
        {
            System.out.printf("%s : Across the bridge.\n",farmer.getName());
            //neonSign is not updating
            neonSign++;
            System.out.println("Neon = " + neonSign);
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