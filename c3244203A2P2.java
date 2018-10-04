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
//  this is the section with the bugg

//
                //changes line into string
                String str = data.nextLine();
                //converts the string name of the file into an actual file 
                //that the code can read
                Scanner file1 = new Scanner(new File(str));
                //try catch for getting correct contents of the file

//
                try 
                {
                    //split by  into string array outside while loop
                    //split by  into string array outside while loop
                    while(file1.hasNext())
                    {
                        token1 = file1.next();
                        input.add(token1);
                        //input = file.nextLine().split("\\s+");
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
        System.out.println("starting to create customers");

        //creating customers
        Customer[] c = new Customer[cNum];
        //creating 5 seats
        //
        //
        //Still not sure if i should make this into an array or not
        Seat s;
        Thread th;

        for(int i = 0; i<cNum;i++)
        {
            //creates the customer with values
            c[i] = new customer(id.get(i), arrives.get(i), duration.get(i));

            //makes customer into a thread
            th = new Thread(c[i]);
            th.start();
        }

    }//end of main
}//end of class

//thread
class Customer implements Runnable
{
    private String id;
    private int arrives;
    private int sits;
    private int duration;
    private boolean exit = false;
    private int leaves;

    public customer(String id, int arrives, int duration, Seat seat)
    {
        this.id = id;
        this.arrives = arrives;
        this.duration = duration;
    }

    //Getters

    //Setters
    public void setLeaves(int leaves)
    {
        this.leaves = leaves;
    }
    public void setSits(int sits)
    {
        this.sits = sits;
    }

    @Override
    public void run()
    {
        while(exit != true)
        {
            if(this.arrives <= getClock() && seat.getSeatAvailable())
            {
                System.out.println("customer is running");
                seat.eatIceCream(this);
            }
            
        }
        
    }
}//end of class

//uses semaphore
class Seat
{
    private static Semaphore seatSem;
    private int clockCounter = 0;
    private boolean seatAvailable;

    public seat()
    {
        seatSem = new Semaphore(5);
        seatAvailable = true;
    }

    //Getters
    public synchronized int getClock()
    {
        return clockCounter;
    }

    public synchronized boolean getSeatAvailable()
    {
        return seatAvailable;
    }

    public synchronized void eatIceCream(Customer c)
    {
        //if 5 permits then reset flag system
        if(seatSem.availablePermits()==5)
        {
            seatAvailable = true;
        }
        //if zero permits, that means all seats are full
        if(seatSem.availablePermits()==0)
        {
            seatAvailable = false;
        }

        //aquire semaphore
        seatSem.acquire();

        //counter variable for the  arrival and leave times
        clockCounter++;

        //release semaphore
        seatSem.release();
    }


    //Setters
}//end of class