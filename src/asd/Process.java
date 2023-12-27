package asd;

public class Process {
	private int id;
    private int varışZamani;
    private int kuyrugaGirisZamani; // user job için mi yoksa multilevel için mi yazacağını düşün
    private int processCalismaZamani = 0; // processin ne kadarı çalıştı tutmak için
    private int priority;
    private int burstTime;
    private int Mbayt;
    private int yazicilar;
    private int tarayicilar;
    private int modems;
    private int cdDrivers;
    private final String color;

    private int hafızadakiBaslangicAdresi;


    public Process (String[] veriler,String color){
        this.varışZamani = Integer.parseInt(veriler[0]) ;
        this.priority = Integer.parseInt(veriler[1]) ;
        this.burstTime = Integer.parseInt(veriler[2]) ;
        this.Mbayt = Integer.parseInt(veriler[3]) ;
        this.yazicilar = Integer.parseInt(veriler[4]) ;
        this.tarayicilar = Integer.parseInt(veriler[5]) ;
        this.modems = Integer.parseInt(veriler[6]) ;
        this.cdDrivers = Integer.parseInt(veriler[7]);
        this.color = color;
    }


    public int getvarışZamani() {
        return varışZamani;
    }

    public void setvarışZamani(int varışZamani) {
        this.varışZamani = varışZamani;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getMbayt() {
        return Mbayt;
    }

    public void setMbayt(int mbayt) {
        Mbayt = mbayt;
    }

    public int getyazicilar() {
        return yazicilar;
    }

    public void setyazicilar(int yazicilar) {
        this.yazicilar = yazicilar;
    }

    public int gettarayicilar() {
        return tarayicilar;
    }

    public void settarayicilar(int tarayicilar) {
        this.tarayicilar = tarayicilar;
    }

    public int getModems() {
        return modems;
    }

    public void setModems(int modems) {
        this.modems = modems;
    }

    public int getCdDrivers() {
        return cdDrivers;
    }

    public void setCdDrivers(int cdDrivers) {
        this.cdDrivers = cdDrivers;
    }

    public int getkuyrugaGirisZamani() {
        return kuyrugaGirisZamani;
    }

    public void setkuyrugaGirisZamani(int kuyrugaGirisZamani) {
        this.kuyrugaGirisZamani = kuyrugaGirisZamani;
    }

    public int getprocessCalismaZamani() {
        return processCalismaZamani;
    }

    public void setprocessCalismaZamani(int processCalismaZamani) {
        this.processCalismaZamani = processCalismaZamani;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public int getHafızadakiBaslangicAdresi() {
        return hafızadakiBaslangicAdresi;
    }

    public void setHafızadakiBaslangicAdresi(int hafızadakiBaslangicAdresi) {
        this.hafızadakiBaslangicAdresi = hafızadakiBaslangicAdresi;
    }
}
