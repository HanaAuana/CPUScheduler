import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;




public class Driver {
	//Test

	public int numCPU;
	public int algorithm;
	public ArrayList<Job> jobs;
	public ArrayList<CPU> CPUs;
	Queue<Job> toProcess;
	ArrayList<Job> processing;
	LinkedList<Job> processed;
	long currentTime;
	long startTime;
	long endTime;

	public Driver(int numCPU, int algorithm){
		this.numCPU= numCPU;
		this.algorithm = algorithm;
		this.jobs = new ArrayList<Job>();
		this.currentTime = -1;
		
		
	}

	public void processJobsFCFS(ArrayList<Job> jobs){
		Job currJob;
		toProcess = new LinkedList<Job>();
		processed = new LinkedList<Job>();
		processing = new ArrayList<Job>(numCPU);
		for(int i = 0; i < numCPU; i++){
			processing.add(null);
		}
		CPUs = new ArrayList<CPU>(numCPU);//Initialize our list of available CPUs
		for(int i = 0; i < numCPU; i++){
			CPUs.add(new CPU(i));
		}
		
		startTime = System.currentTimeMillis(); //Get start time
		System.out.println("Scheduling starting at: "+startTime);

		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			//System.out.println("Adding "+currJob+" at "+currentTime);
			toProcess.offer(currJob);// Add the job to our list to be processed

		}
		while( !toProcess.isEmpty() || hasJob(processing) ){
			for(int i = 0; i < toProcess.size(); i++){ //While we have jobs to process
				currJob = toProcess.poll();
				if(currJob.getTypeString().equals("CPU")){ //If the next job is a CPU job
					//System.out.println("Processing CPU");
					int freeCPU = hasFreeCPU();
					if(freeCPU != -1){// and there is an available CPU
						//System.out.println("Processing "+currJob+" at "+currentTime);
						if(CPUs.get(freeCPU).assignJob(currJob) == null){ //If the CPU was free and we didn't kick out another Job
							//System.out.println("Job added to CPU "+freeCPU);
							currJob.firstAddressedTime = System.currentTimeMillis();
						}
						else{
							System.err.println("Job preempted. Shouldn't have happened in FCFS");
						}
						processing.set(freeCPU, currJob); //Adds the current job to our processing list at the index corresponding to the CPU it will run on
					}
					else{
						//System.out.println("No CPUs free, waiting...");
						toProcess.offer(currJob); //Put the job back on the queue
					}
				}
				else if(currJob.getTypeString().equals("IO")){ //If the next job is an IO job
					System.out.println("IO needed, passing off");
					//For now, we just assume IO requests can be handled independently. 
				}
			}

			while( hasJob(processing) ){
				for(int curCPU = 0; curCPU < CPUs.size(); curCPU++){ //Loop through all of our CPUs
					if(CPUs.get(curCPU).myJob != null){ //Skip CPUs that don't have a job
						//System.out.println(curCPU);
						currJob = CPUs.get(curCPU).isDone(); //Check if the Job on the current CPU is done.
						if (currJob != null){ //If we are in fact done with this Job (isDone will return the completed Job)
							//System.out.println("CPU "+curCPU+" is free");
							processed.offer(currJob);
							processing.set(curCPU, null); //Clear the corresponding location in our processing queue
							CPUs.get(curCPU).myJob  = null; //Clear the CPU of the Job
						}
					}
				}
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("Scheduling ended at:    "+endTime);
		evaluatePerformance(startTime, endTime, processed);		
	}
	
	public void processJobsSJF(ArrayList<Job> jobs){
		Job currJob;
		Comparator<Job> comparartor = new JobLengthComparator();
		toProcess = new PriorityQueue<Job>(comparartor);
		processed = new LinkedList<Job>();
		processing = new ArrayList<Job>(numCPU);
		for(int i = 0; i < numCPU; i++){
			processing.add(null);
		}
		CPUs = new ArrayList<CPU>(numCPU);//Initialize our list of available CPUs
		for(int i = 0; i < numCPU; i++){
			CPUs.add(new CPU(i));
		}
		startTime = System.currentTimeMillis(); //Get start time
		System.out.println("Scheduling starting at: "+startTime);

		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			//System.out.println("Adding "+currJob+" at "+currentTime);
			toProcess.offer(currJob);// Add the job to our list to be processed

		}
		while( !toProcess.isEmpty() || hasJob(processing) ){
			for(int i = 0; i < toProcess.size(); i++){ //While we have jobs to process
				currJob = toProcess.poll();
				if(currJob.getTypeString().equals("CPU")){ //If the next job is a CPU job
					//System.out.println("Processing CPU");
					int freeCPU = hasFreeCPU();
					if(freeCPU != -1){// and there is an available CPU
						//System.out.println("Processing "+currJob+" at "+currentTime);
						if(CPUs.get(freeCPU).assignJob(currJob) == null){ //If the CPU was free and we didn't kick out another Job
							//System.out.println("Job added to CPU "+freeCPU);
							currJob.firstAddressedTime = System.currentTimeMillis();
						}
						else{
							System.err.println("Job preempted. Shouldn't have happened in regular SJF");
						}
						processing.set(freeCPU, currJob); //Adds the current job to our processing list at the index corresponding to the CPU it will run on
					}
					else{
						//System.out.println("No CPUs free, waiting...");
						toProcess.offer(currJob); //Put the job back on the queue
					}
				}
				else if(currJob.getTypeString().equals("IO")){ //If the next job is an IO job
					System.out.println("IO needed, passing off");
					//For now, we just assume IO requests can be handled independently. 
				}
			}

			while( hasJob(processing) ){
				for(int curCPU = 0; curCPU < CPUs.size(); curCPU++){ //Loop through all of our CPUs
					if(CPUs.get(curCPU).myJob != null){ //Skip CPUs that don't have a job
						//System.out.println(curCPU);
						currJob = CPUs.get(curCPU).isDone(); //Check if the Job on the current CPU is done.
						if (currJob != null){ //If we are in fact done with this Job (isDone will return the completed Job)
							//System.out.println("CPU "+curCPU+" is free");
							processed.offer(currJob);
							processing.set(curCPU, null); //Clear the corresponding location in our processing queue
							CPUs.get(curCPU).myJob  = null; //Clear the CPU of the Job
						}
					}
				}
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("Scheduling ended at:    "+endTime);
		evaluatePerformance(startTime, endTime, processed);
	}
	
	public void processJobsPSJF(ArrayList<Job> jobs){
		Job currJob;
		Comparator<Job> comparartor = new JobLengthComparator();
		toProcess = new PriorityQueue<Job>(comparartor);
		processed = new LinkedList<Job>();
		processing = new ArrayList<Job>(numCPU);
		for(int i = 0; i < numCPU; i++){
			processing.add(null);
		}
		CPUs = new ArrayList<CPU>(numCPU);//Initialize our list of available CPUs
		for(int i = 0; i < numCPU; i++){
			CPUs.add(new CPU(i));
		}
		startTime = System.currentTimeMillis(); //Get start time
		System.out.println("Scheduling starting at: "+startTime);

		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			//System.out.println("Adding "+currJob+" at "+currentTime);
			toProcess.offer(currJob);// Add the job to our list to be processed

		}
		while( !toProcess.isEmpty() || hasJob(processing) ){
			for(int i = 0; i < toProcess.size(); i++){ //While we have jobs to process
				currJob = toProcess.poll();
				if(currJob.getTypeString().equals("CPU")){ //If the next job is a CPU job
					//System.out.println("Processing CPU");
					int freeCPU = hasFreeCPU();
					if(freeCPU != -1){// and there is an available CPU
						//System.out.println("Processing "+currJob+" at "+currentTime);
						if(CPUs.get(freeCPU).assignJob(currJob) == null){ //If the CPU was free and we didn't kick out another Job
							//System.out.println("Job added to CPU "+freeCPU);
							currJob.firstAddressedTime = System.currentTimeMillis();
						}
						processing.set(freeCPU, currJob); //Adds the current job to our processing list at the index corresponding to the CPU it will run on
					}
					else{
						int longer = hasLonger(currJob.timeNeeded, processing); //See if there is a job we should evict
						if(longer != -1){ //If there is
							System.out.println("Preempting the job on CPU "+longer+" with length of "+CPUs.get(longer).myJob.timeNeeded+" for job with length of "+ currJob.timeNeeded);
							Job evictedJob = CPUs.get(longer).assignJob(currJob); //Swap our current job with the job with the longest remaining time
							toProcess.offer(evictedJob); //Add the evicted Job back to the queue
						}
						else{ //If we can't evict anyone (Our time remaining is longer than everyone elses)
							//System.out.println("No CPUs available, waiting...");
							toProcess.offer(currJob); //Put the job back on the queue
						}
					}
				}
				else if(currJob.getTypeString().equals("IO")){ //If the next job is an IO job
					System.out.println("IO needed, passing off");
					//For now, we just assume IO requests can be handled independently. 
				}
			}

			while( hasJob(processing) ){
				for(int curCPU = 0; curCPU < CPUs.size(); curCPU++){ //Loop through all of our CPUs
					if(CPUs.get(curCPU).myJob != null){ //Skip CPUs that don't have a job
						//System.out.println(curCPU);
						currJob = CPUs.get(curCPU).isDone(); //Check if the Job on the current CPU is done.
						if (currJob != null){ //If we are in fact done with this Job (isDone will return the completed Job)
							//System.out.println("CPU "+curCPU+" is free");
							processed.offer(currJob);
							processing.set(curCPU, null); //Clear the corresponding location in our processing queue
							CPUs.get(curCPU).myJob  = null; //Clear the CPU of the Job
						}
					}
				}
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("Scheduling ended at:    "+endTime);
		evaluatePerformance(startTime, endTime, processed);
	}

	public void evaluatePerformance(long startTime, long endTime, LinkedList<Job> processed){
		Job currJob;
		int numJobs = processed.size();
		System.out.println("All "+numJobs+" jobs finished");
		System.out.println("Whole process took: "+(endTime-startTime)+"ms");

		long totalTillAddressed = 0;
		long totalTillFinish = 0;
		long totalJobLength = 0;
		for(int i = 0; i < numJobs; i++){
			currJob = processed.get(i);
			totalTillAddressed += (currJob.firstAddressedTime - currJob.enterTime);
			totalTillFinish += (currJob.finishTime - currJob.enterTime);
			totalJobLength += currJob.originalTime;
		}
		long avgTillFinish = totalTillFinish/numJobs;
		long avgTillAddressed = totalTillAddressed/numJobs;
		long avgJobLength = totalJobLength/numJobs;

		System.out.println("Total time to be Adressed: "+totalTillAddressed+"ms");
		System.out.println("Avg time to be addressed:  "+avgTillAddressed+"ms");
		System.out.println("Total time to finish:      "+totalTillFinish+"ms");
		System.out.println("Avg time to finish:        "+avgTillFinish+"ms");
		System.out.println("Total job length:          "+totalJobLength+"ms");
		System.out.println("Avg job length:            "+avgJobLength+"ms");
	}

	public int hasFreeCPU(){ //If there is a CPU that is currently free, return the index of that CPU
		//System.out.println("Checking CPUs "+CPUs.size());
		for(int i = 0; i < CPUs.size(); i++){ // otherwise, return -1
			//System.out.println("Checking "+i);
			if (CPUs.get(i).isFree()){
				return i;
			}
		}

		return -1;
	}
	
	public int hasLonger(long currTime, ArrayList<Job> list){
		//System.out.println("looking for something longer than "+currTime);
		long longestTime = -1;
		int longestLoc = -1;
		for(int nextJob = 0; nextJob < list.size();nextJob++){
			//System.out.println("Checking " +j);
			long nextTime = list.get(nextJob).timeNeeded ;
			if( nextTime > currTime){
				if(nextTime > longestTime){
					longestTime = nextTime;
					longestLoc = nextJob;
				}
			}
		}
		return longestLoc;
	}

	public boolean hasJob(ArrayList<Job> list){
		for(Job j: list){
			//System.out.println("Checking " +j);
			if( j != null){
				//System.out.println("Found not null " +j);
				return true;
			}
		}
		//System.out.println("No non-null found");
		return false;
	}

	public static void main(String[] args) {
		Driver d;
		int numCPU;
		int algorithm;
		String filename;
		ArrayList<Job> jobs1 = new ArrayList<Job>();
		ArrayList<Job> jobs2 = new ArrayList<Job>();
		ArrayList<Job> jobs3 = new ArrayList<Job>();

		if(args.length < 3){
			numCPU = 3;
			algorithm = 0;
			filename = "input.txt";
		}
		else{
			//Parse number of CPUs
			numCPU = Integer.parseInt(args[0]);
			//Parse algorithm selected
			algorithm = Integer.parseInt(args[1]);
			filename = args[2];
		}

		d = new Driver(numCPU, algorithm);

		//Read in text file
		//Parse jobs file

		try(Scanner file = new Scanner(new FileReader(new File(filename)));){ //Open file

			String nextLine;
			d = new Driver(numCPU, algorithm);
			while(file.hasNextLine()){//While we still have lines
				nextLine = file.nextLine(); //Read in the next line

				int nextProc = (int)(nextLine.charAt(0))-48; //Parse info from file
				char nextType = nextLine.charAt(1);
				int nextLength = Integer.parseInt(nextLine.substring(2));

				Job j = new Job(nextProc, nextType, nextLength ); //Create a new job
				jobs1.add(j);
				jobs2.add(j);
				jobs3.add(j);
				//System.out.println("Made "+j);
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
	
	private class JobLengthComparator implements Comparator{

		@Override
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
