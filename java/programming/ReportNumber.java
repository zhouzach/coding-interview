package programming;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 *
 * 有n个人围成圈，顺序排号，从第一个开始报数（从1到3），凡报到3的人退出圈子，最后留下的是原来的第几号位
 */
public class ReportNumber {
    public static void main(String[] args){
        int n=computeLastNum(10);
        System.out.println(n);
    }

    public static int computeLastNum(int n){

        final int modNum = 3;

        List<Integer> people = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            people.add(i + 1);
        }
        System.out.println("初始化编号为" + people + "的" + people.size() + "个人围成一圈");

        ListIterator<Integer> iter = null;
        int k = 1;
        do {
            iter = people.listIterator();
            while (iter.hasNext()) {
                int i = iter.next();
                if (k++ % modNum == 0) {
                    System.out.println("编号" + i + "的人退出圈子");
                    iter.remove();
                    k = 1;
                }
            }
        } while (people.size() > 1);

        System.out.println("剩下编号为" + people + "的" + people.size() + "个人");

        System.out.println(people.get(0));

        return people.get(0);
    }
}
