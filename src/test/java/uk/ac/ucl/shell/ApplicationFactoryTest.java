package uk.ac.ucl.shell;

import org.junit.Test;

import uk.ac.ucl.shell.app.Application;
import uk.ac.ucl.shell.exception.ShellException;

import static org.junit.Assert.*;

public class ApplicationFactoryTest {

    @Test
    public void givenIncorrectAppName_thenThrowException() {
        Exception exception = assertThrows(ShellException.class, () -> {
            ApplicationFactory.getApplication("p_wd");
        });
        String expectedMessage = "no existing application";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenUnsafeAppName_thenCreateUnsafeDecoratorObject() {
        Application app = ApplicationFactory.getApplication("_pwd");
        assertTrue(app.getClass().toString().endsWith("UnsafeDecorator"));
    }

    @Test
    public void givenAppNamePwd_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("pwd");
        assertTrue(app.getClass().toString().endsWith("Pwd"));
    }

    @Test
    public void givenAppNameCd_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("cd");
        assertTrue(app.getClass().toString().endsWith("Cd"));
    }

    @Test
    public void givenAppNameLs_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("ls");
        assertTrue(app.getClass().toString().endsWith("Ls"));
    }

    @Test
    public void givenAppNameCat_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("cat");
        assertTrue(app.getClass().toString().endsWith("Cat"));
    }

    @Test
    public void givenAppNameEcho_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("echo");
        assertTrue(app.getClass().toString().endsWith("Echo"));
    }

    @Test
    public void givenAppNameHead_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("head");
        assertTrue(app.getClass().toString().endsWith("Head"));
    }

    @Test
    public void givenAppNameTail_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("tail");
        assertTrue(app.getClass().toString().endsWith("Tail"));
    }

    @Test
    public void givenAppNameGrep_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("grep");
        assertTrue(app.getClass().toString().endsWith("Grep"));
    }

    @Test
    public void givenAppNameCut_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("cut");
        assertTrue(app.getClass().toString().endsWith("Cut"));
    }

    @Test
    public void givenAppNameFind_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("find");
        assertTrue(app.getClass().toString().endsWith("Find"));
    }

    @Test
    public void givenAppNameUniq_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("uniq");
        assertTrue(app.getClass().toString().endsWith("Uniq"));
    }

    @Test
    public void givenAppNameSort_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("sort");
        assertTrue(app.getClass().toString().endsWith("Sort"));
    }

    @Test
    public void givenAppNameHistory_thenCreateApp() {
        Application app = ApplicationFactory.getApplication("history");
        assertTrue(app.getClass().toString().endsWith("History"));
    }

}
