# QueueingSimulation
This project simulates a single-server queue (M/M/1) using discrete-event simulation principles. It models customer arrivals and services in a system where:

Interarrival and service times follow an exponential distribution.

Only one server handles incoming customers (no parallel service).

The queue has a configurable maximum capacity.

🧠 Concepts Modeled
Discrete-event simulation

M/M/1 queue behavior (Poisson arrivals, exponential service times)

Server utilization, queue length, and customer delay tracking

Random number generation using inverse transform sampling

📂 Input Format (mm1.in)
A single line with three values separated by spaces:

<mean_interarrival_time> <mean_service_time> <number_of_customers>

Example:

1.0 0.5 1000

This means:

On average, customers arrive every 1.0 minutes

Each customer takes 0.5 minutes to be served

Simulate until 1000 customers are processed

📤 Output Format (mm1.out)
The program prints the following statistics:

Single-server queueing system

Mean interarrival time     X.XXX minutes
Mean service time          Y.YYY minutes
Number of customers        NNNN

Average delay in queue     A.AAA minutes
Average number in queue    B.BBB
Server utilization         C.CCC
Time simulation ended      D.DDD minutes

⚙️ How It Works
arrive() handles customer arrival events

depart() handles customer departure events

timing() determines the next scheduled event

expon(mean) generates exponentially distributed values

updateTimeAvgStats() updates time-weighted statistics

🚀 How to Run

Save your input in a file named mm1.in

Compile and run the Java file:

javac MM1QueueSimulation.java

java MM1QueueSimulation

Results will be written to mm1.out
