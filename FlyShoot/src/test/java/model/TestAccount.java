package model;

import com.avic.sever.game.model.AccountManager;
import com.codebroker.core.manager.JongoDBService;
import com.codebroker.util.PropertiesWrapper;
import jodd.props.Props;
import org.jongo.Jongo;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestAccount {
    static  Jongo jongo;
    @BeforeClass
    public static void setup() {
        Props props=new Props();
        props.setValue("mongodb.host","192.168.0.199");
        props.setValue("mongodb.port", "32774");
        props.setValue("mongodb.dbname","flyShoot");
        PropertiesWrapper configPropertieWrapper = new PropertiesWrapper(props);
        JongoDBService jongoDBService=new JongoDBService();
        jongoDBService.init(configPropertieWrapper);
        jongo=jongoDBService.getJongo();
    }

    @Test
    public void register(){
        assert AccountManager.getInstance().regeditAccount(jongo,"Test1","124567")==null;
        assert AccountManager.getInstance().regeditAccount(jongo,"Test2","124567")==null;

    }

    @Test
    public void check(){
        assert  AccountManager.getInstance().checkRegedit(jongo,"Test1");
        assert  AccountManager.getInstance().checkRegedit(jongo,"Test2");
    }

    @Test
    public void find(){
        assert  AccountManager.getInstance().selectAccount(jongo,"Test1","124567")!=null;
        assert  AccountManager.getInstance().selectAccount(jongo,"Test1","12456")==null;
    }

}
