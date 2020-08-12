import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReputationKeeper {

    private String fileName = "badReputationRank.csv";
    private String filePath = "/home/gnegrini/Distribuidos/MulticastNewsShare/";
    private String lineSeparator = System.getProperty("line.separator");


    public ReputationKeeper(String username){
        this.fileName = username+fileName;
    }


    public void startKeeper() {

        createReputationFile();

    }

    /**
     *  Checks if the file already exists
     * */
    private boolean findReputationFile() {

        File csvFile = new File(filePath + fileName);
        if (csvFile.isFile()) {
            return true;
        }
        return false;
    }

    /** 
     * Create initial file with  the headers
     * */
    private void createReputationFile() {


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

    /**
     * Updates the file, incrementing the number of fake news from a sender
     * if it already exists or just add new sender to the file
     * Since Java doesn't have this feature, we need to read the file,
     * write it to another one and rename it in the end.
     * Adapted from:
     * //https://stackoverflow.com/questions/1377279/find-a-line-in-a-file-and-remove-it
     * @param sender
     */
	public void updateFile(String sender) {

        int numOfFakeNews = 1;
        
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