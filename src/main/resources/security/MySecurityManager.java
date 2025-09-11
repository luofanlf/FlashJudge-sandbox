

public class MySecurityManager extends SecurityManager{

    @Override
    public void checkExec(String cmd){
        throw new SecurityException("checkExec auth exception:"+cmd);
    }
    
}
