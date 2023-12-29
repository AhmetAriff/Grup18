package asd;

public class ModemService {
<<<<<<< HEAD
	private final int sistemdekiModemSayisi = 1;
=======

    private final int sistemdekiModemSayisi = 1;
>>>>>>> 537edf65185ffaef5e192556b904ac3d647f309c
    private int kullanimdakiModemSayisi = 0;
    public int getsistemdekiModemSayisi(){
        return this.sistemdekiModemSayisi;
    }
    public int getMusaitModemSayisi(){
        return sistemdekiModemSayisi - kullanimdakiModemSayisi;
    }
    public boolean modemMusaitMi(Process process){
        return process.getModems() <= getMusaitModemSayisi();
    }
    public void modemiKullan(Process process){
        kullanimdakiModemSayisi += process.getModems();
    }
    public void modemiSerbestBirak(Process process){
        kullanimdakiModemSayisi -=process.getModems();
<<<<<<< HEAD
    }
=======
    }

>>>>>>> 537edf65185ffaef5e192556b904ac3d647f309c
}
