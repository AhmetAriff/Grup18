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
        return getBestFitIndex(process) != -1;
    }

    public void allocateMemory(Process process){
        int bestFitIndex = getBestFitIndex(process);

        if(bestFitIndex != -1){
            process.setHafızadakiBaslangicAdresi(bestFitIndex);
            for(int i = 0 ;i <process.getMbayt();i++){
                memory[bestFitIndex+i] = 1;
            }
        }
    }

    public int getBestFitIndex(Process process){
        int minFark = 960;
        int bestFitIndex = -1;
        int j = 0;
        int programiBaslatIndex  ;
        int length;
        boolean memoryTamamenBosMu = true;

        if(process.getPriority() == 0){
            programiBaslatIndex = 0; // real time icin 0 - 63 arası tathsis edilir
            length = 64;
        }
        else{
            programiBaslatIndex = 64; // user job için 64 - 1023 arası tahsis edilir
            length = 1024;
        }
        for (int i=programiBaslatIndex;i<length;i++){
            if(memory[i] == 0){
                j++;
            }
            else{
                memoryTamamenBosMu = false;
                int diff = j - process.getMbayt();
                if(diff > 0){//alan yeterli mi
                    if(diff<minFark){
                        minFark = diff;
                        bestFitIndex  = i-j;
                    }
                }
                j = 0;
            }
        }
        // eger allocate edilmek istene alan real time için 0 -64 user job için 64 1024 tamamen bos ise
        if(memoryTamamenBosMu)
            bestFitIndex = programiBaslatIndex;

        return bestFitIndex;
    }


    public void memoryyiSerbestBirak(Process process){
        for(int i = 0;i<process.getMbayt();i++){
            memory[i+process.getHafızadakiBaslangicAdresi()] = 0;
        }
    }

    public int getMaximumMemoryKapasitesi(Process process) {
        if(process.getPriority() == 0)
            return maximumMemoryKapasitesiForRealTime;
        else
            return maximumMemoryKapasitesiForUserJob;
    }
}

