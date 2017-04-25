

/**
 * If primary path is present at index k, then the backup paths will be in 2k and (2k+1)
 */

import java.util.*;
import java.io.*;


class requestVector {
	int source;
	int destination;
	long startTime;
	int delayTime;
	int requestNumber;
	int status;
	
	public requestVector(int source,int destination,long startTime,int delayTime,int requestNumber ){
		this.source=source;
		this.destination=destination;
		this.startTime=startTime;
		this.delayTime=delayTime;
		this.requestNumber=requestNumber;
		status=0;
	}
}
public class sharedPathProtection {

	final static int numberOfPrimaryPaths=40;
        final static int numberOfBackupPaths=80;
        final static int numOfBackups=5;
        final static int numOfNodes=14;
        volatile ArrayList<requestVector> requestVector = new ArrayList<>();
	volatile int primaryWavelength, backupWaveLength, sharedWaveLength,numberOfRequests;
	volatile ArrayList<int []> primaryPaths = new ArrayList<>();
	volatile ArrayList<int []> backupPaths = new ArrayList<>();
	volatile int[] primaryPathReserve = new int[numberOfPrimaryPaths];
	volatile int[][] backupPathReserve = new int[numberOfBackupPaths][numOfBackups];
	
	public void display(){
		int i,j;
		requestVector r1=null;
		System.out.println("====================================================================================");
		//System.out.println("The primary paths are:");
		//for (i=0;i<primaryPaths.size();++i){
		//	for (j=0;j<numOfNodes;++j){
				
		//		System.out.print(primaryPaths.get(i)[j]+"\t");
		//	}
		//	System.out.println("\n");
		//}
		
		//System.out.println("====================================================================================");

		//System.out.println("====================================================================================");
		//System.out.println("The bkp paths are:");
		//for (i=0;i<backupPaths.size();++i){
		//	for (j=0;j<numOfNodes;++j){
		//		System.out.print(backupPaths.get(i)[j]+"\t");
		//		}
		//	System.out.println("\n");
		//	}
		//System.out.println("====================================================================================");
		
		System.out.println("====================================================================================");
		System.out.println("The request vectors are:");
		//System.out.println("i="+i);
		for (i=0;i<requestVector.size();++i){
			r1=requestVector.get(i);
			System.out.print("source=" + r1.source+"\t");
			System.out.print("destination=" + r1.destination+"\t");
			System.out.print("startTime=" + r1.startTime+"\t");
			System.out.print("delayTime=" + r1.delayTime+"\t");
			System.out.print("requestNumber=" + r1.requestNumber+"\t");
			System.out.print("status=" + r1.status+"\t");
			System.out.println("");
		}
		System.out.println("====================================================================================");

		System.out.println("====================================================================================");
		System.out.println("The primary reserve vectors are:");
		//System.out.println("i="+i);
		for (i=0;i<primaryPathReserve.length;++i)
			System.out.print(primaryPathReserve[i]+"\t");
		System.out.println("\n");
		System.out.println("====================================================================================");
		/**
		System.out.println("====================================================================================");
		System.out.println("The backup reserve vectors are:");
		//System.out.println("i="+i);
		for (i=0;i<backupPathReserve.length;++i){
			for (j=0;j<numOfBackups;++j)
				System.out.print(backupPathReserve[i][j]+"\t");
		System.out.println("\n");
		}
		System.out.println("====================================================================================");
        */        
	}
	
	public int[] convertIntoArray(String str){
		int []intArr=new int [numOfNodes];
		for(int j=0;j<numOfNodes;++j)
			intArr[j]=-1;
		for(int i=0;i<str.length();i++){
			if (str.charAt(i)=='A')
				intArr[i]=10;
			else if (str.charAt(i)=='B')
				intArr[i]=11;
			else if (str.charAt(i)=='C')
				intArr[i]=12;
			else if (str.charAt(i)=='D')
				intArr[i]=13;
			else
				intArr[i]=Character.getNumericValue(str.charAt(i));
		}
		return intArr;
	}
	public void getPaths() throws IOException{
		int path[] = new int [numOfNodes];
		BufferedReader br = new BufferedReader(new FileReader("I:\\NetBeansProjects\\sharedPathProtection\\src\\PrimaryPath.txt"));
		String sCurrentLine;
		sCurrentLine=br.readLine();
		while (sCurrentLine !=	null){
			path = convertIntoArray(sCurrentLine);
			primaryPaths.addAll(Arrays.asList(path));
			sCurrentLine=br.readLine();
		}
		br.close();

		br = new BufferedReader(new FileReader("I:\\NetBeansProjects\\sharedPathProtection\\src\\BackupPath.txt"));
		sCurrentLine=br.readLine();
		while (sCurrentLine !=	null){
			path = convertIntoArray(sCurrentLine);
			backupPaths.addAll(Arrays.asList(path));
			sCurrentLine=br.readLine();
		}
		br.close();
		display();

	}
	
	public void init(){
		
		int i,j;
		for (i=0;i<numberOfPrimaryPaths;++i)
			primaryPathReserve[i]=-1;
		for (i=0;i<numberOfBackupPaths;++i)
			for (j=0;j<numOfBackups;++j)
				backupPathReserve[i][j]=-1;
	}
	
        public boolean checkForDuplicate(int source,int destination){
            boolean flag=false;
            int i;
            requestVector r1;
            
            for (i=0;i<requestVector.size();++i){
                r1=requestVector.get(i);
                
                if (r1.source==source && r1.destination==destination){
                    flag=true;
                    break;
                }
            }
            return flag;
        }
	public void generateRequest()throws InterruptedException{
		int source,destination;
		Random rand = new Random();
		
		synchronized(this)
            {
            while (numberOfRequests<500){
            	numberOfRequests = numberOfRequests +1;
            	System.out.println("In Thread1: Start generating Request: " +numberOfRequests );
            	source = rand.nextInt(10) + 4;
                    
            	destination = rand.nextInt(4) + 0;
                
                if (checkForDuplicate(source,destination)){
                  --numberOfRequests;
                  removeReservations();
                }
                else{
                    long startTime=System.currentTimeMillis();
                    double tempDelayTime=startTime%10000;
                    int delayTime;
                    if (Double.isInfinite(tempDelayTime))
                	delayTime=0;
                    else
                	delayTime = (int) tempDelayTime;
                    requestVector.add(new requestVector(source,destination,startTime,(int) Math.exp((delayTime+200)/1000),numberOfRequests));
                    display();

                }
                //if (numberOfRequests==1)
                //	requestVector.add(new requestVector(4,0,startTime,(int) Math.exp((delayTime+10)* -1),numberOfRequests));

               //if (numberOfRequests==2)
               	//requestVector.add(new requestVector(4,2,startTime,(int) Math.exp((delayTime+10)* -1),numberOfRequests));

        		//System.out.println("source="+source+"\t"+"destination="+destination);
            	//System.out.println("Generate Request thread running:numberOfRequests= " +numberOfRequests);
            	// releases the lock on shared resource
            	if (numberOfRequests>0)
            		notify();
            	//System.out.println("Going to wait in Thread1");
            	//for (int i=0;i<requestVector.size();++i)
            	//	System.out.print(requestVector.get(i)+"\t");
            	wait();
                // and waits till some other method invokes notify().
                //System.out.println("Resumed Request Generation");
                //display();
                //System.out.println("primaryWavelength = " +primaryWavelength+ "backupWaveLength = "+backupWaveLength+"sharedWaveLength = "+sharedWaveLength);
                }
            }
		return;
	}
	
	public int getPrimaryPath(){
		int i=0,j=0,index=0;
		int src,dest;
		long startT;
		double delayT;
		boolean reserved=false;
		requestVector r1=null;
                requestVector r2=null;
		
		for (index=0;index<requestVector.size();++index){
			r1=requestVector.get(index);
			if (r1.status==0)
				break;
		}
                if (requestVector.size()> 0 && r1.status==0){
		src = r1.source;
		dest=r1.destination;
		startT=r1.startTime;
		delayT=r1.delayTime;
                
		
		for (i=0;i<primaryPaths.size();++i){
			for (j=0;j<numOfNodes;++j){
				if (primaryPaths.get(i)[0]==src && primaryPaths.get(i)[j]==dest && primaryPaths.get(i)[j+1]==-1){
					//primaryPaths.get(i)[j+2] = r1.requestNumber;
					primaryPathReserve[i] = r1.requestNumber;
					reserved = true;
					break;
				}
			}
			if (reserved)
				break;
                    }
		//System.out.println("Value of j="+j);
                }
		if (!reserved)
			i=999;
		else{
			r1.status=2;
			requestVector.set(index,r1);
		}
		return i;
		
	}
	public int processPrimaryPath(){
		int k=0;
		k=getPrimaryPath();
		return k;
	}
	
	public boolean linkJoint(int path1[],int path2[]){
		boolean flag=false;
		for (int i=0; i<numOfNodes-1;++i){
			if (path1[i]==path2[i] && path1[i+1]==path2[i+1])
				flag=true;
		}
		return flag;
	}
	
	/**
	public boolean checkPrimaryReservation(int k){
		boolean flag=false;
		for (int i=0;i<13;++i){
			if (primaryPaths.get(k)[i]==-1 && primaryPaths.get(k)[i+1]>0){
				flag=true;
				break;
			}
		}
		return flag;
	}
	*/
	
	
	public void reserveBackupPath(int k){
		int i=0,j=0,index=0,l;
		// int tempReq;
		//boolean reserved=false,backupPathLinkJoint,primaryPathLinkJoint=false,primaryReserved,reserveFlag=false;
		boolean primaryPathLinkJoint=false,reserveFlag=false,reserved=false;
		requestVector r1=null;
		i=2*k;
		
		for (index=0;index<requestVector.size();++index){
			//System.out.println("Index =" +index);
			r1=requestVector.get(index);
			if (r1.status==2)
				break;
		}
		reserveFlag=false;
		for (j=0;j<numOfBackups;++j){
			if (backupPathReserve[i][j]==-1)
				continue;
			else {
				reserveFlag=true;
				break;
			}
			
		}
		
		if (!reserveFlag){
			for (j=0;j<5;++j){
				if (backupPathReserve[i][j]!=-1){
					//tempReq = backupPathReserve[i][j];
					for (l=0;l<primaryPathReserve.length;++l){
						if (primaryPathReserve[l]==backupPathReserve[i][j]){
							primaryPathLinkJoint = linkJoint(primaryPaths.get(k),primaryPaths.get(l));
							if (primaryPathLinkJoint)
								break;
						}
					}
				}
			}
		}
		else {
			backupPathReserve[i][0]=r1.requestNumber;
			//System.out.println("Done1");
			reserved=true;
			return;
		}
		
		if (!primaryPathLinkJoint && !reserved){
			for (j=0;j<numOfBackups;++j){
				if (backupPathReserve[i][j]==-1) {
					backupPathReserve[i][j] = r1.requestNumber;
					//System.out.println("Done2"+ "i="+i+"\t"+"j=" +j);
					r1.status=1;
					requestVector.set(index,r1);

					return;
//					break;
				}
			}
		}
		else{
			i=(2*k)+1;
			reserveFlag=false;
			for (j=0;j<numOfBackups;++j){
				if (backupPathReserve[i][j]==-1)
					continue;
				else {
					reserveFlag=true;
					break;
				}
				
			}
			if (!reserveFlag){
				for (j=0;j<numOfBackups;++j){
					if (backupPathReserve[i][j]!=-1){
						//tempReq = backupPathReserve[i][j];
						for (l=0;l<primaryPathReserve.length;++l){
							if (primaryPathReserve[l]==backupPathReserve[i][j]){
								primaryPathLinkJoint = linkJoint(primaryPaths.get(k),primaryPaths.get(l));
								if (primaryPathLinkJoint)
									break;
							}
						}
					}
				}
			}
			else {
				backupPathReserve[i][0]=r1.requestNumber;
				//System.out.println("Done3");
				r1.status=1;
				requestVector.set(index,r1);
				reserved=false;
				return;
			}			
		}
		
		if (!primaryPathLinkJoint && !reserved){
			for (j=0;j<numOfBackups;++j){
				if (backupPathReserve[i][j]==-1){
					backupPathReserve[i][j] = r1.requestNumber;
					//System.out.println("Done4"+ "i="+i+"\t"+"j=" +j);
					r1.status=1;
					requestVector.set(index,r1);
					//break;
					return;
				}
				
			}
		}
	}
	
	public int endOfPath(int indicator, int index){
		int returnValue=0;
		if (indicator==0){
			for (int i=0;i<numOfNodes-1;++i){
				if (backupPaths.get(index)[i] == -1)
					returnValue = i;	
			}
		}
		if (indicator==1){
			for (int i=0;i<numOfNodes-1;++i){
				if (primaryPaths.get(index)[i] == -1)
					returnValue = i;	
			}
		}
		return returnValue;
	}
	
	public void calcBkpWavelengths(int index) {
		int len=0;
		int endOfPathInd=0;
		
		endOfPathInd=endOfPath(0,index);
		//len = endOfPathInd - 1; 
		for (int i=0;i<endOfPathInd;++i){
			if (backupPaths.get(index)[i]>=0 && backupPaths.get(index)[i+1] >=0)
				len=len+1;
			if (backupPaths.get(index)[i]==-1)
				break;
		}
		
		backupWaveLength = backupWaveLength + len;
	}
	
	public boolean checkReserved(int i){
		boolean flag=false;
		for (int j=0;j<numOfNodes-1;++j){
			if (backupPaths.get(i)[j]== -1 && backupPaths.get(i)[j+1]>0)
				flag=true;
		} 
		
		return flag;
	}
	
	public void calcSharedWavelengths(int index) {
		int len=0;
		int i,j;
		
		for (i=index+1;i<backupPaths.size()-1;++i){
			if (checkReserved(i)){
				for (j=0;j<numOfNodes-1;++j){
					if (backupPaths.get(index)[j]== backupPaths.get(i)[j] && backupPaths.get(index)[j+1]== backupPaths.get(i)[j+1])
						len=len+1;
					if ((backupPaths.get(index)[j]==-1) || (backupPaths.get(i)[j])== -1)
						break;
				}
			}
		}
		
		sharedWaveLength = sharedWaveLength + len;
	}
	
	public void calcPrimaryWavelengths(int index) {
		int len=0,endOfPathInd;
		endOfPathInd=endOfPath(1,index);
		//len = endOfPathInd - 1; 
		for (int i=0;i<endOfPathInd;++i){
			if (primaryPaths.get(index)[i]>=0 && primaryPaths.get(index)[i+1] >=0)
				len=len+1;
			if (primaryPaths.get(index)[i]==-1)
				break;
		}
		primaryWavelength = primaryWavelength + len;
	}

	

	public void scanWavelengths(){
		int i,j;
		
		for (i=0;i<backupPathReserve.length;++i){
			for(j=0;j<numOfBackups;++j){
				if(backupPathReserve[i][j]==numberOfRequests ){
					calcBkpWavelengths(i);
		//			calcSharedWavelengths(i);
				}
			}
		}
		for (i=0;i<primaryPathReserve.length;++i){
				if(primaryPathReserve[i]==numberOfRequests){
					calcPrimaryWavelengths(i);
				}
			}

	}
	
	public void removePathReservation(int indicator,int reqNumber){
		boolean flag=false;
		boolean removalFlag=false;
		if (indicator ==0){   //indicator 0 indicates primary path
			for (int i=0; i<primaryPathReserve.length;++i){
				if (primaryPathReserve[i]==reqNumber){
					primaryPathReserve[i] = -1;
				}
			}			
		}
		
		if (indicator ==1){ //indicator 1 indicates backup path
			for (int i=0; i<backupPathReserve.length;++i){
				for (int j=0; j<numOfBackups;++j){
					if (backupPathReserve[i][j] == reqNumber){
						backupPathReserve[i][j]=-1;
					}
				}
			}
			
		}
		return;
	}
	
	public void removeReservations(){
		long presentTime=System.currentTimeMillis();
		int index;
		requestVector r1=null;
		//System.out.println("presentTime=" + presentTime);
		
		for (index=0;index<requestVector.size();++index){
	//		System.out.println("Index =" +index);
			r1=requestVector.get(index);
			if (r1.startTime+r1.delayTime<presentTime){
				//System.out.println("Here i am");
				if (r1.status==1){
					removePathReservation(0,r1.requestNumber);
					removePathReservation(1,r1.requestNumber);
					requestVector.remove(index);
				}
			}
		}
		
	}
	
	public void serveRequest()throws InterruptedException {
        synchronized(this)
        {
            while (true){
            	System.out.println("In Thread2:"+numberOfRequests);
            	int k=processPrimaryPath();
            	if (k!=999) {
            	reserveBackupPath(k);
            	scanWavelengths();
            	System.out.println("Before Removal:");
            	display();
            	removeReservations();
            	System.out.println("After Removal:");
            	display();
                System.out.println("primaryWavelength = "+primaryWavelength+" backupWaveLength ="+backupWaveLength);
                }
            	notify();
            	wait();
            	
            }
        }
		
	}
	
	
	public static void main(String[] args)throws InterruptedException,IOException {
		
		System.out.println("Starting..");
		final sharedPathProtection SP1 = new sharedPathProtection();
		SP1.init();
		SP1.getPaths();
		
		
		// Create a thread object that calls SP1.generateRequest()
		
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SP1.generateRequest();
					}
				catch(InterruptedException e){
					e.printStackTrace();
					}
				}
			});
		// Create another thread object that calls SP1.serveRequest()
		
		Thread t2 = new Thread(new Runnable(){
			@Override
			public void run(){
				try {
					SP1.serveRequest();
					}
				catch(InterruptedException e){
					e.printStackTrace();
					}
				}
			});
		// Start both threads
		t1.start();
		t2.start();
		
		// t1 finishes before t2
		t1.join();
		t2.join();
		}
	}
