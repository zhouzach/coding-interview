package programming;


/**
 *
 *
 * 如何用1个6L的水杯和一个5L的水杯，精确的打3L水?
 *
 * 将6L的水杯中的水倒入5L的水杯中，剩下1L的水，再讲5L水杯的水清空，将1L的水倒进去5L水杯中，
 * 再将6L的水倒入剩有1L水的5L水杯中，6L的水杯中剩下2L，再将它倒入空的5L水杯中，再将6L的水导入倒入剩有2L水的5L水杯中，
 * 这时候6L的水杯就剩下3L的水
 */
public class WaterCup {

    public static void main(String[] args){
        get3LWater();

    }

    public static void get3LWater(){
        final int BigCupSize = 6, SmallCupSize = 5;

        int iSrcRemain = 0, iDestRemain = 0, iTurn = 0;
        while (iDestRemain != 3)
        {
            iSrcRemain = BigCupSize;  //大杯倒满水
            iSrcRemain = (iDestRemain + BigCupSize) % SmallCupSize;    //倒入小杯后大杯剩余
            iDestRemain = iSrcRemain; //往小杯中倒入大杯中的剩余
            iTurn++;
            System.out.println("第" + iTurn + "次，小杯剩余" + iDestRemain);
        }

        System.out.println("ok!\n");

    }
}
