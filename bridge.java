public class bridge {
    BridgeControl n = new BridgeControl();

    public void Person() {
        n.enterBridge();
        int numberOfCrossing = n.getTheNumberOfCrossing();
        // if 0 then bridge 1
        // if 1 then bridge 2
        CrossBrige(numberOfCrossing+1);
        n.exitBridge();

    }
    
}

class monitor BridgeControl{
    private int numberOfCrossing;
    condition bridgeWaiting;

    public BridgeControl(){
        numberOfCrossing = 0;
    }

    public synchronized void enterBridge(){
        if(numberOfCrossing > 2){
            try{
                bridgeWaiting.wait();
            }catch(InterruptedException e){
              
            }
        }
        numberOfCrossing++;
    }

    public getTheNumberOfCrossing(){
        return numberOfCrossing;
    }

    public synchronized void exitBridge(){
        numberOfCrossing--;
        bridgeWaiting.notifyAll();
    }


}