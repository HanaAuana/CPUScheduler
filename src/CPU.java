//Michael Lim

//A class to represent a CPU
public class CPU {

	int id;
	public boolean isFree;
	public Job myJob;
	public long jobStartTime;

	public CPU(int id){
		this.id = id;
		isFree = true;
		myJob = null;
		jobStartTime = -1; //This will be set when we get a job
	}
	
	public Job assignJob(Job newJob){//Given a Job, if we don't currently have a job, simply take it. Otherwise evict our current job first
		if(newJob.enterTime == -1){ //If our new job doesn't have an enter time, set it to now
			newJob.enterTime = System.currentTimeMillis();
		}
		if(newJob.firstAddressedTime == -1){//If our new job doesn't have a start time, set it to now
			newJob.firstAddressedTime = System.currentTimeMillis();
		}
		if(this.isFree == true){//If we don't already have a job
			myJob = newJob;
			jobStartTime = System.currentTimeMillis(); //Set this jobs start time to now
			isFree = false;
			return null;
		}
		else{// If we already have a job
			Job oldJob = new Job(myJob.proc, myJob.type, myJob.originalTime); //Make a copy of our old job
			oldJob.timeNeeded = myJob.timeNeeded;
			oldJob.enterTime = myJob.enterTime;
			
			myJob = newJob;//Set our job to this new Job
			jobStartTime = System.currentTimeMillis();//Set the job start time to now
			isFree = false;
			return oldJob; //Return the old job
		}
	}
	
	//Given an amount of time worked, check if our current job is done working
	public Job isDone(long timeWorked){
		long curTime = System.currentTimeMillis();
		myJob.timeNeeded -= timeWorked; //Apply the amount of work done
		
		if(myJob.timeNeeded < 0){//Can't need negative work time
			myJob.timeNeeded = 0;
		}
		
		if( myJob.timeNeeded == 0){//If we're done
			this.isFree = true;
			myJob.finishTime = curTime;	
			return myJob;//Return our finished job
		}
		else{//If we're not done, return null
			return null;
		}
	}
	
	public boolean isFree(){//Return true if we're free, false if we have a job already
		return this.isFree;
	}
}
