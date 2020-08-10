public class ReputationKeeperTest {

    public static void main(String[] args) {
        
        ReputationKeeper keeper = new ReputationKeeper();

        System.out.println("Find File expected false, " + keeper.findReputationFile());

        keeper.createReputationFile();

        System.out.println("Find File expected true, " + keeper.findReputationFile());

        keeper.updateFile("1561@debian");

        keeper.updateFile("1562@debian");

        keeper.updateFile("1563@debian");
        
        keeper.updateFile("1564@debian");

        keeper.updateFile("1561@debian");

        keeper.updateFile("1561@debian");

    }
    
}