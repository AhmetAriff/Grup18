package asd;

import java.util.LinkedList;
import java.util.Queue;

public class Dispatcher {

	private final Queue<Process> processList;
    private final Queue<Process> realTimeQueue;
    private final Queue<Process> priority1Queue;
    private final Queue<Process> priority2Queue;
    private final Queue<Process> priority3Queue;
    private final LinkedList<Process> userJobQueue;
    private final ModemService modemService;
    private final YaziciService yaziciService;
    private final MemoryService memoryService;
    private final TarayiciService tarayiciService;
    private final CdDriverService cdDriverService;
    private final    ProcessService processService;
    private int counter = 0; // saniye tutacak

    Dispatcher(
            LinkedList<Process> processList
            , ModemService modemService
            , YaziciService yaziciService
            , MemoryService memoryService
            , TarayiciService tarayiciService
            , CdDriverService cdDriverService
            , ProcessService processService
    ){
        this.processList = processList;
        this.priority1Queue = new LinkedList<>();
        this.priority2Queue = new LinkedList<>();
        this.priority3Queue = new LinkedList<>();
        this.realTimeQueue  = new LinkedList<>();
        this.userJobQueue = new LinkedList<>();
        this.modemService = modemService;
        this.yaziciService = yaziciService;
        this.memoryService = memoryService;
        this.tarayiciService = tarayiciService;
        this.cdDriverService = cdDriverService;
        this.processService = processService;
    }
    public void programiBaslat(){
    	basligiYaz();
        while(!tümProcesslerTamamlandiMi()){
            processZamanAsimi();
            while(true){
              Process process =  processList.peek();

                if( process == null || process.getvarışZamani() > counter ){
                  break; // process listtin en önündeki processin zamanı henüz gelmemişse
                }
                if(process.getPriority() == 0){ // process realtime ise
                    if(realTimeQueue.isEmpty()){
                        askıyaAl();
                    }
                    if(sistemdeCalisabilirMi(process)){
                        process.setProcessDurumu("RUNNING");
                        processService.durumYazdir(process);
                        realTimeQueue.add(process);
                        allocateProcessResources(process);
                        process.setkuyrugaGirisZamani(counter);
                    }
                    else{
                        if(process.getMbayt() > 64)
                        {
                            processService.realTimeHafizaHatasiYazdir(process);
                        }
                        else{
                            processService.cokSayidaKaynakHatasiYazdir(process);
                        }
                    }
                }
                else{
                    userJobQueue.add(process);
                }
                processList.remove(process);
            }

            for(int i = 0 ; i<userJobQueue.size();i++){
                Process process = userJobQueue.get(userJobQueue.size()-i-1);
                if(!sistemdeCalisabilirMi(process)){
                    userJobQueue.remove(process);
                    i++;
                    if (process.getMbayt() > 960){
                        processService.userJobHafizaHatasiYazdir(process);
                    }
                    else{
                        processService.cokSayidaKaynakHatasiYazdir(process);
                    }
                }
                else if(processKaynaklariMusaitMi(process)){ // processin kaynak gerkesinimleri sistemde şuan mevcut mu
                    allocateProcessResources(process);
                    addMultiLevelQueue(process); // kuyruklara giriş yaptı
                    userJobQueue.remove(process);// process listesinden kaldır
                    i++;
                }
            }

            if(!realTimeQueue.isEmpty()){
                Process firstComeProcess = realTimeQueue.peek();
                processService.runProcessForOneSecond(firstComeProcess); // processi bir saniye işlet; 
                firstComeProcess.setProcessDurumu("RUNNNING");
                processService.durumYazdir(firstComeProcess);

                if(processService.isProcessFinished(firstComeProcess)){
                    firstComeProcess.setProcessDurumu("COMPLETED");
                    processService.durumYazdir(firstComeProcess);
                    Process process = realTimeQueue.remove(); // bittiyse yolla
                    kaynaklariSerbestBirak(process); // kaynak iadesi
                }
            }
            else if (!priority1Queue.isEmpty()){
                Process process = priority1Queue.remove();
                processService.runProcessForOneSecond(process);
                process.setProcessDurumu("RUNNING");
                processService.durumYazdir(process);

                if(!processService.isProcessFinished(process)){
                    priority2Queue.add(process);
                }
                else{
                    kaynaklariSerbestBirak(process);
                    process.setProcessDurumu("COMPLETED");
                    processService.durumYazdir(process);
                }
            }
            else if (!priority2Queue.isEmpty()) {
                Process process = priority2Queue.remove();
                processService.runProcessForOneSecond(process);
                process.setProcessDurumu("RUNNING");
                processService.durumYazdir(process);
                if(!processService.isProcessFinished(process)){
                    priority3Queue.add(process);
                }
                else{
                    kaynaklariSerbestBirak(process);
                    process.setProcessDurumu("COMPLETED");
                    processService.durumYazdir(process);
                }

            }
            else if (!priority3Queue.isEmpty()) {
                Process process = priority3Queue.remove();
                processService.runProcessForOneSecond(process);
                process.setProcessDurumu("RUNNING");
                processService.durumYazdir(process);
                if(!processService.isProcessFinished(process)){
                    priority3Queue.add(process); // fronttan çıkarttım backe ekledim
                }
                else{
                    kaynaklariSerbestBirak(process);
                    process.setProcessDurumu("COMPLETED");
                    processService.durumYazdir(process);
                }
            }
            counter ++;  // en son counteri arttıracam

            //processZamanAsimi();
        }
    }

    private void kaynaklariSerbestBirak(Process process) {
        memoryService.memoryyiSerbestBirak(process);
        tarayiciService.tarayiciyiSerbestBirak(process);
        modemService.modemiSerbestBirak(process);
        yaziciService.yaziciyiSerbestBirak(process);
        cdDriverService.cdDriveriSerbestBirak(process);
    }

    private boolean tümProcesslerTamamlandiMi() {
        return counter != 0
                && userJobQueue.isEmpty()
                && realTimeQueue.isEmpty()
                && priority1Queue.isEmpty()
                && priority2Queue.isEmpty()
                && priority3Queue.isEmpty()
                && processList.isEmpty();
    }

    private void addMultiLevelQueue(Process process){
        int priority = process.getPriority();
         if (priority == 1){
             priority1Queue.add(process);
         }
        else if (priority == 2){
             priority2Queue.add(process); 
         }
        else{
             priority3Queue.add(process);
         }
        process.setkuyrugaGirisZamani(counter); // process kuyruğa ne zaman girdi
    }

    private boolean processKaynaklariMusaitMi(Process process){
        return modemService.modemMusaitMi(process)
                && memoryService.memoryMusaitMi(process)
                && tarayiciService.TarayıcıMusaitMi(process)
                && yaziciService.yazicilarMusaitMi(process)
                && cdDriverService.cdDriverMusaitMi(process);
    }

    private void allocateProcessResources(Process process){
        modemService.modemiKullan(process);
        yaziciService.yaziciyiKullan(process);
        tarayiciService.tarayiciyiKullan(process);
        memoryService.allocateMemory(process);
        cdDriverService.cdDriveriKullan(process);
    }

    private void processZamanAsimi() { // gerekli yerleri dolaşıp 20 saniyeden fazla sistemde duran process varsa sistemden çıkar ve hata mesajı yazdır
        realTimeQueue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                kaynaklariSerbestBirak(process);
                return true;  
            }
            return false;  
        });
        priority1Queue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                kaynaklariSerbestBirak(process);
                return true;  
            }
            return false;  
        });
        priority2Queue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                kaynaklariSerbestBirak(process);
                return true;  
            }
            return false;  
        });
        priority3Queue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                kaynaklariSerbestBirak(process);
                return true;  
            }
            return false;  
        });
        userJobQueue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                kaynaklariSerbestBirak(process);
                return true;  
            }
            return false;  
        });
    }

    private void askıyaAl(){
        if(!priority1Queue.isEmpty()){
        	 Process process = priority1Queue.peek();
        	 process.setProcessDurumu("ASKIYA ALINDI");
             processService.durumYazdir(process);
        }
        else if(! priority2Queue.isEmpty()){
            Process process = priority2Queue.remove();  // 2.öncelikteli process kesme yedi 1.önceliğe attım
            priority1Queue.add(process);
            process.setProcessDurumu("ASKIYA ALINDI");
            processService.durumYazdir(process);
        }
        else if(!priority3Queue.isEmpty()){
            Process process = priority3Queue.remove();  // 3.öncelikteli process kesme yedi 2.önceliğe attım
            priority2Queue.add(process);
            process.setProcessDurumu("ASKIYA ALINDI");
            processService.durumYazdir(process);
        }
    }

    private  boolean sistemdeCalisabilirMi(Process process){
        return  process.gettarayicilar() <= tarayiciService.getSistemTarayıcıSayısı()
                && process.getyazicilar() <= yaziciService.getsistemdekiYaziciSayisi()
                && process.getModems() <= modemService.getsistemdekiModemSayisi()
                && process.getCdDrivers() <= cdDriverService.getsystemCdDriverSayisi()
                && process.getMbayt() <= memoryService.getMaximumMemoryKapasitesi(process);
    }
    private void basligiYaz(){
        System.out.println("\t  Pid" + "     varis" + "     oncelik" +"     MBytes" +"     prn" + "        scn"+"       modem" + "     cd" + "    status");
        System.out.println("----------------------------------------------------------------------------------------------");
    }
}

