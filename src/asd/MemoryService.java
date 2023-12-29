package asd;

public class MemoryService {

	MemoryService(){
        memory = new int[1024];
        for(int i = 0;i<1024;i++){
            memory[i] = 0;
        }
    }
    private int [] memory;
    private final int  maximumMemoryKapasitesiForUserJob = 960;
    private final int  maximumMemoryKapasitesiForRealTime = 64;

    public boolean memoryMusaitMi(Process process) {
        return getBestFitIndex(process) != -1;
    }

}
