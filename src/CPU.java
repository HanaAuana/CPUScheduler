
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
		if(this.isFree == true){
			myJob = newJob;
			jobStartTime = System.currentTimeMillis();
			isFree = false;
			return null;
		}
		else{
			Job oldJob = new Job(myJob.proc, myJob.type, myJob.originalTime);
			oldJob.timeNeeded = myJob.timeNeeded;
			//oldJob.timeNeeded -= (System.currentTimeMillis()- jobStartTime);
			myJob = newJob;
			jobStartTime = System.currentTimeMillis();
			isFree = false;
			return oldJob;
		}
	}
	
	public Job isDone(){
		
		long curTime = System.currentTimeMillis();
		myJob.timeNeeded -= (curTime-jobStartTime);
		if(myJob.timeNeeded < 0){
			myJob.timeNeeded = 0;
		}
		if( myJob.timeNeeded == 0){
			this.isFree = true;
			myJob.finishTime = curTime;		
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
