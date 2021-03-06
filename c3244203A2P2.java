import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.lang.String;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

//main class
public class c3244203A2P2
{
    public static void main(String[] args) throws IOException
    {
        //Reads File from input
        //CAUSE I REMEMBERED THIS TIME

        //true false
        boolean success = false;    
        //setting variables for num of customers
        int cNum=0;
        //data from the file
        List<String> input = new LinkedList<String>();
        String token1 = "";
        List<String> id = new LinkedList<String>();
        List<Integer> arrives  = new ArrayList<Integer>();
        List<Integer> duration  = new ArrayList<Integer>();
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
                Scanner file1 = new Scanner(new File(str));
                //try catch for getting correct contents of the file/
                try 
                {
                    //split by  into string array outside while loop
                    while(file1.hasNext())
                    {
                        token1 = file1.next();
                        input.add(token1);
                    }                  

                    for(int i = 0; i < input.size(); i+=3)
                    {
                        //better to use this for string comparison
                        //equal is 0
                        if(input.get(i).compareToIgnoreCase("end") == 0)
                        {
                            //break loop if END
                            success = true;
                            break;
                        }

                        //Bug is here only loops once
                        arrives.add(Integer.parseInt(input.get(i)));
                        id.add(input.get(i+1));
                        duration.add(Integer.parseInt(input.get(i+2)));
                        cNum++;

                    }

                } 
                catch (NoSuchElementException e) 
                {
                    System.out.println("Empty file, or invalid contents!");
                }
                file1.close();
            } 
            catch (FileNotFoundException e) 
            { 
                System.out.println("File not found! Please give a valid file!");
            }
        }
        //close file
        data.close();

        //bug testing
        //System.out.println("starting to create customers");

        //creating customers
        Customer[] c = new Customer[cNum];
        //making and starting the timer
        Timer timer1 = new Timer(cNum);
        Thread timeThread;
        timeThread = new Thread(timer1);
        timeThread.start();

        Seat seat1 = new Seat(timer1);
        Thread th;

        System.out.println("Customer\tarrives\tSeats\tLeaves ");

        //loop to start all the customers
        for(int i = 0; i<cNum;i++)
        {
            try 
            { 
                //helps threads arriving at the same time, first in first serve
                Thread.sleep(1); 
            } 
            catch (InterruptedException e) 
            { 
                // TODO Auto-generated catch block 
                e.printStackTrace(); 
            } 

            //creates the customer with values
            c[i] = new Customer(id.get(i), arrives.get(i), duration.get(i), seat1, timer1);

            //makes customer into a thread
            th = new Thread(c[i]);
            th.start();
        }

    }//end of main
}//end of class

//customer is a thread
class Customer implements Runnable
{
    //variables
    private String id;
    private int arrives;
    private int sits;
    private int duration;
    private boolean exit = false;
    private int leaves;
    private Seat seat;
    private Timer timer;

    //constructor
    public Customer(String id, int arrives, int duration, Seat seat, Timer timer)
    {
        this.id = id;
        this.arrives = arrives;
        this.duration = duration;
        this.seat = seat;
        this.timer = timer;
    }

    //Getters
    public String getId()
    {
        return id;
    }
    public int getArrives()
    {
        return arrives;
    }
    public int getSits()
    {
        return sits;
    }
    public int getLeaves()
    {
        return leaves;
    }
    public int getDuration()
    {
        return duration;
    }

    //Setters
    public void setLeaves(int leaves)
    {
        this.leaves = leaves;
    }
    public void setSits(int sits)
    {
        this.sits = sits;
    }
    public void setExit()
    {
        this.exit = true;
    }

    //run function
    @Override
    public void run()
    {
        while(exit != true)
        {

            try 
            {
                Thread.sleep(1);
            } 
            catch (Exception e)
            {
                //TODO: handle exception
            }

            if(this.arrives <= timer.getClock() && seat.getSeatAvailable())
            {
                //begins eating the ice cream
                seat.eatIceCream(this);
            }
            
        }
        
    }
}//end of class

//uses semaphore
class Seat
{
    //variables
    private static Semaphore seatSem;
    private boolean seatAvailable;
    private Timer timer;

    //constructor
    public Seat(Timer timer)
    {
        seatSem = new Semaphore(5);
        seatAvailable = true;
        this.timer = timer;
    }

    //public synchronized boolean getSeatAvailable()
    public boolean getSeatAvailable()
    {
        return seatAvailable;
    }

    //public synchronized void eatIceCream(Customer c)
    public void eatIceCream(Customer c)
    {
        try 
        {
            //aquire semaphore
            seatSem.acquire();

            //if zero permits, that means all seats are full
            if(seatSem.availablePermits()==0)
            {
                seatAvailable = false;
            }

            //setting the time when customer sits
            c.setSits(timer.getClock());


            //setting the time when customer leaves
            c.setLeaves(c.getSits()+c.getDuration());

            //printing the values
            System.out.println(c.getId()+"\t\t"+c.getArrives() +"\t"+c.getSits() +"\t"+c.getLeaves() +" ");

            //thread is eating its delicious icecream
            Thread.sleep(200*c.getDuration());
            //thread exits
            c.setExit();
            //used to end the timer
            timer.upExitedCustomer();
           
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }

        //release semaphore
        seatSem.release();

        //if 5 permits then reset flag system
        if(seatSem.availablePermits()==5)
        {
            seatAvailable = true;
        }

    }


 
}//end of class

//timer for duration calculation
class Timer implements Runnable
{
    //variables
    int clockCounter;
    int exitedCustomer = 0;
    int customerNum;

    //constructor
    Timer(int customerNum)
    {
        clockCounter = 0;
        this.customerNum = customerNum;
    }

    //getters
    //public synchronized int getClock()
    public int getClock()
    {
        return clockCounter;
    }

    //setters
    public void upExitedCustomer()
    {
        exitedCustomer++;
    }

    //run function
    public void run()
    {
        //while customers still exist run
        while(exitedCustomer != customerNum) 
        { 
                //increases clock counter after a second
                //to allow more threads to try and access
                try 
                { 
                    Thread.sleep(200); 
                    clockCounter++; 
                } 
                catch (InterruptedException e) 
                { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                } 
            
            
        }
    }
}