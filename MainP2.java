import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class MainP2 {

    public static void main(String[] args) {
        System.out.println("Question 2.");
        //File reading
        boolean success = false;    //looping file input
        int N=0,S=0;
        String[] input;
        Scanner in = new Scanner(System.in);
        System.out.println("Enter file name eg input.txt: ");
        while (!success) {  //loop until a valid file is given
            try {
                String f = in.nextLine();
                Scanner file = new Scanner(new File(f));    //Throws file not found exception
                try {
                    //split by space
                    input = file.nextLine().split("\\s+");
                    //set number of north and south farmers
                    N = Integer.parseInt(input[0].replaceAll("[^0-9]+",""));    
                    S = Integer.parseInt(input[1].replaceAll("[^0-9]+",""));
                    success = true; //no exception thrown, all went well, break loop
                } catch (NoSuchElementException e) {System.out.println("File was empty or invalid! Please enter a valid file.");}
                file.close();
            } catch (FileNotFoundException e) { System.out.println("File not found! Please enter a valid file.");}
        }
        in.close();
        //end file reading

        Bridge bridge = new Bridge();   //create our bridge
        Farmer[] f = new Farmer[N+S];   //array of Farmers
        //create North farmers
        for (int i=0; i<N; i++) {
            f[i] = new Farmer("N_Farmer"+(i+1),"North",bridge);
        }
        //create South farmers
        for (int i=N; i<S+N; i++) {
            f[i]= new Farmer("S_Farmer"+(i-N+1),"South",bridge);
        }

        //start all farmers
        for (int i=0;i<S+N;i++) {
            f[i].start();   //start Farmer Threads. Farmers can run start, as Farmer extends thread
        }
    }
}

class Farmer extends Thread
{
    private String location;    //current location
    private String destination; //Opposite location, destination, set in the constructor
    private String id;          //name      
    private Bridge bridge;      //bridge being used
    private boolean finished=false;
    private boolean counted = false;

    //constructor
    public Farmer(String id, String location, Bridge bridge) {
        this.id=id;
        this.location=location;
        if (location=="North") destination="South"; //Island objects are not necessary for this particular implementation, as our options are merely North or South
        else destination="North";
        this.bridge = bridge;
        System.out.println(id+": Waiting for bridge. Going towards "+destination);  //print initial waiting for bridge

    }

    //getters
    public String getLocation() {
        return location;
    }
    public String getID() {
        return id;
    }
    public boolean isCounted() {
        return counted;
    }
    //setter
    public void setFinished(boolean finished) {
        this.finished=finished;
    }
    public void counted() {
        counted=true;
    }

    //Overrides the Thread toString() method. Called with Thread.getCurrent().toString()
    @Override   
    public String toString() {
        return id;
    }
    @Override   //initiatied when the Farmer Thread .start() method is called
    public void run() {
        //if ready to cross

        while (!finished) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

            if (bridge.getNorth() != 2 && bridge.getSouth() != 2 && !counted) { //if neither equal 2 yet and we havent added this to the list
                bridge.upThis(this);    //increments the appropriate north/south counter in a thread safe method, also marks this thread as counted=true
            }
            if (counted && bridge.getNorth()==2 && id.startsWith("N")) {    //if this has been counted, and is a northern farmer and there are 2 northern farmers ready
                bridge.cross(this);
                bridge.exit();
                finished=true;
                if (bridge.getExited()==2) {    //if both successfully crossed reset counts
                    bridge.resetExited();
                    bridge.resetNorth();
                    //System.out.println("Reset exited and North"); //DEBUG
                    //System.out.println("Exit: "+bridge.getExited()+", North: "+bridge.getNorth()+", South: "+bridge.getSouth()); //DEBUG
                }
            }
            else if (counted && bridge.getSouth()==2 && id.startsWith("S")) { //else if this has been counted, and is a southern farmer and there are 2 southern farmers ready
                bridge.cross(this);
                bridge.exit();
                finished=true;
                if (bridge.getExited()==2) {    //if both successfully crossed reset counts
                    bridge.resetExited();
                    bridge.resetSouth();
                    //System.out.println("Reset exited and South"); //DEBUG
                    //System.out.println("Exit: "+bridge.getExited()+", North: "+bridge.getNorth()+", South: "+bridge.getSouth()); //DEBUG
                }
            }

        }
    }//end run  

}//end class

class Bridge 
{
    private int crossed;    //Count the number of crossings
    private static Semaphore bridgeSem; //semaphore to only allow 1 crossing at a time
    private int northWaiting, southWaiting;
    private int exited;
    //Constructor
    public Bridge() {
        crossed=0;
        bridgeSem = new Semaphore(2);   //one bridge resource, mutual exclusivity
        northWaiting = southWaiting = 0;
        exited = 0;

    }

    //Getters
    public int getCrossed() {
        return crossed;
    }
    //Methods
    public synchronized void upCross() {
        crossed++;
        System.out.println("NEON = "+getCrossed());
    }
    public synchronized void upThis(Farmer f) {
        if (f.getID().startsWith("N")) northWaiting++;
        else southWaiting++;
        f.counted();
        //System.out.println(f.getID()+" is queued to cross");  //DEBUG 
    }
    public synchronized void upExited() {
        exited++;
    }
    public synchronized int getNorth() {
        return northWaiting;
    }
    public synchronized int getSouth() {
        return southWaiting;
    }
    public synchronized int getExited() {
        return exited;
    }
    public synchronized void resetExited() {
        exited=0;
    }
    public synchronized void resetNorth() {
        northWaiting=0;
    }
    public synchronized void resetSouth() {
        southWaiting=0;
    }

    public void cross(Farmer f) { 
        //Semaphore acquire
        try {   
            bridgeSem.acquire();    
            System.out.println(f.getID()+": Crossing bridge Step 5.");
            System.out.println(f.getID()+": Crossing bridge Step 10.");
            System.out.println(f.getID()+": Crossing bridge Step 15.");

            //Sleep for 200 units ,improves readability (else output is too fast) 
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {} //No interrupts implemented, so thread shouldn't be interrupted?

            System.out.println(f.getID()+": Across the Bridge.");
            upCross();  //increment NEON counter, synchronized to avoid print conflicts
            //Sleep for 200 units ,improves readability (else output is too fast) 
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {} //No interrupts implemented, so thread shouldn't be interrupted?
        }
        catch (InterruptedException e) {} 
    }

    public void exit() {
        //Semaphore release
        upExited();
        bridgeSem.release();

    }
}