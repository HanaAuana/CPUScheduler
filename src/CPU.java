
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
			Job oldJob = myJob;
			myJob = newJob;
			jobStartTime = System.currentTimeMillis();
			isFree = false;
			return oldJob;
		}
	}
	
	public Job isDone(){
		long curTime = System.currentTimeMillis();
		if(curTime >= jobStartTime + myJob.timeNeeded){
			this.isFree = true;
			myJob.finishTime = curTime;
			
			return myJob;
		}
		else{
			return null;
		}
	}
	
	public boolean isFree(){
		return this.isFree;
	}
}
