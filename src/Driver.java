import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;




public class Driver {
	//Test
	
	public int numCPU;
	public int algorithm;
	public ArrayList<Job> jobs;
	
	public Driver(int numCPU, int algorithm){
		this.numCPU= numCPU;
		this.algorithm = algorithm;
		this.jobs = new ArrayList<Job>();
	}
	
	public void processJobs(ArrayList<Job> jobs){
		LinkedList<Job> toProcess = new LinkedList<Job>();
		
		Job currJob;
		long currentTime;
		long startTime = System.currentTimeMillis(); //Get start time
		
		while(!jobs.isEmpty()){// While we have Jobs to add
			currentTime = System.currentTimeMillis(); //Get the current time, and set the Job's "Enter time" 
			currJob = jobs.remove(0);
			currJob.enterTime = currentTime;
			toProcess.offer(currJob);// Add the job to our list to be processed
			
		}
	}

	public static void main(String[] args) {
		Driver d;
		int numCPU;
		int algorithm;
		String filename;
		
		if(args.length < 3){
			numCPU = 3;
			algorithm = 1;
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
				d.jobs.add(j);
				System.out.println("Made "+j);
			}
		}
		catch(IOException e){
			System.err.println("File Error, check file name");
			System.exit(-1);
		}
		

	}

}
