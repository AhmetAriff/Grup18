package asd;

public class TarayiciService {
	private final int sistemdekiTarayiciSayisi = 1;
    private int kullanimdakiTarayiciSayisi = 0;

    public  int getSistemTarayıcıSayısı(){
        return this.sistemdekiTarayiciSayisi;
    }
    public int getMusaitTarayıcıSayısı(){
        return sistemdekiTarayiciSayisi - kullanimdakiTarayiciSayisi;
    }
    public boolean TarayıcıMusaitMi(Process process){
        return process.gettarayicilar() <= getMusaitTarayıcıSayısı();
    }
    public void tarayiciyiKullan(Process process){
        kullanimdakiTarayiciSayisi += process.gettarayicilar();
    }

    public void tarayiciyiSerbestBirak(Process process){
        kullanimdakiTarayiciSayisi -= process.gettarayicilar();
    }


}
