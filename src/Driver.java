//Michael Lim

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

//A class to read in an input file of jobs, and schedule them according to some algorithm
//Can calculate some basic statistics for later analysis
public class Driver {

	public int numCPU;
	public int algorithm;


	long currentTime;
	long startTime;
	long endTime;

	public Driver(int numCPU, int algorithm){
		this.numCPU= numCPU;
		this.algorithm = algorithm;
		this.currentTime = -1;
	}

	public void processJobsFCFS(ArrayList<Job> jobs){
		Job currJob;
		long lastWork = 0;
		LinkedList<Job> newJobs = new LinkedList<Job>();
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );

		LinkedList<Job> toProcess = new LinkedList<Job>();
		LinkedList<Job> processed = new LinkedList<Job>();

		ArrayList<Job> processing = new ArrayList<Job>(numCPU);
		for(int i = 0; i < numCPU; i++){
			processing.add(null);
		}

		ArrayList<CPU> CPUs = new ArrayList<CPU>(numCPU);//Initialize our list of available CPUs
		for(int i = 0; i < numCPU; i++){
			CPUs.add(new CPU(i));
		}

		startTime = System.currentTimeMillis(); //Get start time

		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			toProcess.offer(currJob);// Add the job to our list to be processed

		}
		while( !toProcess.isEmpty() || hasJob(processing) ){
			int numJobsToProcess = toProcess.size(); 

			for(int i = 0; i < numJobsToProcess; i++){ //While we have jobs to process
				currJob = toProcess.poll();

				if(currJob.getTypeString().equals("CPU")){ //If the next job is a CPU job
					int freeCPU = hasFreeCPU(CPUs);

					if(freeCPU != -1){// and there is an available CPU
						if(CPUs.get(freeCPU).assignJob(currJob) == null){ //If the CPU was free and we didn't kick out another Job
							if(currJob.firstAddressedTime == -1){
								currJob.firstAddressedTime = System.currentTimeMillis();
							}
						}
						processing.set(freeCPU, currJob); //Adds the current job to our processing list at the index corresponding to the CPU it will run on
					}
					else{
						toProcess.offer(currJob); //Put the job back on the queue
					}
				}
				else if(currJob.getTypeString().equals("IO")){ //If the next job is an IO job
					System.out.println("IO needed, passing off");
					//For now, we just assume IO requests can be handled independently. 
				}
			}

			while(!newJobs.isEmpty()){ //If we have new jobs to add
				Job newJob = newJobs.pop(); //Add them, and set their enter times
				newJob.enterTime = System.currentTimeMillis();
				toProcess.add(newJob);
			}

			for(int curCPU = 0; curCPU < CPUs.size(); curCPU++){ //Loop through all of our CPUs
				if(CPUs.get(curCPU).myJob != null){ //Skip CPUs that don't have a job
					long curTime = System.currentTimeMillis();
					long timeWorked;
					if(lastWork != 0){ //If this isn't pur first loop through
						timeWorked = (curTime-lastWork); //Find out how much time has passed since we last checked
					}
					else{ //Otherwise, we haven't done any work yet
						timeWorked = 0;
					}
					currJob = CPUs.get(curCPU).isDone(timeWorked); //Check if the Job on the current CPU is done.
					if (currJob != null){ //If we are in fact done with this Job (isDone will return the completed Job)
						processed.offer(currJob);
						processing.set(curCPU, null); //Clear the corresponding location in our processing queue
						CPUs.get(curCPU).myJob  = null; //Clear the CPU of the Job
					}
				}
			}
			lastWork = System.currentTimeMillis();
		}
		endTime = System.currentTimeMillis();
		evaluatePerformance(startTime, endTime, processed);		
	}

	public void processJobsSJF(ArrayList<Job> jobs){
		Job currJob;
		Comparator<Job> comparartor = new JobLengthComparator();
		long lastWork = 0;

		LinkedList<Job> newJobs = new LinkedList<Job>();
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );

		PriorityQueue<Job> toProcess = new PriorityQueue<Job>(jobs.size(), comparartor);
		LinkedList<Job> processed = new LinkedList<Job>();

		ArrayList<Job> processing = new ArrayList<Job>(numCPU);
		for(int i = 0; i < numCPU; i++){
			processing.add(null);
		}

		ArrayList<CPU> CPUs = new ArrayList<CPU>(numCPU);//Initialize our list of available CPUs
		for(int i = 0; i < numCPU; i++){
			CPUs.add(new CPU(i));
		}
		startTime = System.currentTimeMillis(); //Get start time

		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			toProcess.offer(currJob);// Add the job to our list to be processed

		}
		while( !toProcess.isEmpty() || hasJob(processing) ){
			int numJobsToProcess = toProcess.size(); 
			for(int i = 0; i < numJobsToProcess; i++){ //While we have jobs to process
				currJob = toProcess.poll();
				if(currJob.getTypeString().equals("CPU")){ //If the next job is a CPU job
					int freeCPU = hasFreeCPU(CPUs);
					if(freeCPU != -1){// and there is an available CPU
						if(CPUs.get(freeCPU).assignJob(currJob) == null){ //If the CPU was free and we didn't kick out another Job
							if(currJob.firstAddressedTime == -1){
								currJob.firstAddressedTime = System.currentTimeMillis();
							}
						}
						processing.set(freeCPU, currJob); //Adds the current job to our processing list at the index corresponding to the CPU it will run on
					}
					else{
						toProcess.offer(currJob); //Put the job back on the queue
					}
				}
				else if(currJob.getTypeString().equals("IO")){ //If the next job is an IO job
					System.out.println("IO needed, passing off");
					//For now, we just assume IO requests can be handled independently. 
				}
			}

			while(!newJobs.isEmpty()){ //If we have new jobs to add
				Job newJob = newJobs.pop(); //Add them, and set their enter times
				newJob.enterTime = System.currentTimeMillis();
				toProcess.add(newJob);
			}

			for(int curCPU = 0; curCPU < CPUs.size(); curCPU++){ //Loop through all of our CPUs
				if(CPUs.get(curCPU).myJob != null){ //Skip CPUs that don't have a job
					long curTime = System.currentTimeMillis();
					long timeWorked;
					if(lastWork != 0){ //If this isn't pur first loop through
						timeWorked = (curTime-lastWork); //Find out how much time has passed since we last checked
					}
					else{ //Otherwise, we haven't done any work yet
						timeWorked = 0;
					}
					currJob = CPUs.get(curCPU).isDone(timeWorked); //Check if the Job on the current CPU is done.
					if (currJob != null){ //If we are in fact done with this Job (isDone will return the completed Job)
						processed.offer(currJob);
						processing.set(curCPU, null); //Clear the corresponding location in our processing queue
						CPUs.get(curCPU).myJob  = null; //Clear the CPU of the Job
					}
				}
			}
			lastWork = System.currentTimeMillis();
		}
		endTime = System.currentTimeMillis();
		evaluatePerformance(startTime, endTime, processed);
	}

	public void processJobsPSJF(ArrayList<Job> jobs){
		Job currJob;
		Comparator<Job> comparartor = new JobLengthComparator();
		long lastWork = 0;

		LinkedList<Job> newJobs = new LinkedList<Job>();
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );
		newJobs.offer( new Job(1, 'C', 10 ) );

		PriorityQueue<Job> toProcess = new PriorityQueue<Job>(jobs.size(), comparartor);
		LinkedList<Job> processed = new LinkedList<Job>();

		ArrayList<Job> processing = new ArrayList<Job>(numCPU);
		for(int i = 0; i < numCPU; i++){
			processing.add(null);
		}

		ArrayList<CPU> CPUs = new ArrayList<CPU>(numCPU);//Initialize our list of available CPUs
		for(int i = 0; i < numCPU; i++){
			CPUs.add(new CPU(i));
		}
		startTime = System.currentTimeMillis(); //Get start time

		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			toProcess.offer(currJob);// Add the job to our list to be processed

		}
		while( !toProcess.isEmpty() || hasJob(processing) ){
			int numJobsToProcess = toProcess.size(); 
			for(int i = 0; i < numJobsToProcess; i++){ //While we have jobs to process
				currJob = toProcess.poll();
				if(currJob.getTypeString().equals("CPU")){ //If the next job is a CPU job
					int freeCPU = hasFreeCPU(CPUs);
					if(freeCPU != -1){// and there is an available CPU
						Job checkedJob = CPUs.get(freeCPU).assignJob(currJob);
						if(checkedJob == null){ //If the CPU was free and we didn't kick out another Job
							if(currJob.firstAddressedTime == -1){
								currJob.firstAddressedTime = System.currentTimeMillis();
							}
							processing.set(freeCPU, currJob); //Adds the current job to our processing list at the index corresponding to the CPU it will run on
						}
					}
					else{
						int longer = hasLonger(currJob.timeNeeded, processing); //See if there is a job we should evict
						if(longer != -1){ //If there is
							Job evictedJob = CPUs.get(longer).assignJob(currJob); //Swap our current job with the job with the longest remaining time
							toProcess.offer(evictedJob); //Add the evicted Job back to the queue
						}
						else{ //If we can't evict anyone (Our time remaining is longer than everyone elses)
							toProcess.offer(currJob); //Put the job back on the queue
						}
					}
				}
				else if(currJob.getTypeString().equals("IO")){ //If the next job is an IO job
					System.out.println("IO needed, passing off");
					//For now, we just assume IO requests can be handled independently. 
				}
			}
			while(!newJobs.isEmpty()){ //If we have new jobs to add
				Job newJob = newJobs.pop(); //Add them, and set their enter times
				newJob.enterTime = System.currentTimeMillis();
				toProcess.add(newJob);
			}

			for(int curCPU = 0; curCPU < numCPU; curCPU++){ //Loop through all of our CPUs
				if(CPUs.get(curCPU).myJob != null){ //Skip CPUs that don't have a job
					long curTime = System.currentTimeMillis();
					long timeWorked;

					if(lastWork != 0){ //If this isn't pur first loop through
						timeWorked = (curTime-lastWork); //Find out how much time has passed since we last checked
					}
					else{ //Otherwise, we haven't done any work yet
						timeWorked = 0;
					}
					currJob = CPUs.get(curCPU).isDone(timeWorked); //Check if the Job on the current CPU is done.

					if (currJob != null){ //If we are in fact done with this Job (isDone will return the completed Job)
						processed.offer(currJob);
						processing.set(curCPU, null); //Clear the corresponding location in our processing queue
						CPUs.get(curCPU).myJob  = null; //Clear the CPU of the Job
					}
				}
			}
			lastWork = System.currentTimeMillis();
		}
		endTime = System.currentTimeMillis();
		evaluatePerformance(startTime, endTime, processed);
	}

	//Given a list of processed jobs, calculate some important statistics
	public void evaluatePerformance(long startTime, long endTime, LinkedList<Job> processed){
		Job currJob;
		int numJobs = processed.size();
		long totalTillAddressed = 0;
		long totalTillFinish = 0;
		long totalJobLength = 0;

		for(int i = 0; i < numJobs; i++){ //Calculate totals
			currJob = processed.get(i);
			totalTillAddressed += (currJob.firstAddressedTime - currJob.enterTime);
			totalTillFinish += (currJob.finishTime - currJob.enterTime);
			totalJobLength += currJob.originalTime;
		}

		long avgTillFinish = totalTillFinish/numJobs; //Calculate averages
		long avgTillAddressed = totalTillAddressed/numJobs;
		long avgJobLength = totalJobLength/numJobs;

		//		System.out.println("All "+numJobs+" jobs finished");
		//		System.out.println("Total time to be Adressed: "+totalTillAddressed+"ms");
		System.out.println("Avg wait time:             "+avgTillAddressed+"ms");
		//		System.out.println("Total time to finish:      "+totalTillFinish+"ms");
		System.out.println("Avg time to finish:        "+avgTillFinish+"ms");
		//		System.out.println("Total job length:          "+totalJobLength+"ms");
		System.out.println("Avg job length:            "+avgJobLength+"ms");
	}

	public int hasFreeCPU(ArrayList<CPU> CPUs){ //If there is a CPU that is currently free, return the index of that CPU
		for(int i = 0; i < CPUs.size(); i++){ // otherwise, return -1
			if (CPUs.get(i).isFree()){
				return i;
			}
		}
		return -1;
	}

	//Check if we have a Job with a longer burst time than the time given, if so, return the index of that job
	public int hasLonger(long currTime, ArrayList<Job> list){
		long longestTime = -1;
		int longestLoc = -1;

		for(int nextJob = 0; nextJob < list.size();nextJob++){ //Check all of our jobs
			long nextTime = list.get(nextJob).timeNeeded ; //Get the time of the job

			if( nextTime > currTime){ //If this time is longer than what we're looking at
				if(nextTime > longestTime){ //If the time is longer than the longest we've currently found
					longestTime = nextTime; //Store the values
					longestLoc = nextJob;
				}
			}
		}
		return longestLoc; //Return the longest we found, or -1 if we found none
	}

	//Returns true if we have a job in the given list
	public boolean hasJob(ArrayList<Job> list){
		for(Job j: list){
			if( j != null){//If we find a non-null value, return ture
				return true;
			}
		}//Otherwise return false
		return false;
	}

	public static void main(String[] args) {
		Driver d;
		int numCPU;
		int algorithm;
		String filename;
		ArrayList<Job> jobs1 = new ArrayList<Job>();//Create a list for each algorithm
		ArrayList<Job> jobs2 = new ArrayList<Job>();//in case we want to use all three
		ArrayList<Job> jobs3 = new ArrayList<Job>();//simultaneously for comparison

		if(args.length < 3){ //If no command line args are given, default values are
			numCPU = 1; //Use 1 CPU
			algorithm = 0; //Use all algorithms
			filename = "input.txt"; //Use jobs found in input.txt
		}
		else{
			//Parse number of CPUs
			numCPU = Integer.parseInt(args[0]);
			//Parse algorithm selected
			algorithm = Integer.parseInt(args[1]);
			filename = args[2];
		}
		d = new Driver(numCPU, algorithm);

		try(Scanner file = new Scanner(new FileReader(new File(filename)));){ //Open file
			String nextLine;
			d = new Driver(numCPU, algorithm);

			while(file.hasNextLine()){//While we still have lines
				nextLine = file.nextLine(); //Read in the next line
				int nextProc = (int)(nextLine.charAt(0))-48; //Parse info from file
				char nextType = nextLine.charAt(1);
				int nextLength = Integer.parseInt(nextLine.substring(2));

				Job j1 = new Job(nextProc, nextType, nextLength ); //Create a new job for use in FCFS
				Job j2 = new Job(nextProc, nextType, nextLength ); //Create a new job for use in SJF
				Job j3 = new Job(nextProc, nextType, nextLength ); //Create a new job for use in PSJF

				jobs1.add(j1);//Add these jobs to their respective lists
				jobs2.add(j2);
				jobs3.add(j3);
			}
		}
		catch(IOException e){
			System.err.println("File Error, check file name");
			System.exit(-1);
		}

		if(algorithm == 1){
			System.out.println("Scheduling jobs using FCFS");
			d.processJobsFCFS(jobs1);
		}
		else if(algorithm == 2){
			System.out.println("Scheduling jobs using regular SJF");
			d.processJobsSJF(jobs2);
		}
		else if(algorithm == 3){
			System.out.println("Scheduling jobs using preemptive SJF");
			d.processJobsPSJF(jobs3);
		}
		else if (algorithm == 0){ //Check all
			System.out.println("Scheduling jobs using FCFS");
			d.processJobsFCFS(jobs1);
			System.out.println();
			System.out.println("Scheduling jobs using regular SJF");
			d.processJobsSJF(jobs2);
			System.out.println();
			System.out.println("Scheduling jobs using preemptive SJF");
			d.processJobsPSJF(jobs3);
		}
	}
	//A comparator to find the job with the shortest job length
	private class JobLengthComparator implements Comparator{

		public int compare(Object obj1, Object obj2) {
			Job job1 = (Job)obj1;
			Job job2 = (Job)obj2;
			if(job1.timeNeeded < job2.timeNeeded){
				return -1;
			}
			if(job1.timeNeeded > job2.timeNeeded){
				return 1;
			}
			return 0;
		}
	}
}
