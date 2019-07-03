package concurrent;

public class RunnableObject implements Runnable{

    public void run(){
        System.out.println("RunnableObject run");
    }
}


class ThreadObject extends Thread{
    public void run(){
        System.out.println("ThreadObject run");

    }

}
class Demo{
    public static void main(String[] args){
        RunnableObject runnableObject = new RunnableObject();
        runnableObject.run();
        ThreadObject threadObject = new ThreadObject();
        threadObject.run();
    }
}