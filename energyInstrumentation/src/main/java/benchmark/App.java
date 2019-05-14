package benchmark;

public class App {

    public static void main(String[] args){
        for (int i=0;i<10;i++){
            sleepMethod();
        }
    }

    public static void sleepMethod(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("I'm awake");
        addMethod();
        System.out.println("I finished adding");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("byebye");
    }

    public static void addMethod(){
        for(long i = 0L; i < 999999999L; ++i) {
            ++i;
        }
    }
}
