import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class c3244203A2P1 {

    public static void main(String[] args) {

        //Reads File from input
        //CAUSE I REMEMBERED THIS TIME

        //true false
        boolean success = false;    
        //setting variables for num of farmers
        int N=0,S=0;
        //data from the file
        String[] input;
        //scanner saves next line file name into data
        Scanner data = new Scanner(System.in);
        System.out.println("Enter file name eg input.txt: ");

        //loops till it finds a valid file
        while (!success) 
        {
            //try catch for getting the correct file
            try 
            {
                //changes line into string
                String str = data.nextLine();
                //converts the string name of the file into an actual file 
                //that the code can read
                Scanner file = new Scanner(new File(str));    

                //try catch for getting correct contents of the file
                try 
                {
                    //split by  into string array outside while loop
                    input = file.nextLine().split("\\s+");
                    //set number of north and south farmers

                    //set number of north and south farmers
                    //input 0  and 1 both only have integers, cause the other values
                    //have been removed, if the order of N and S is changed, program will
                    //run in reverse
                    N = Integer.parseInt(input[0].replaceAll("[^0-9]+",""));    
                    S = Integer.parseInt(input[1].replaceAll("[^0-9]+",""));

                    //break loop
                    success = true;
                } 
                catch (NoSuchElementException e) 
                {
                    System.out.println("Empty file, or invalid contents!");
                }
                file.close();
            } 
            catch (FileNotFoundException e) 
            { 
                System.out.println("File not found! Please give a valid file!");
            }
        }
        //close file
        data.close();

        //Create the bridge
        //starvePre to prevent starvation
        Bridge bridge = new Bridge(N+S); 
        //Instantiate an array of Farmers
        Farmer[] f = new Farmer[N+S];   
        Thread th;
        //create North farmers
        for (int i=0; i<N; i++)
        {
            //creates north farmers
            f[i] = new Farmer("N_Farmer"+(i+1),"North",bridge);

            //makes the farmer a thread then starts it
            th = new Thread(f[i]);
            th.start();
        }
        //create South farmers
        for (int i=N; i<S+N; i++) 
        {
            //creates south farmers
            f[i]= new Farmer("S_Farmer"+(i-N+1),"South",bridge);

            //makes the farmer a thread then starts it
            th = new Thread(f[i]);
            th.start();
        }

    }//end main
}//end class

class Farmer implements Runnable
{
    //Home island
    private String home;
    //current island
    private String location;    
    //destination its trying to get to
    private String destination; 
    //name
    private String id;          
    //bridge that connects the islands   
    private Bridge bridge; 

    //checks how many times its crossed
    private int crossed;

    //constructor
    public Farmer(String id, String location, Bridge bridge) 
    {
        //for future reference this means the object itself
        //aka the farmer for this
        this.id = id;
        this.location = location;
        this.home = location;
        this.bridge = bridge;
        this.crossed = 0;

        if(location=="North")
        {
            destination = "South"; 
        } 
        else 
        {
            destination = "North";
        }

        System.out.println(id + ": Waiting for bridge. Going towards " + destination);  

    }

    //getters
    public String getLocation() 
    {
        return location;
    }
    public String getID() 
    {
        return id;
    }
    public int getCrossed()
    {
        return crossed;
    }

    //setters
    public void setLocation(String location)
    {
        this.location = location;
    }
    public void setDestination(String destination)
    {
        this.destination = destination;
    }
    public void upCrossed()
    {
        this.crossed++;
    }

    //run function
    @Override   
    public void run() 
    {
        while(true)
        {
            //starvation prevention using counter system
            if(this.crossed <= bridge.getStarve())
            { 
                bridge.crossBridge(this);
                try 
                {
                    TimeUnit.MILLISECONDS.sleep(500);
                } 
                catch (InterruptedException e) {}

                System.out.println(this.id + ": Waiting for bridge. Going towards " + destination); 
            }
            //waits a bit before it retries
            else
            {
                try 
                {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } 
                catch (InterruptedException e) {}
            }
        }
    }  

}//end class

class Bridge 
{
    //count the number of total crossings
    private int neon;   
    //the bridge is the semaphore
    private static Semaphore bridgeSem;
    //various counters to prevent starvation
    private int starvePre = 0;
    private int counter = 0;
    private int farmNum;


    //Constructor
    public Bridge(int farmNum) 
    {
        this.neon=0;
        this.farmNum = farmNum;
        //one bridge resource, mutual exclusivity
        bridgeSem = new Semaphore(1);   
    }

    //Getters
    public int getNeon()
     {
        return neon;
    }

    public synchronized int getStarve()
    {
        return starvePre;
    }

    //Methods
    public synchronized void upNeon() {
        neon++;
        System.out.println("NEON = " + getNeon());
    }

    public void crossBridge(Farmer f)
     { 
        //Semaphore acquire
        try 
        {   
            //semaphore allows access
            bridgeSem.acquire();    
            //prints the crossing and sleep in between for better reading
            System.out.println(f.getID()+": Crossing bridge Step 5.");
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println(f.getID()+": Crossing bridge Step 10.");
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println(f.getID()+": Crossing bridge Step 15.");
            TimeUnit.MILLISECONDS.sleep(500);

            //Sleep for 500 milliseconds ,improves readability
            try 
            {
                Thread.sleep(200);
            } 
            catch (InterruptedException e) {} //No interrupts implemented, so thread shouldn't be interrupted?

            //another required print result
            System.out.println(f.getID()+": Across the Bridge.");
            TimeUnit.MILLISECONDS.sleep(500);

            //this segments reverses the location and destination
            if(f.getLocation() == "North")
            {
                f.setLocation("South");
                f.setDestination("North");
            }
            else
            {
                f.setLocation("North");
                f.setDestination("South");
            }

            //increment NEON counter, synchronized to avoid print conflicts
            upNeon();  
            counter++;
            //BUG TESTING
            //System.out.println("!!!!!!!counter "+ counter);

            //once counter goes through all the farmer, it resets letting farmers who have already crossed
            //also cross
            if(counter == farmNum)
            {
                starvePre++;
                counter = 0;
            }

            f.upCrossed();

            //bug testing for starvation
//            System.out.println(f.getID()+": crossed "+ f.getCrossed() +" many times");

            //Sleep for 500 milliseconds ,improves readability
            try
            {
                TimeUnit.MILLISECONDS.sleep(500);
            }
            catch (InterruptedException e) {} //No interrupts implemented, so thread shouldn't be interrupted?
        }
        catch (InterruptedException e) {} 

        //semaphore is released
        bridgeSem.release();
    }
}//end class