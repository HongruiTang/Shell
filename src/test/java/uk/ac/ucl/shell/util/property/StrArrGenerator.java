package uk.ac.ucl.shell.util.property;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class StrArrGenerator extends Generator<StrArr> {
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = ".-\\;:_@[]^/|}{";
    private static final String ALL_MY_CHARS = LOWERCASE_CHARS
            + UPPERCASE_CHARS + NUMBERS + SPECIAL_CHARS;
    public static final int CAPACITY = 5000;

    public StrArrGenerator(){
        super(StrArr.class);
    }

    @Override
    public StrArr generate(SourceOfRandomness random, GenerationStatus status) {
        String lineSeparator = System.getProperty("line.separator");
        StrArr strArr = new StrArr();
        boolean firstInput = false;

        for(int idx = 0; idx < random.nextInt(50, 10000); idx++){
            StringBuilder sb = new StringBuilder(CAPACITY); 
            for (int i = 0; i < random.nextInt(50, CAPACITY); i++) { 
                int randomIndex = random.nextInt(ALL_MY_CHARS.length());
                sb.append(ALL_MY_CHARS.charAt(randomIndex));
                firstInput = true;
            }

            if(firstInput==true ||
            !sb.toString().equals(strArr.getStrArr().get(strArr.getStrArr().size()-1))){
                strArr.addStr(sb.toString() + lineSeparator);
            }
            
        }
        return strArr;
    }
}
