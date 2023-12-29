package asd;

public class CdDriverService {
	private int systemCdDriverSayisi = 2;
    private int kullanimdakiCdDriverSayisi = 0;
    public int getMusaitCdDriverSayisi(){
        return systemCdDriverSayisi - kullanimdakiCdDriverSayisi;
    }

    public int getsystemCdDriverSayisi(){
        return this.systemCdDriverSayisi;
    }
    public boolean cdDriverMusaitMi(Process process) {
        return process.getCdDrivers() <= getMusaitCdDriverSayisi();
    }
    public void cdDriveriKullan(Process process){
        kullanimdakiCdDriverSayisi += process.getCdDrivers();
    }

    public void cdDriveriSerbestBirak(Process process){
        kullanimdakiCdDriverSayisi -=process.getCdDrivers();
    }
}
