package asd;

public class Main {

	public static void main(String[] args) {
		
		Color color = new Color();

	    FileService fileService = new FileService("giris.txt",color);
	    
	    ModemService modemService = new ModemService();
	       YaziciService yaziciService = new YaziciService();
	       MemoryService memoryService = new MemoryService();
	       TarayiciService tarayiciService = new TarayiciService();
	       CdDriverService cdDriverService = new CdDriverService();
	       ProcessService processService = new ProcessService();

	       Dispatcher dispatcher = new Dispatcher(
	               fileService.dosyayiOkuVeListeleriOlustur(),
	               modemService,
	               yaziciService,
	               memoryService,
	               tarayiciService,
	               cdDriverService,
	               processService
	       );


	       dispatcher.programiBaslat();
	}

}
