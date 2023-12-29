package asd;

public class YaziciService {
	private final int sistemdekiYaziciSayisi = 2;
    private int kullanimdakiYazisiSayisi = 0;
    public int getMusaitYaziciSayisi(){
        return sistemdekiYaziciSayisi - kullanimdakiYazisiSayisi;
    }

    public int getsistemdekiYaziciSayisi(){
        return this.sistemdekiYaziciSayisi;
    }
    public boolean yazicilarMusaitMi(Process process){
        return process.getyazicilar() <= getMusaitYaziciSayisi();
    }
    public void yaziciyiKullan(Process process){
        kullanimdakiYazisiSayisi += process.getyazicilar();
    }

    public void yaziciyiSerbestBirak(Process process){
        kullanimdakiYazisiSayisi -=process.getyazicilar();
    }
}
