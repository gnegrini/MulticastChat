import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReputationKeeper {

    private String fileName = "reputationRank.csv";
    private String filePath = "/home/gnegrini/Distribuidos/MulticastNewsShare/";
    private String lineSeparator = System.getProperty("line.separator");

    public boolean networkAlreadyHasKeeper;

    public void startKeeper() {

        if (findReputationFile()) {
            networkAlreadyHasKeeper = true;
        } else {
            networkAlreadyHasKeeper = false;
            createReputationFile();
        }        
    }

    public boolean findReputationFile() {

        File csvFile = new File(filePath + fileName);
        if (csvFile.isFile()) {
            return true;
        }
        return false;
    }

    public void createReputationFile() {

        if (!networkAlreadyHasKeeper) {

            FileWriter csvWriter;
            try {
                csvWriter = new FileWriter(filePath + fileName);

                csvWriter.append("sender");
                csvWriter.append(",");
                csvWriter.append("numOfFakeNews");
                csvWriter.append(lineSeparator);

                csvWriter.flush();
                csvWriter.close();
            } catch (IOException e) {
                System.out.println("Error while creating the reputation file");
                e.printStackTrace();
            }            
            
        }
        
    }
    
    //https://stackoverflow.com/questions/1377279/find-a-line-in-a-file-and-remove-it
	public void updateFile(String sender) {        

        int numOfFakeNews = 1;        

        // search each line fo the file for the sender
        // if found sender
        //      update senderExists var
        //      read numOfFakeNews and increment counter
        //      save senderlineNumber
        // if senderExist
        //      delete line by senderLineNumber
        // write sender, numOfFakeNews to file
        try{
            File inputFile = new File(filePath+fileName);
            File tempFile = new File(filePath+"tempFile.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.contains(sender)){
                    trimmedLine = trimmedLine.replace(lineSeparator, "");
                    String[] data = trimmedLine.split(",");
                    numOfFakeNews += Integer.parseInt(data[1]);                    

                    continue;
                }
                writer.write(currentLine + lineSeparator);
            }
            writer.write(sender+","+numOfFakeNews+lineSeparator);
            writer.close(); 
            reader.close(); 
            if(!tempFile.renameTo(inputFile)){
                throw new Exception("Could not remove file");
            }
                
        } catch(Exception e){
            System.out.println("Error while updating reputation file");
            e.printStackTrace();
        }
        
    }
    
    
}