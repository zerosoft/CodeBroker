package db.mongodb;

import com.codebroker.core.ContextResolver;
import com.codebroker.database.JongoDBService;
import org.bson.types.ObjectId;
import org.jongo.Find;
import org.jongo.MongoCursor;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.ArrayList;
import java.util.List;

/**
 * 性能存储
 */
public class ServerInfo {
    @MongoId // auto
    private String id;
    private int server_id;
    private String server_name;
    private int zone_num;
    private int room_num;
    private int user_num;
    private float cpu_now;
    private float memory_now;
    private long last_uptime;

    public static void del(ObjectId objectId) {
        JongoDBService manager = ContextResolver.getManager(JongoDBService.class);
        manager.getJongo().getCollection(ServerInfo.class.getSimpleName()).remove(objectId);
    }

    public static ServerInfo getById(String serverId) {
        JongoDBService manager = ContextResolver.getManager(JongoDBService.class);
        ServerInfo as = manager.getJongo().getCollection(ServerInfo.class.getSimpleName())
                .findOne(new ObjectId(serverId)).as(ServerInfo.class);
        return as;
    }

    public static ServerInfo getByServerId(String serverId) {
        JongoDBService manager = ContextResolver.getManager(JongoDBService.class);
        ServerInfo as = manager.getJongo().getCollection(ServerInfo.class.getSimpleName())
                .findOne("{serverId: #}", serverId).as(ServerInfo.class);
        return as;
    }

    public static List<ServerInfo> getAll() {
        JongoDBService manager = ContextResolver.getManager(JongoDBService.class);
        Find find = manager.getJongo().getCollection(ServerInfo.class.getSimpleName()).find();
        MongoCursor<ServerInfo> as = find.as(ServerInfo.class);
        List<ServerInfo> result = new ArrayList<>();
        while (as.hasNext()) {
            result.add(as.next());
        }
        return result;
    }

    public void insert() {
        JongoDBService manager = ContextResolver.getManager(JongoDBService.class);
        manager.getJongo().getCollection(ServerInfo.class.getSimpleName()).save(this);
    }

    public void update() {
        JongoDBService manager = ContextResolver.getManager(JongoDBService.class);
        manager.getJongo().getCollection(ServerInfo.class.getSimpleName()).update(this.id).with(this);
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public int getZone_num() {
        return zone_num;
    }

    public void setZone_num(int zone_num) {
        this.zone_num = zone_num;
    }

    public int getRoom_num() {
        return room_num;
    }

    public void setRoom_num(int room_num) {
        this.room_num = room_num;
    }

    public int getUser_num() {
        return user_num;
    }

    public void setUser_num(int user_num) {
        this.user_num = user_num;
    }

    public float getCpu_now() {
        return cpu_now;
    }

    public void setCpu_now(float cpu_now) {
        this.cpu_now = cpu_now;
    }

    public float getMemory_now() {
        return memory_now;
    }

    public void setMemory_now(float memory_now) {
        this.memory_now = memory_now;
    }

    public long getLast_uptime() {
        return last_uptime;
    }

    public void setLast_uptime(long last_uptime) {
        this.last_uptime = last_uptime;
    }
}
