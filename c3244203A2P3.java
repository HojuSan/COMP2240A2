import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.lang.String;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

//main class
public class c3244203A2P3
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


                //try catch for getting correct contents of the file
                try 
                {
                    //split by  into string array outside while loop
                    //split by  into string array outside while loop
                    while(file1.hasNext())
                    {
                        token1 = file1.next();
                        input.add(token1);
                        //System.out.println(token1);
                    }                  

                    //saves the amount of customers
                    cNum = Integer.parseInt(input.get(0));

                    //saves all the values into lists
                    for(int i = 1; i < input.size(); i+=2)
                    {

                        id.add(input.get(i));
                        duration.add(Integer.parseInt(input.get(i+1)));

                    }

                    //finishes the file reading
                    success = true;

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

        //creating customers
        Customer[] c = new Customer[cNum];
        //creating 5 seats
        //
        //
        //Still not sure if i should make this into an array or not
        Timer timer1 = new Timer(cNum);
        Thread timeThread;
        timeThread = new Thread(timer1);
        timeThread.start();

        CoffeeMachine cm = new CoffeeMachine(timer1);
        Thread th;

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
            c[i] = new Customer(id.get(i), duration.get(i), i,cm, timer1);

            //makes customer into a thread
            th = new Thread(c[i]);
            th.start();
            //System.out.println("order"+c[i].getOrder());
        }
    }
    
}

class Customer implements Runnable
{

    //variables
    private Timer timer;
    private CoffeeMachine cm;
    private String id;
    private int duration;
    private ReentrantLock r;
    private boolean finished;
    private int order;
    private int counter;

    //constructor
    Customer(String id, int duration, int order, CoffeeMachine cm, Timer timer)
    {
        this.order = order;
        this.id = id;
        this.duration = duration;
        this.cm = cm;
        this.timer = timer;
        r = new ReentrantLock();
        finished = false;
        counter = 0;

    }

    //Setters
    public void cFinished()
    {
        finished = true;
    }

    //Getters
    public String getId()
    {
        return this.id;
    }
    public int getDuration()
    {
        return this.duration;
    }
    public int getOrder()
    {
        return this.order;
    }


    //reentrant lock, even if multiple threads have it, only
    //the first thread that gains access will be able to lock and have access
    //the other threads have to wait before they can do shit

    // System.out.println("Here");

    public synchronized void run()
    {
        //as long as coffee hasn't brewed
        while(finished!=true)
        {
            //checks if its the order else, wait a bit try again
            if(this.order == cm.getCounter() && cm.getU()<3)
            {
                //seats are full so wait then try again
                while(cm.getU()==3)
                {
                    //Sleep for 500 milliseconds 
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {}
                    break;
                }

                
                //seats empty then begin brewing 
                if(cm.getU()==0 )
                {
                    //hot coffee only
                    if(this.id.contains("H"))
                    {
                        cm.hA();
                    }
                    //cold coffee only
                    else if(this.id.contains("C")) 
                    {
                        cm.cA();
                    }
                }

                
                //if already something is brewing
                if(cm.getU()>0)
                {
                    //System.out.println("Here");
                    //hot coffee brewing then cold order comes in
                    if(this.id.contains("C")&&cm.getH()==true)//location of update
                    {
                        //locks the thread
                        r.lock();
                        while(finished!=true)
                        {
                            
                            try
                            {
                                Thread.sleep(1);
                            }
                            catch (InterruptedException e) {}
                            
                            if(cm.getU()==0)
                            {
                                try
                                {
                                    Thread.sleep(1);
                                }
                                catch (InterruptedException e) {}
                                //System.out.println("Cold got locked out then started");
                                //reverses the coffee allowed to brew
                                cm.cA();
                                cm.upCounter();
                                cm.serveCoffee(this);
                                
                                r.unlock();
                                break;
                            }
                            
                        }
                    }
                    //cold coffee brewing then hot order comes in
                    else if(this.id.contains("H")&&cm.getC()==true) //location of update
                    {
                        //locks the thread
                        r.lock();
                        cm.hA();
                        while(finished!=true)
                        {
                            try
                            {
                                Thread.sleep(1);
                            }
                            catch (InterruptedException e) {}

                            if(cm.getU()==0)
                            {
                                try
                                {
                                    Thread.sleep(1);
                                }
                                catch (InterruptedException e) {}
                                //System.out.println("hot got locked out then started");
                                //reverses the coffee allowed to brew
                                cm.hA();
                                cm.upCounter();
                                cm.serveCoffee(this);
                                
                                r.unlock();
                                break;
                            }
                        }
                    }
                    //just serves h or c if already serving
                    else if(this.id.contains("H")&&cm.getH()==true||this.id.contains("C")&&cm.getC()==true)
                    {
                        cm.upCounter();
                        cm.serveCoffee(this);
                    }
                }
                //just serves h or c if already serving
                else if(this.id.contains("H")&&cm.getH()==true||this.id.contains("C")&&cm.getC()==true)
                {
                    cm.upCounter();
                    cm.serveCoffee(this);
                }
            }
            else
            {
                //System.out.println("counter "+this.order);
                //Sleep for 500 milliseconds ,improves readability
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {} 
            }

        }//end of the while loop
                     
    }//run function
}//end of class

class CoffeeMachine
{

    //variables
    private Customer c;
    private Timer timer;
    private boolean hAcess;
    private boolean cAcess;
    private int mUsed;
    private int counter;
    private boolean d1,d2,d3,notPrinted;

    

    //constructor
    CoffeeMachine(Timer timer)
    {
        this.timer = timer;
        this.hAcess = false;
        this.cAcess = false;
        this.mUsed = 0;
        this.counter = 0;
        this.d1 =false;
        this.d2 =false;
        this.d3 =false;
        this.notPrinted =false;

    }

    //setters
    //hot access
    public void hA()
    {
        hAcess = true;
        cAcess = false;
    }
    //cold access
    public void cA()
    {
        hAcess = false;
        cAcess = true;
    }
    public void upCounter()
    {
        counter++;
    }

    //getters
    public boolean getH()
    {
        return hAcess;
    }
    public boolean getC()
    {
        return cAcess;
    }
    public int getU() 
    {
        return mUsed;
    }
    public int getCounter()
    {
        return counter;
    }
    
    //this is where the magic happens
    public void serveCoffee(Customer c)
    {
        int disNum =0;
        try 
        {

            mUsed++;

            if(d1==false)
            {
                disNum = 1;
                d1=true;
            }
            else if(d2==false)
            {
                disNum = 2;
                d2=true;
            }
            else if(d3==false)
            {
                disNum = 3;
                d3=true;
            }

            //System.out.println("mUsed is :"+mUsed+" hCounter is :"+hCounter+" cCounter is :"+cCounter);
            System.out.println("("+timer.getClock()+") "+ c.getId()+ " uses dispenser " + disNum + "(time: "+ c.getDuration()+")");

            //thread is brewing delicious coffee
            Thread.sleep(500*c.getDuration());
            //System.out.println("did it");
        
        } 
        catch (Exception e) 
        {
            //TODO: handle exception
        }
        

        if(disNum == 1)
        {
            d1=false;
        }
        else if(disNum == 2)
        {
            d2=false;
        }
        else if(disNum == 3)
        {
            d3=false;
        }
        c.cFinished();
        //seats being used becomes available
        mUsed--;
        timer.upExit();
        if(timer.getExited() == timer.getCustomerNum() && notPrinted==false)
        {
            notPrinted =true;
            System.out.println("("+timer.getClock()+")  DONE");
        }

    }
}

class Timer implements Runnable
{
    //variables
    int customerNum;
    int clockCounter;
    int exitedCustomer;


    //constructor
    Timer(int customerNum)
    {
        clockCounter = 0;
        this.customerNum = customerNum;
        this.exitedCustomer = 0;
    }

    //Setters
    public void upExit()
    {
        exitedCustomer++;
    }

    //Getters
    public int getClock()
    {
        return clockCounter;
    }
    public int getExited()
    {
        return exitedCustomer;
    }
    public int getCustomerNum()
    {
        return customerNum;
    }

    //not functional yet
    public void run()
    {
        //while customers still exist run
        while(exitedCustomer != customerNum) 
        { 
            //increases clock counter after a second
            //to allow more threads to try and access
            try 
            { 
                //1second
                Thread.sleep(500); 
                clockCounter++; 
                //bug testing
                //System.out.println("The time: ("+clockCounter+")");
            } 
            catch (InterruptedException e) 
            { 
                // TODO Auto-generated catch block 
                e.printStackTrace(); 
            } 
            
            
        }
        
    }
}
