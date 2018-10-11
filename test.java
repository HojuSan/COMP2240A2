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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//main class
public class test
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

        //creating all the classes
        Customer[] c = new Customer[cNum];
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
        }
    }
}

class Customer implements Runnable
{
    //variables
    private Timer timer;
    private CoffeeMachine cm;
    private String id;
    private boolean finished,access;
    private int duration,order,hoc;


    //constructor
    Customer(String id, int duration, int order, CoffeeMachine cm, Timer timer)
    {
        this.order = order;
        this.id = id;
        this.duration = duration;
        this.cm = cm;
        this.timer = timer;
        this.finished = false;
        this.access = false;
        if(this.id.contains("H"))
        {
            //0 means the coffee is hot
            hoc = 0;
        }
        else if(this.id.contains("C"))
        {
            //1 means the coffee is cold
            hoc =1;
        }

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
    public boolean checkAccess()
    {
        //no coffees are brewing so go ahead
        if(cm.getCoffeeState()==2)
        {
            access = true;
        }
        //hot coffee is brewing
        else if(cm.getCoffeeState()==0 && this.hoc==0)
        {
            access = true;
        }
        //cold coffee is brewing
        else if(cm.getCoffeeState()==1 && this.hoc==1)
        {
            access = true;
        }

        //else not ready to run the coffee
        return access;
    }
    public int getHoc()
    {
        return hoc;
    }
    //Setters

    public void finished()
    {
        finished = true;
    }

    public void run()
    {
        while(finished!=true)
        {
            //if all dispensers are not busy run
            if(cm.getDisUsed()<3)
            {
                //checks if coffee is allowed to brew, access and order is right
                if(checkAccess()==true && this.order==cm.getOrder())
                {
                    //starts brewing coffee
                    cm.makeCoffee(this);
                    //customer leaves
                    finished();
                }
                else
                {
                    //requests for change
                    cm.changeRequest();
                    //wait
                    try {
                        this.wait();
                    } catch (Exception e) {
                        //TODO: handle exception
                    }
                }
            }
            else
            {
                //wait
                try {
                    this.wait();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }
        
    }
}//end of class

class CoffeeMachine
{
    //variables
    private Customer c;
    private Timer timer;
    private int disUsed,order;
    private boolean hot,cold;


    //constructor
    CoffeeMachine(Timer timer)
    {
        this.timer = timer;
        this.disUsed = 0;
        this.hot = false;
        this.cold = false;
        this.order = 0;
    }

    //Getters
    public int getDisUsed()
    {
        return disUsed;
    }
    public int getOrder()
    {
        return order;
    }
    //gets weather the brewer is making hot or cold coffee
    public int getCoffeeState()
    {
        //returns brewing hot
        if(hot==true)
        {
            return 0;
        }
        //returns brewing cold
        else if(cold==true)
        {
            return 1;
        }
        //nothing is brewing
        return 2;
    }

    //Setters
    public void setCoffee(int state)
    {
        if(state == 0)
        {
            hot = true;
        }
        else if(state == 1)
        {
            cold = true;
        }
    }
    //synchronized so only one thread can request change at a time
    public synchronized void changeRequest()
    {
        while(disUsed!=0)
        {
            if(this.hot==true)
            {
                hot = false;
                cold = true;
            }
            else if(this.cold==true)
            {
                cold = false;
                hot = true;
            }
        }
    }

    public synchronized void makeCoffee(Customer c)
    {
        //ups counters
        order++;
        disUsed++;

        System.out.println(c.getId());
        //the print statement
        System.out.println("("+timer.getClock()+") "+ c.getId()+ " uses dispenser " + 69 + "(time: "+ c.getDuration()+")");
        
        try {
            //thread is brewing delicious coffee
            TimeUnit.MILLISECONDS.sleep(500*c.getDuration());
        } catch (Exception e) {
            //TODO: handle exception
        }
        
        //decreases counters
        disUsed--;
        timer.upExitedCus();
        timer.checkFinished();
        notifyAll();
    }
}

class Timer implements Runnable
{
    //variables
    private int customerNum, exitedCus;
    private boolean finished;
    private int timer1;

    //constructor
    Timer(int customerNum)
    {
        this.customerNum = customerNum;
        this.finished = false;
        this.timer1 = 0;
        this.exitedCus =0;
    }

    //getters
    public int getClock()
    {
        return timer1;
    }
    //setters
    public void upExitedCus()
    {
        exitedCus++;
    }
    public void checkFinished()
    {
        if(this.customerNum == this.exitedCus)
        {
            this.finished = true;
        }
    }

    public void run()
    {
        while(finished!=true)
        {
            //increases clock counter after a second
            //to allow more threads to try and access
            try 
            { 
                //1second
                TimeUnit.MILLISECONDS.sleep(500);
                timer1++; 
            } 
            catch (InterruptedException e) 
            { 
                // TODO Auto-generated catch block 
                e.printStackTrace(); 
            }
        }
    }
}
