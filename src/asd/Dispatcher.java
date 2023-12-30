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
        //TODO  process time out hatasını çöz
    public void programiBaslat(){
        System.out.println("Pid" + "\t varış" + "\t  öncelik" +"\tMBytes" +"\tprn" + "\t\tscn"+"\t modem" + "\t\tcd" + "\t\tstatus");
        while(!tümProcesslerTamamlandiMi()){
            processZamanAsimi(); // üzerine biraz daha düşün
            while(true){
              Process process =  processList.peek();

                if( process == null || process.getvarışZamani() > counter ){
                  break;
                }
                if(process.getPriority() == 0){
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
                /*else{
                    processService.yazdirDeneme(process.getColor(),process.getId(),counter,"proces sistemde yeterli kaynak olmadıgı için bekliyor");
                }*/
            }

            if(!realTimeQueue.isEmpty()){
                Process firstComeProcess = realTimeQueue.peek();
                processService.runProcessForOneSecond(firstComeProcess); // processi bir saniye işlet;
                /*processService.yazdirDeneme(firstComeProcess.getColor(),firstComeProcess.getId(),counter,"process real time kuyrukda 1 saniye calisti");*/
                firstComeProcess.setProcessDurumu("RUNNNING");
                processService.durumYazdir(firstComeProcess);

                if(processService.isProcessFinished(firstComeProcess)){
                    /*processService.yazdirDeneme(firstComeProcess.getColor(),firstComeProcess.getId(),counter,"process real timeda bitti ve atıldı");*/
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
             processService.yazdirDeneme(process.getColor(),process.getId(),counter,"process 1.öncelik kuyruğuna girdi");
         }
        else if (priority == 2){
             priority2Queue.add(process);
             processService.yazdirDeneme(process.getColor(),process.getId(),counter,"process 2.öncelik kuyruğuna girdi");
         }
        else{
             priority3Queue.add(process);
             processService.yazdirDeneme(process.getColor(),process.getId(),counter,"process 3.öncelik kuyruğuna girdi");
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

    private void processZamanAsimi() {
        realTimeQueue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                return true;  
            }
            return false;  
        });
        priority1Queue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                return true;  
            }
            return false;  
        });
        priority2Queue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                return true;  
            }
            return false;  
        });
        priority3Queue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                return true;  
            }
            return false;  
        });
        userJobQueue.removeIf(process -> {
            if (processService.isProcessTimeOutExceeded(process, counter)) {
                processService.zamanAşimiHatasiYazdir(process);
                return true;  
            }
            return false;  
        });
    }


    // round robinde kuyruğun başındaki processi işletip tekrar kuyruğa sokmalıyız
    private void askıyaAl(){
        if(!priority1Queue.isEmpty()){
            // düşün bunu bu zaten askıya alındı 1 saniye daha çalıştırma muhabbeti ödev dökümanındaki incele
        }
        else if(! priority2Queue.isEmpty()){
            Process process = priority2Queue.remove();  // 2.öncelikteli process kesme yedi 1.önceliğe attım
            priority1Queue.add(process);
            processService.yazdirDeneme(process.getColor(),process.getId(),counter,"process 2.öncelikten 1.önceliğe askıya alındı");
        }
        else if(!priority3Queue.isEmpty()){
            Process process = priority3Queue.remove();  // 3.öncelikteli process kesme yedi 2.önceliğe attım
            priority2Queue.add(process);
            processService.yazdirDeneme(process.getColor(),process.getId(),counter,"process 3.öncelikten 2.önceliğe askıya alındı");
        }
    }

    private  boolean sistemdeCalisabilirMi(Process process){
        return  process.gettarayicilar() <= tarayiciService.getSistemTarayıcıSayısı()
                && process.getyazicilar() <= yaziciService.getsistemdekiYaziciSayisi()
                && process.getModems() <= modemService.getsistemdekiModemSayisi()
                && process.getCdDrivers() <= cdDriverService.getsystemCdDriverSayisi()
                && process.getMbayt() <= memoryService.getMaximumMemoryKapasitesi(process);
    }
}

