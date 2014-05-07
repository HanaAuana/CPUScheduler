
public class Job {
	public enum Type {CPU, IO}
	
	public int proc;
	public char type;
	public int timeNeeded;
	public long enterTime;
	public long firstAddressedTime;
	public long finishTime;
	
	public Job(int proc, char type, int timeNeeded){
		this.proc = proc;
		this.type = type;
		this.timeNeeded = timeNeeded;
		this.firstAddressedTime = -1;
		this.enterTime = -1;
		this.finishTime = -1;
	}
	
	public String getTypeString(){
		if (this.type == 'C'){
			return "CPU";
		}
		else if ( this.type == 'I'){
			return "IO";
		}
		else{
			return "Unknown";
		}
	}
	
	public String toString(){
		return "A "+getTypeString()+" job for process "+ this.proc+", started at "+this.enterTime+", and needs " + this.timeNeeded;
	}
	
}
