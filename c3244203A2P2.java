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
        customer[] c = new customer[cNum];
        //creating 5 seats
        //
        //
        //Still not sure if i should make this into an array or not
        seat[] s = new seat[5];
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
class customer implements Runnable
{
    private String id;
    private int arrives;
    private int duration;
    private int leaves;

    public customer(String id, int arrives, int duration)
    {
        this.id = id;
        this.arrives = arrives;
        this.duration = duration;
    }

    @Override
    public void run()
    {
        System.out.println("customer is running as it should");
    }
}//end of class

//uses semaphore
class seat
{
    public seat()
    {

    }
}//end of class