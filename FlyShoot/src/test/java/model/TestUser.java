package model;

import com.avic.sever.game.model.UserEntity;
import com.avic.sever.game.model.UserManager;
import com.codebroker.database.JongoDBService;
import com.codebroker.util.PropertiesWrapper;
import jodd.props.Props;
import org.jongo.Jongo;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestUser {
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
        assert UserManager.getInstance().createUser(jongo,"Test1","124567")==null;
    }




    @Test
    public void find(){
        assert UserManager.getInstance().selectUserByUserId(jongo,"124567")!=null;
        assert UserManager.getInstance().selectUserByAccountId(jongo,"Test1")!=null;
    }

    @Test
    public  void update(){
        UserEntity entity = UserManager.getInstance().selectUserByUserId(jongo, "124567");
        entity.setLevel(2);
//        entity.getPlanes().add("F-23");
        UserManager.getInstance().update(jongo,entity);

        entity.setLevel(3);
        entity.getPlanes().remove("F-23");
        UserManager.getInstance().update(jongo,entity);
    }
}
