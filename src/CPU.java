
public class CPU {

	int id;
	public boolean isFree;
	public Job myJob;
	public long jobStartTime;

	public CPU(int id){
		this.id = id;
		isFree = true;
		myJob = null;
		jobStartTime = -1;
	}
	
	public Job assignJob(Job newJob){
		if(newJob.enterTime == -1){
			newJob.enterTime = System.currentTimeMillis();
		}
		if(newJob.firstAddressedTime == -1){
			newJob.firstAddressedTime = System.currentTimeMillis();
		}
		if(this.isFree == true){
			myJob = newJob;
			jobStartTime = System.currentTimeMillis();
			isFree = false;
			return null;
		}
		else{
			Job oldJob = new Job(myJob.proc, myJob.type, myJob.originalTime);
			oldJob.timeNeeded = myJob.timeNeeded;
			oldJob.enterTime = myJob.enterTime;
			//oldJob.timeNeeded -= (System.currentTimeMillis()- jobStartTime);
			myJob = newJob;
			//System.err.println("New Job "+newJob.enterTime+" Old Job "+ oldJob.enterTime);
			jobStartTime = System.currentTimeMillis();
			isFree = false;
			return oldJob;
		}
	}
	
	public Job isDone(long timeWorked){
		
		long curTime = System.currentTimeMillis();
		
		//System.out.println("Job needed "+myJob.timeNeeded+"ms more");
		//System.out.println("Job got "+timeWorked+"ms of work");
		myJob.timeNeeded -= timeWorked;
		//System.out.println("Job needs "+myJob.timeNeeded+"ms more");
		if(myJob.timeNeeded < 0){
			myJob.timeNeeded = 0;
		}
		if( myJob.timeNeeded == 0){
			this.isFree = true;
			myJob.finishTime = curTime;	
			//System.out.println("Job finished in  "+(myJob.finishTime-myJob.enterTime)+"ms");
			return myJob;
		}
		else{
			//System.out.println("Job needs "+myJob.timeNeeded+"ms more");
			return null;
		}
	}
	
	public boolean isFree(){
		return this.isFree;
	}
}
