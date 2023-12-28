package asd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class FileService {
	private final Color color;
    private final String filePath;
    FileService(String filePath,Color color){
        this.filePath = filePath;
        this.color = color;
    }
    public LinkedList<Process> dosyayiOkuVeListeleriOlustur(){
        LinkedList<Process> processList = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String satir;
            int i = 1;
            while ((satir = br.readLine()) != null) {
     
                String[] veriler = satir.split(", ");
                Process process = new Process(veriler,color.getRandomColor());
                process.setId(i);
                i++;
                processList.add(process);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processList;
    }
}
