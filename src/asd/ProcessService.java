package asd;

public class ProcessService {

	public void runProcessForOneSecond(Process process){
        int processCalismaZamani = process.getprocessCalismaZamani();
        processCalismaZamani++; // 1 birim çalıştırıldı
        process.setprocessCalismaZamani(processCalismaZamani);
    }

    public boolean isProcessFinished(Process process){
        return process.getBurstTime() == process.getprocessCalismaZamani();
    }

    public boolean isProcessTimeOutExceeded(Process process,int count){
        return (count == process.getkuyrugaGirisZamani() + 21);   // 20 saniye kadark kalabiliyor max
    }
    public void yazdirDeneme (String color ,int processId,int time, String message){
        System.out.println(color + "processin idsi = " + processId + " zaman = " + time +" mesaj = " + message );
    }

    public void durumYazdir(Process process){
        System.out.println(process.getColor() + process.getId() + "\t\t" + process.getVarışZamani() + "\t\t" + process.getPriority() + "\t\t" + process.getMbayt() + "\t\t" + process.getyazicilar() + "\t\t" + process.gettarayicilar() + "\t\t" + process.getModems() + "\t\t"+ process.getCdDrivers() + "\t\t" + process.getProcessDurumu());
    }

    public void cokSayidaKaynakHatasiYazdir(Process process){
        System.out.println(process.getId() + "\tHATA   Prcess çok sayıda kaynak talep ediyor - process silindi");
    }

    public void realTimeHafizaHatasiYazdir(Process process){
        System.out.println(process.getId() +"\tHata Gerçek zamanlı process (64mb) dan fazla hazfıza talep ediyor - process silindi");
    }

    public void userJobHafizaHatasiYazdir(Process process){
        System.out.println(process.getId() + "\tHata  process (960mb) dan fazla hazfıza talep ediyor - process silindi");
    }

    public void zamanAşimiHatasiYazdir(Process process){
        System.out.println(process.getId() + "\tHATA process zaman aşımı (20 snde tamamlanamadı)");
    }

}

