import java.io.*;

public class MM1QueueSimulation {
    static final int Q_LIMIT = 100;
    static final int BUSY = 1;
    static final int IDLE = 0;

 //These are state variables tracking simulation state
    static int nextEventType, numCustsDelayed, numDelaysRequired, numEvents, numInQ, serverStatus;
    //These track time-based metrics, server utilization, and simulation clock
    static float areaNumInQ, areaServerStatus, meanInterarrival, meanService,
            simTime, timeLastEvent, totalOfDelays;
            //timeArrival: this array stores arrival times of customers in queue.
            //timeNextEvent:  stores next scheduled time for events ie. holds the next time of arrival and departure events
    static float[] timeArrival = new float[Q_LIMIT + 1];
    static float[] timeNextEvent = new float[3];

    //File handlers 
    static BufferedReader infile;
    static PrintWriter outfile;

    public static void main(String[] args) throws IOException {
        //Opens input and output files.
        infile = new BufferedReader(new FileReader("mm1.in"));
        outfile = new PrintWriter(new FileWriter("mm1.out"));

        //Only two events: arrival and departure
        numEvents = 2;

        //Reads mean interarrival time, mean service time, and number of customers from the input file
        String[] input = infile.readLine().split(" ");
        meanInterarrival = Float.parseFloat(input[0]);
        meanService = Float.parseFloat(input[1]);
        numDelaysRequired = Integer.parseInt(input[2]);

        //Prints initial values to the output file
        outfile.printf("Single-server queueing system\n\n");
        outfile.printf("Mean interarrival time%11.3f minutes\n\n", meanInterarrival);
        outfile.printf("Mean service time%16.3f minutes\n\n", meanService);
        outfile.printf("Number of customers%14d\n\n", numDelaysRequired);

        //initializes simulation variables
        initialize();

        //Main simulation loop where it runs until required number of customers are processed.
        while (numCustsDelayed < numDelaysRequired) {
            timing();
            updateTimeAvgStats();

            switch (nextEventType) {
                case 1 -> arrive();
                case 2 -> depart();
            }
        }

        //Prints results and closes files
        report();
        infile.close();
        outfile.close();
    }

    //we initialize the simulation states
    static void initialize() {
        //Sets the first arrival time and leaves departure time as "infinity" since no one is being served yet.
        simTime = 0.0f;
        serverStatus = IDLE;
        numInQ = 0;
        timeLastEvent = 0.0f;

        numCustsDelayed = 0;
        totalOfDelays = 0.0f;
        areaNumInQ = 0.0f;
        areaServerStatus = 0.0f;

        timeNextEvent[1] = simTime + expon(meanInterarrival);
        timeNextEvent[2] = Float.MAX_VALUE;
    }

    //timing () finds the next event ie smallest time in timeNextEvent array and updates simTime
    static void timing() {
        float minTimeNextEvent = Float.MAX_VALUE;
        nextEventType = 0;

        for (int i = 1; i <= numEvents; i++) {
            if (timeNextEvent[i] < minTimeNextEvent) {
                minTimeNextEvent = timeNextEvent[i];
                nextEventType = i;
            }
        }

        //If no valid next event, the simulation ends abnormally.
        if (nextEventType == 0) {
            outfile.printf("\nEvent list empty at time %f", simTime);
            outfile.flush();
            System.exit(1);
        }

        simTime = minTimeNextEvent;
    }

    //arrive() handles a customer's arrival
    static void arrive() {
        float delay;

        //schedules the next arrival
        timeNextEvent[1] = simTime + expon(meanInterarrival);

        //schedules the next arrival
        if (serverStatus == BUSY) {
            numInQ++;
            if (numInQ > Q_LIMIT) {
                outfile.printf("\nOverflow of the array time_arrival at");
                outfile.printf(" time %f", simTime);
                outfile.flush();
                System.exit(2);
            }

            timeArrival[numInQ] = simTime;
        } else {
            delay = 0.0f;
            totalOfDelays += delay;
            numCustsDelayed++;
            serverStatus = BUSY;
            timeNextEvent[2] = simTime + expon(meanService);
        }
    }

    //depart() handles a customer's departure
    static void depart() {
        float delay;

        if (numInQ == 0) {
            serverStatus = IDLE;
            timeNextEvent[2] = Float.MAX_VALUE;
        } else {
            numInQ--;
            delay = simTime - timeArrival[1];
            totalOfDelays += delay;
            numCustsDelayed++;
            timeNextEvent[2] = simTime + expon(meanService);

            //shifts the queue forward after removing the served customer
            for (int i = 1; i <= numInQ; i++) {
                timeArrival[i] = timeArrival[i + 1];
            }
        }
    }

    //Calculates and writes final statistics
    static void report() {
        outfile.printf("\n\nAverage delay in queue%11.3f minutes\n\n", totalOfDelays / numCustsDelayed);
        outfile.printf("Average number in queue%10.3f\n\n", areaNumInQ / simTime);
        outfile.printf("Server utilization%15.3f\n\n", areaServerStatus / simTime);
        outfile.printf("Time simulation ended%12.3f minutes\n", simTime);
    }

    //keeps track of the cumulative time that the server is busy and queue lengths for average calculations
    static void updateTimeAvgStats() {
        float timeSinceLastEvent = simTime - timeLastEvent;
        timeLastEvent = simTime;

        areaNumInQ += numInQ * timeSinceLastEvent;
        areaServerStatus += serverStatus * timeSinceLastEvent;
    }

    //expom() generates exponentially distributed random numbers using the inverse transform method, which models interarrival and service times in M/M/1 queues
    static float expon(float mean) {
        double randValue = Math.random();
        // Ensure the random value is in (0,1) to avoid taking log(0)
        if (randValue <= 0.0)
            randValue = 1.0e-10;
        else if (randValue >= 1.0)
            randValue = 1.0 - 1.0e-10;

        double result = -mean * Math.log(1.0 - randValue);
        if (result > 1.0e+30)
            return (float) 1.0e+30f;

        return (float) result;
    }
}
