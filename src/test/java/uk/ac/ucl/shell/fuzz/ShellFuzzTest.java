package uk.ac.ucl.shell.fuzz;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.DequeAdaptor;

import com.code_intelligence.jazzer.junit.FuzzTest;

class ShellFuzzTest {

    @FuzzTest(maxDuration = "1m")
    public void fuzz(byte[] data) {
        Shell sh = new Shell();
        sh.eval(new String(data), new DequeAdaptor());
    }

}